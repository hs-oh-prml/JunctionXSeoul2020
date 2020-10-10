package com.example.junctionxseoul2020.data

import java.io.Serializable
import java.util.*

data class Post(
    val pID: String,
    val img: String,
    val uID: String,
    val story: String,
    val uploadTime: String,
    val uploadLat: Double,
    val uploadLng: Double,
    val comments: Vector<String>?
): Serializable