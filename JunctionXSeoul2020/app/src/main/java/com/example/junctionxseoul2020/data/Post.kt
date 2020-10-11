package com.example.junctionxseoul2020.data

import java.io.Serializable
import java.util.*

/*
    uploadTime -> YYYY.MM.DD HH:mm
*/

class Post(
    val pid: String,
    val img: String,
    val uid: String,
    var story: String?,
    val uploadTime: String,
    val uploadLat: Double,
    val uploadLng: Double,
    var comments: ArrayList<String>?
) : Serializable
{
    constructor(): this("pid", "img", "uid", null, "time", 37.541601, 127.078838, null){}
}
