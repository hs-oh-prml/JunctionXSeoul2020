package com.example.junctionxseoul2020

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.example.junctionxseoul2020.data.Post
import com.example.junctionxseoul2020.data.PostManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.activity_navigation_drawer.*

class NavigationDrawerActivity : AppCompatActivity(), OnMapReadyCallback {

    val gson = Gson()

    lateinit var postManager: PostManager
    var lat = 37.541601
    var lng = 127.078838
    lateinit var locationSource: FusedLocationSource
    lateinit var naverMap: NaverMap
    lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 0
        )
        setContentView(R.layout.activity_navigation_drawer)

        hamburger.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }

        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient("4fdqbc9gq2")
        locationSource = FusedLocationSource(this, 0)

        val startIntent: Intent = Intent(this, StartLoading::class.java)
        startActivityForResult(startIntent, 764)
    }

    private fun init() {
        postManager = PostManager()

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivityForResult(loginIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 764) {
            init()
        }
        /*
        로그인 액티비티가 종료된 경우
        */
        else if (requestCode == 1) {
            if (data != null) {
                val vec = data.getStringArrayListExtra("postManager")
                for (i in vec) {
                    val post = gson.fromJson(i, Post::class.java)
                    postManager.posts.add(post)
                    //postManager.posts.add(gson.fromJson(i, Post::class.java))
                }
            }

            val fm = supportFragmentManager
            val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
                ?: MapFragment.newInstance().also {
                    fm.beginTransaction().add(R.id.map, it).commit()
                }

            mapFragment.getMapAsync(this)
        }
        /*
        게시글 작성 액티비티가 종료된 경우
        */
        else if (requestCode == 11) {
            if (data != null) {
                if (data.getBooleanExtra("isAdded", false)) {
                    val post = data.getSerializableExtra("post") as Post
                    Log.d("Log_POST", post.toString())
                    val temp = Marker()
                    temp.position = LatLng(post.uploadLat, post.uploadLng)
                    temp.onClickListener = Overlay.OnClickListener {
                        Log.e("onClick", post.pid)
                        val intent = Intent(this, PopupReadActivity::class.java)
                        intent.putExtra("post", post)
                        startActivityForResult(intent, 992)
                        true
                    }
                    temp.map = naverMap
                }
            }
        } else if (requestCode == 992) {
            if (data != null) {
                val post = data.getSerializableExtra("post") as Post
                for (i in postManager.posts) {
                    if (i.pid == post.pid)
                        i.comments = post.comments
                }
            }
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

    fun showMarker() {
        Log.d("LOG_POSTMANGER", postManager.posts.toString())
        for (post in postManager.posts) {
            val temp: Marker = Marker()
            temp.position = LatLng(post.uploadLat, post.uploadLng)
            temp.onClickListener = Overlay.OnClickListener {
                Log.e("onClick", post.pid)
                val intent: Intent = Intent(this, PopupReadActivity::class.java)
                intent.putExtra("post", post)
                startActivityForResult(intent, 992)
                true
            }
            temp.map = naverMap
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow


        fusedLocationClient = FusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            lat = it.latitude
            lng = it.longitude

            val overlay = CircleOverlay()
            overlay.center = LatLng(lat, lng)
            overlay.map = naverMap
            overlay.radius = 1000.0
            overlay.color = getColor(R.color.overlayColor)
        }

        val rdb = FirebaseDatabase.getInstance().getReference("post/")
        rdb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postManager.posts.clear()
                for (postSnapshot in snapshot.children) {
                    Log.d(TAG, "onChildAdded:" + postSnapshot.key!!)

                    val json = postSnapshot.value.toString()
                    postManager.posts.add(gson.fromJson(json, Post::class.java))
                    showMarker()
                }
            }
            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }
        })
    }

    fun onWritePostBtnClicked(view: View) {
        val intent: Intent = Intent(this, PopupWriteActivity::class.java)
        intent.putExtra("latitude", lat)
        intent.putExtra("longitude", lng)
        // 유저 id값을 알아내서 intent에 넣고 액티비티를 띄워야 한다.
        startActivityForResult(intent, 11)
    }

    companion object {
        private const val TAG = "TAG_REALTIME"
    }


    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.friends -> {
                true
            }
            R.id.notification ->{
                true
            }
            R.id.faq->{
                true
            }
            R.id.go_zepeto->{
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.addCategory(Intent.CATEGORY_BROWSABLE)
                intent.data = Uri.parse("zepeto://home")
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation_drawer, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
