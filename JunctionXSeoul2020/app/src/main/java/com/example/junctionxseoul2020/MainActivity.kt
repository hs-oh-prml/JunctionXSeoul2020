package com.example.junctionxseoul2020

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.junctionxseoul2020.data.Post
import com.example.junctionxseoul2020.data.PostManager
import com.example.junctionxseoul2020.data.UserManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var postManager: PostManager
    lateinit var userManager: UserManager
    var lat = 0.0
    var lng = 0.0
    lateinit var locationSource: FusedLocationSource
    lateinit var naverMap: NaverMap

    lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient("4fdqbc9gq2")
        locationSource = FusedLocationSource(this, 0)


        ActivityCompat.requestPermissions(
            MainActivity@ this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 0
        )

        setContentView(R.layout.activity_main)

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivityForResult(loginIntent, 1)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /*
        로그인 액티비티가 종료된 경우
        */
        if (requestCode == 1) {
            postManager = data?.getSerializableExtra("postManager") as PostManager
            userManager = data.getSerializableExtra("userManager") as UserManager
        }
        /*
        게시글 작성 액티비티가 종료된 경우
        */
        else if (requestCode == 11) {

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )
        ) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        /*
        디버깅 용도로 임시로 만든 코드임
        */
        // 시작
        val temp_vector: Vector<String> = Vector<String>()
        temp_vector.add("1")
        temp_vector.add("2")
        temp_vector.add("3")
        temp_vector.add("4")
        temp_vector.add("5")
        temp_vector.add("6")
        temp_vector.add("7")
        postManager = PostManager()
        postManager.posts.add(
            Post(
                "1",
                1,
                "1",
                "first post",
                "2020.10.10 12:13",
                37.543264,
                127.076049,
                temp_vector
            )
        )
        postManager.posts.add(
            Post(
                "2",
                2,
                "2",
                "second post",
                "2020.10.10 12:13",
                37.541953,
                127.079621,
                Vector<String>()
            )
        )
        postManager.posts.add(
            Post(
                "3",
                3,
                "3",
                "third post",
                "2020.10.10 12:13",
                37.542433,
                127.078807,
                Vector<String>()
            )
        )
        postManager.posts.add(
            Post(
                "4",
                4,
                "4",
                "fourth post",
                "2020.10.10 12:13",
                37.544261,
                127.076116,
                Vector<String>()
            )
        )
        // 종료

        val marker: Vector<Marker> = Vector<Marker>()
        for (post in postManager.posts) {
            val temp: Marker = Marker()
            temp.position = LatLng(post.uploadLat, post.uploadLng)
            temp.onClickListener = object : Overlay.OnClickListener {
                override fun onClick(p0: Overlay): Boolean {
                    Log.e("onClick", post.pID)
                    val intent: Intent = Intent(this@MainActivity, PopupReadActivity::class.java)
                    intent.putExtra("story", post.story)
                    intent.putExtra("uploadTime", post.uploadTime)
                    intent.putExtra("comments", post.comments)
                    startActivity(intent)
                    return true
                }

            }
            temp.map = naverMap
            marker.add(temp)
        }
    }

    fun onWritePostBtnClicked(view: View) {
        val intent: Intent = Intent(this, PopupWriteActivity::class.java)
        intent.putExtra("latitude", lat)
        intent.putExtra("longitude", lng)
        // 유저 id값을 알아내서 intent에 넣고 액티비티를 띄워야 한다.
        startActivityForResult(intent, 11)
    }

}