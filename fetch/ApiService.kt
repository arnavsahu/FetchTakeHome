package com.arnav.fetch

import retrofit2.http.GET

//The model layer of our MVVM architecture
//interface to contain abstract methods representing API endpoints
interface ApiService {
    //define endpoint: tells retrofit to make http get request to hiring.json endpoint
    @GET("hiring.json")
    //getItems() method should be called within coroutine and should return a list of Item objects
    //retrofit + gson should parse the json into these Item objects to be returned
    //to be implemented by retrofit to make network requests and parse json
    //getItems() can pause the coroutine it called within (retrofit network request)
    suspend fun getItems(): List<Item>
}