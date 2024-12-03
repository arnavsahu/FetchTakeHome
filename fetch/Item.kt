package com.arnav.fetch

//data class to define structure of each Json object as a data class
//part of model in mvvm
data class Item(
    val id: Int,
    val listId: Int,
    val name: String?,
    //price, rating
    val price: Float,
    val rating: Int
)
