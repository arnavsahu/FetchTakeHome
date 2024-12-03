package com.arnav.fetch

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//part of our model layer of mvvm
//Singleton OOP pattern
//ensures only one instance of this class exists that is globally accessible through object keyword
object RetrofitInstance {

    //private instance
    //delay initialization of retrofit until needed to save resources
    private val retrofit by lazy {
        Retrofit.Builder()
            //set base url for api to combine with get endpoint
            .baseUrl("https://fetch-hiring.s3.amazonaws.com/")
            //to convert json strings to kotlin objects using gson library
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //declare a public read only api var that creates an implementation of ApiService interface through retrofit
    //when instantiated and getItems() is called, retrofit handles the network request and return list of Item items through ApiService
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}