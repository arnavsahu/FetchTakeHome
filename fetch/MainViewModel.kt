package com.arnav.fetch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf

//our ViewModel layer in MVVM architecture
//fetches data from api, processes data, holds processed data as an observable state for UI view component (MainActivity.kt)
class MainViewModel : ViewModel() {

    //private mutable state variable that holds list of Item objects
    private val _items = mutableStateOf<List<Item>>(emptyList())

    //public read only version (State is a read only interface) of _items meant to be exposed to UI
    val items: State<List<Item>> = _items

    //run fetchItems() immediately when MainViewModel class is created
    //loads items immediately so we don't have to manually call fetchItems() in the UI
    init {
        fetchItems()
    }

    //error message for data fetch failure
    //can show in UI and try fetching data again if it fails
    private val _errorMessage = mutableStateOf<String?>(null)

    private val stateOf = derivedStateOf {  }

    //make async call to fetch data using retrofit
    private fun fetchItems() {
        //coroutine scope which allows us to make network request outside of main thread to keep UI smooth and responsive
        //tied to this model's lifecycle. Network request should be stopped when viewModel is cleared/destroyed
        //routine will suspend until retrofit response is returned to free resources in thread
        viewModelScope.launch {
            try {
                //retrofit network request get list of Item objects (suspending fun)
                val response = RetrofitInstance.api.getItems()
                processData(response)
                //_errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load data: ${e.message}"
            }
        }
    }

    //main filtering and sorting logic performs in coroutine once network request is successful
    private fun processData(items: List<Item>) {
        //filter blank or null name fields
        val filteredItems = items.filter { !it.name.isNullOrBlank() }
        //group items by listId then sort the groups by listId (map key is listId)
        val groupedItems = filteredItems.groupBy { it.listId }
            .toSortedMap()
        //sort list of items in each group then combines each group back into single list
        val sortedItems = groupedItems.flatMap { entry ->
            //parses the number in name and sorts by that
            entry.value.sortedBy {
                it.name?.substringAfter("Item ")?.toInt()
            }
        }
        //store final list organized by id and order of name in mutable _items
        _items.value = sortedItems
    }

    private fun processDataByPrice(items: List<Item>) {
        val filteredItems = items.filter { !it.name.isNullOrBlank() }
        //group items by listId then sort the groups by listId (map key is listId)
        val groupedItems = filteredItems.groupBy { it.listId }
            .toSortedMap()
        val sortedItems = groupedItems.flatMap { entry ->
            //parses the number in name and sorts by that
            entry.value.sortedBy {
                it.price
            }
        }
        _items.value = sortedItems
    }
}