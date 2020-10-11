package com.example.junctionxseoul2020

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
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
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.*
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.android.synthetic.main.activity_navigation_drawer.*
import java.io.InputStream
import java.net.URL
import java.net.URLDecoder

class NavigationDrawerActivity : AppCompatActivity(), OnMapReadyCallback {

    val myProgressBar: MyProgressBar = MyProgressBar()
    val gson = Gson()
    var clat:Double = 0.0
    var clng:Double = 0.0
    var dis:Int = 1000
    val ref = this
    lateinit var postManager: PostManager
    var lat = 37.541601
    var lng = 127.078838
    lateinit var locationSource: FusedLocationSource
    lateinit var naverMap: NaverMap
    lateinit var fusedLocationClient: FusedLocationProviderClient

    val overlay = CircleOverlay()
    var markers = ArrayList<Marker>()

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

        disBtn_50km.setOnClickListener {
            Log.d("LOG_SWITCH_50km", markers.toString())
            overlay.radius = 50000.0
            val cameraUpdate = CameraUpdate.zoomTo(7.0).animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)
            //showMarker(locationSource.lastLocation!!.latitude, locationSource.lastLocation!!.longitude, 50000)    //50km

            clat = locationSource.lastLocation!!.latitude
            clng = locationSource.lastLocation!!.longitude
            dis = 50000

//
//            val asyncTask=AsyncTaskClass()
//            asyncTask.execute()
            showMarker()
        }

        disBtn_5km.setOnClickListener {
            Log.d("LOG_SWITCH_5km", markers.toString())
            overlay.radius = 5000.0
            val cameraUpdate = CameraUpdate.zoomTo(12.0).animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)
            //showMarker(locationSource.lastLocation!!.latitude, locationSource.lastLocation!!.longitude, 5000)    //5km
            clat = locationSource.lastLocation!!.latitude
            clng = locationSource.lastLocation!!.longitude
            dis = 5000


//            val asyncTask=AsyncTaskClass()
//            asyncTask.execute()

            showMarker()
        }

        disBtn_1km.setOnClickListener {
            Log.d("LOG_SWITCH_1km", markers.toString())
            overlay.radius = 1000.0
            val cameraUpdate = CameraUpdate.zoomTo(14.0).animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)
            //showMarker(locationSource.lastLocation!!.latitude, locationSource.lastLocation!!.longitude, 1000)    //5km
            clat = locationSource.lastLocation!!.latitude
            clng = locationSource.lastLocation!!.longitude
            dis = 1000
//            val asyncTask=AsyncTaskClass()
//            asyncTask.execute()

            showMarker()
        }

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
                    markers.add(temp)
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
        /*
    inner class AsyncTaskClass: AsyncTask<Void, Void, String>(){


        val posts = ArrayList<Post>()
            //postManager.posts
        val icons = ArrayList<Bitmap>()

        override fun onPreExecute() {


            myProgressBar.progressON(this@NavigationDrawerActivity,null)
            Log.d("LOGD","async")
            super.onPreExecute()
            for(marker in markers){
                marker.isVisible=false
                marker.map = null
            }
            markers.clear()

            for (post in postManager.posts) {
                val temp: Marker = Marker()
                temp.position = LatLng(post.uploadLat, post.uploadLng)
                val distance = calcDistance(post.uploadLat, post.uploadLng, clat, clng)
                if(distance > dis){
                    temp.map = null
                }
                else{
                    posts.add(post)
                    temp.onClickListener = Overlay.OnClickListener {
                        Log.e("onClick", post.pid)
                        val intent: Intent = Intent(ref, PopupReadActivity::class.java)
                        intent.putExtra("post", post)
                        startActivityForResult(intent,992)
                        true
                    }
                    temp.map = naverMap
//                  temp.captionColor = Color.parseColor("#808ade")
                    temp.icon = MarkerIcons.LIGHTBLUE
                    markers.add(temp)
                }
            }
        }

        override fun doInBackground(vararg params: Void?): String? {

            for(i in markers.indices){
                val post = posts[i]
                val marker = markers[i]
                val url = URLDecoder.decode(post.img)
                var bitmap = BitmapFactory.decodeStream((URL(url).content) as InputStream)

                var bitmaptmp = Bitmap.createScaledBitmap(bitmap,bitmap.width/2,bitmap.height/2,true)

                val borderSize =10
                val bitmapWithBorder = Bitmap.createBitmap(bitmap.width + borderSize * 2, bitmap.height + borderSize * 2, bitmap.config)
                val canvas = Canvas(bitmapWithBorder)
                canvas.drawColor(Color.argb(255,124,151,194))
                canvas.drawBitmap(bitmap, borderSize.toFloat(), borderSize.toFloat(), null)
                icons.add(bitmapWithBorder)
            }
            return "확인"
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)

        }

        override fun onPostExecute(result: String?) {

            super.onPostExecute(result)
            for(i in markers.indices){
                val groundOverlay = GroundOverlay()
                groundOverlay.bounds = LatLngBounds(LatLng(posts[i].uploadLat, posts[i].uploadLng), LatLng(posts[i].uploadLat+0.0035, posts[i].uploadLng+0.004))
                groundOverlay.setImage(OverlayImage.fromBitmap(icons[i]))
                groundOverlay.map=naverMap
                //markers[i].icon = OverlayImage.fromBitmap(icons[i])
            }

            myProgressBar.progressOFF()
        }
    }


*/
//
    fun showMarker() {
        Log.d("LOG_POSTMANGER", postManager.posts.toString())
        markers.clear()
        for (post in postManager.posts) {
            val temp = Marker()
            temp.position = LatLng(post.uploadLat, post.uploadLng)
            val distance = calcDistance(post.uploadLat, post.uploadLng, clat, clng)
            if (distance > dis) {
                temp.map = null
            } else {
                temp.onClickListener = Overlay.OnClickListener {
                    Log.e("onClick", post.pid)
                    val intent: Intent = Intent(this, PopupReadActivity::class.java)
                    intent.putExtra("post", post)
                    startActivityForResult(intent, 992)
                    true
                }
                temp.map = naverMap
                markers.add(temp)
            }
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

            overlay.center = LatLng(lat, lng)
            overlay.map = naverMap
            overlay.radius = 1000.0         //1000m
            overlay.color = getColor(R.color.overlayColor)
            overlay.globalZIndex=-400000
        }

        val rdb = FirebaseDatabase.getInstance().getReference("post/")
        rdb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postManager.posts.clear()
                for (postSnapshot in snapshot.children) {
                    Log.d(TAG, "onChildAdded:" + postSnapshot.key!!)

                    val json = postSnapshot.value.toString()
                    postManager.posts.add(gson.fromJson(json, Post::class.java))


                    //showMarker(locationSource.lastLocation!!.latitude, locationSource.lastLocation!!.longitude, 1000)
                }

                clat = locationSource.lastLocation!!.latitude
                clng = locationSource.lastLocation!!.longitude
//                val asyncTask=AsyncTaskClass()
//                asyncTask.execute()
                showMarker()
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


    fun calcDistance(lat1:Double, lng1:Double, lat2:Double, lng2:Double): Double {  // generally used geo measurement function
        val R = 6378.137; // Radius of earth in KM
        val dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        val dLon = lng2 * Math.PI / 180 - lng1 * Math.PI / 180;
        val a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                Math.sin(dLon/2) * Math.sin(dLon/2);
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        val d = R * c;
        return d * 1000; // meters
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
