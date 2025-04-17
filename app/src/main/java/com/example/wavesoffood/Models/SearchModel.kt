package com.example.wavesoffood.Models

class SearchModel {

    var foodprice:String=""
    var foodimage:Int = 0
    var foodname: String =""
    var description:String=""
    lateinit var ingredients:ArrayList<String>



    constructor(foodprice: String, foodimage: Int, foodname: String) {
        this.foodprice = foodprice
        this.foodimage = foodimage
        this.foodname = foodname
    }


    constructor()
    constructor(
        foodprice: String,
        foodimage: Int,
        foodname: String,
        description: String,
        ingredients: ArrayList<String>

    ) {
        this.foodprice = foodprice
        this.foodimage = foodimage
        this.foodname = foodname
        this.description = description
        this.ingredients=ingredients
    }
}