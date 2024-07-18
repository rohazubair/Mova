package com.example.myapplication

data class MovieDataModal(var rating: String = "", var imgUrl: String = "") {
    constructor() : this("", "")
}
