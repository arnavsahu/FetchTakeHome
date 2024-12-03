package com.arnav.fetch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arnav.fetch.ui.theme.FetchTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.rotate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.foundation.layout.statusBarsPadding

//our view (UI) of MVVM architecture.
//observes the exposed items state from ViewModel and renders these items in a formatted way
class MainActivity : ComponentActivity() {
    //override onCreate with potential saved data from previous activity state
    override fun onCreate(savedInstanceState: Bundle?) {
        //call superclass onCreate from ComponentActivity() to initialize activity (null) or from saved instance data
        //sets up some basic parts of activity: connecting to android system, managing lifecycle, etc.
        super.onCreate(savedInstanceState)
        setContent {
            FetchTheme {
                FetchDataApp()
            }
        }
    }
}

//functions marked with composable can be used to build UI in Jetpack Compose
@Composable
//instantiate or get mainViewModel (MainViewModel class) through viewModel
//saves data during configuration changes (when view is rebuilt)
fun FetchDataApp(mainViewModel: MainViewModel = viewModel()) {
    //items val which "listens" to changes from the items shown to UI by MainViewModel
    //UI will recompose to reflect any data changes
    val items by mainViewModel.items
    //val errorMessage by mainViewMode.errorMessage

    MaterialTheme {
        Surface(
            //ensure surface takes up entire screen space
            //list was starting under status bar on screen, statusBarsPadding fixes this
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            //can add errorMessage check here which calls MainViewModel.fetchItems()
            //checks for items from http request and loads or passes to ItemList composable fun
            if (items.isNotEmpty()) {
                ItemList(items)
            } else {
                //container
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
//parameter: list of Item objets retrieved from mainViewModel
fun ItemList(items: List<Item>) {
    //mutable state map where key is listId and val is boolean (open/close)
    //remembers the boolean states of groups through other UI or data changes (ie screen refresh when another group is expanded)
    //mutable to track changes and recompose automatically (observable)
    val expandedGroups = remember { mutableStateMapOf<Int, Boolean>() }

    //scrollable list that only renders items visible on screen
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        //initialized val groupedItems map where key is listId and values are items
        val groupedItems = items.groupBy { it.listId }
        //iterate over each group
        groupedItems.forEach { (listId, itemsInList) ->
            item {
                //retrieve expanded state from expandedGroups
                val isExpanded = expandedGroups[listId] ?: false
                //display header for each listId group through ListIdHeader composable fun
                //contains id, state, and toggle switch as parameters
                ListIdHeader(
                    listId = listId,
                    isExpanded = isExpanded,
                    //onHeaderClick gives access to ListIdHeader to toggle the expanded state and change the group's expanded state
                    onHeaderClick = {
                        expandedGroups[listId] = !isExpanded
                    }
                )
            }
            //if group is expanded, render each item in group by calling ItemRow on each item to display item data
            if (expandedGroups[listId] == true) {
                items(itemsInList) { item ->
                    ItemRow(item)
                }
            }
        }
    }
}

@Composable
//responsible for handling the click action and displaying group header
fun ListIdHeader(
    listId: Int,
    isExpanded: Boolean,
    onHeaderClick: () -> Unit
) {
    //to animate arrow icon rotation based upon open/close state
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f, label = ""
    )
    //clickable to make header toggle expansion state
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            //changes the group's toggle state, which in turn causes our ItemList function to recompose
            .clickable { onHeaderClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
        ) {
            //display List ID val with weight so arrow icon is pushed to the left of header row
            Text(
                text = "List ID: $listId",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            //rotate icon based upon rotationAngle (expanded or collapsed)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.rotate(rotationAngle)
            )
        }
    }
}

@Composable
//display item name in full width padded row (Item object param)
fun ItemRow(item: Item) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        //if item.name isn't null (shouldn't be since this is prefiltered in MainViewModel
        //but our code doesn't know that so I use ? to bypass
        item.name?.let {
            //set text to item.name
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
