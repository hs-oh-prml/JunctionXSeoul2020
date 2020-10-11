package com.example.junctionxseoul2020

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.Image
import android.media.ImageReader
import android.net.Uri
import android.os.*
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.junctionxseoul2020.apiService.RetrofitService
import com.example.junctionxseoul2020.data.Post
import com.example.junctionxseoul2020.data.ZepetoRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_popup_write.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URI
import java.net.URLEncoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class PopupWriteActivity : FragmentActivity() {

    val myProgressBar: MyProgressBar = MyProgressBar()
    lateinit var postDB: DatabaseReference
    lateinit var userDB: DatabaseReference

    var storage = Firebase.storage

    lateinit var editText: EditText
    lateinit var zepetoImg: ImageView

    var latitude: Double = 0.0
    var longitude: Double = 0.0


    lateinit var retrofit: RetrofitService          // retrofit API manager
    lateinit var photoBoothList: ArrayList<String>  // photobooth id list
    lateinit var hashCode:String                    // user's hashcode

    val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA) // camera permision
    val FLAG_PERM_CAMERA = 98                                                // permission flag
    var cameraDevice: CameraDevice? = null
    lateinit var texture: SurfaceTexture
    lateinit var captureRequestBuilder: CaptureRequest.Builder
    var cameraCaptureSessions: CameraCaptureSession? = null
    var faceCamera = false
    var cameraId = ""
    lateinit var imageDimension:Size
    var map: StreamConfigurationMap? = null
    val stateCallback = object: CameraDevice.StateCallback() {
        override fun onOpened(p0: CameraDevice) {
//            TODO("Not yet implemented")
            cameraDevice = p0
            createCameraPreviewSession()
        }

        override fun onDisconnected(p0: CameraDevice) {
//            TODO("Not yet implemented")
            cameraDevice!!.close()
        }

        override fun onError(p0: CameraDevice, p1: Int) {
//            TODO("Not yet implemented")
            cameraDevice!!.close()
            cameraDevice = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lpWindow: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        lpWindow.dimAmount = 0.6f
        window.attributes = lpWindow

        setContentView(R.layout.activity_popup_write)

        editText = findViewById(R.id.editText1)
        zepetoImg = findViewById(R.id.zepetoImg)

        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)

        init()

        // initialize user's hash code
//        hashCode = "K4R33L"

        if(App.prefs.getUserHASHCODE() == null){
            hashCode = ""
        } else {
            hashCode = App.prefs.getUserHASHCODE()!!
        }
        Log.d("user_hash_code", hashCode)

        // load photobooth list from file 'data.txt'
        readFile()
        // retrofit object
        retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .baseUrl("https://render-api.zepeto.io/v2/")
            .build()
            .create(RetrofitService::class.java)
        // zepetoImg 이미지뷰에 이미지 업로드 시작
        ZepetoAPI()
        // zepetoImg 이미지뷰에 이미지 업로드 종료

    }

    fun init(){

        cameraOnBtn.setOnClickListener {
            if(checkPermission(CAMERA_PERMISSION, FLAG_PERM_CAMERA)){
                openCamera()
            } else {
                return@setOnClickListener
            }
        }
        refreshBtn.setOnClickListener {
            ZepetoAPI()
        }

        cameraShotBtn.setOnClickListener {
            closeCameraPreviewSession()
            closeCamera()
        }

        shoppingBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse("zepeto://home/shop/hair")
            startActivity(intent)
        }

    }
    fun openCamera(){
        Log.e("OpenCamera", "openCamera() method called")
        var manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try{
            cameraId = if(faceCamera){
                manager.cameraIdList[1]
            } else{
                manager.cameraIdList[0]
            }
            val characteristics = manager.getCameraCharacteristics(cameraId)
            map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            imageDimension = map!!.getOutputSizes<SurfaceTexture>(SurfaceTexture::class.java)[0]

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 100)
                return
            }
            manager.openCamera(cameraId!!, stateCallback, null)
        } catch (e: CameraAccessException){
            e.printStackTrace()
        }
    }
    fun createCameraPreviewSession(){
        Log.d("CameraPreviewSession", "Preview Start")
        try{
            texture = background_image.surfaceTexture
            texture.setDefaultBufferSize(zepetoImg.width, zepetoImg.height)
            var surfaces = ArrayList<Surface>()
            var surface = Surface(texture)
            captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)
            surfaces.add(surface)

            var width = 330;
            var height = 330;
            Log.d("Image_Size", "${width}, ${height}")
            var reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
            var readerListener = ImageReader.OnImageAvailableListener { p0 ->
                var image: Image? = null;
                try {
                    image = p0!!.acquireNextImage()
                    var buffer = image.planes[0].buffer
                    image.close()
                    buffer.clear()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            captureRequestBuilder.addTarget(reader.surface)
            surfaces.add(reader.surface)

            var backgroundThread = HandlerThread("streaming")
            backgroundThread.start()
            var backgroundHandler = Handler(backgroundThread.looper)
            reader.setOnImageAvailableListener(readerListener, backgroundHandler)

            cameraDevice!!.createCaptureSession(surfaces, object: CameraCaptureSession.StateCallback(){
                override fun onConfigureFailed(p0: CameraCaptureSession) {
                }
                override fun onConfigured(p0: CameraCaptureSession) {
                    if(cameraDevice == null){
                        return
                    }
                    cameraCaptureSessions = p0
                    updatePreview()
                }
            }, null)
        } catch(e:CameraAccessException){
            e.printStackTrace()
        }
    }
    fun updatePreview(){
        if(cameraDevice == null){
            Log.d("Update_Preview","Error: Camera Device is null")
            return
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        var thread = HandlerThread("preview")
        thread.start()
        var bgHandler = Handler(thread.looper)
        try{
            cameraCaptureSessions!!.setRepeatingRequest(captureRequestBuilder.build(), null, bgHandler)
//            takePicture()
        }catch (e: CameraAccessException){
            e.printStackTrace()
        }

    }
    fun closeCameraPreviewSession(){
        if(cameraCaptureSessions != null){
            cameraCaptureSessions!!.close()
            cameraCaptureSessions = null
        }
    }
    fun closeCamera(){
        if(null != cameraDevice){
            cameraDevice!!.close()
            cameraDevice = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        closeCameraPreviewSession()
        closeCamera()
    }

    override fun onPause() {
        super.onPause()
        closeCameraPreviewSession()
        closeCamera()
    }
    fun checkPermission(permissions: Array<out String>, flag: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions,  flag)
                    return false
                }
            }
        }
        return true
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            FLAG_PERM_CAMERA -> {
                for (grnat in grantResults) {
                    if (grnat != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "카메라 권한을 승인해야지만 카메라를 사용할 수 있습니다.", Toast.LENGTH_LONG).show()
                    } else {
                        openCamera()
                    }
                }
            }
        }
    }
    fun readFile(){
        photoBoothList = ArrayList()
//        Thread(Runnable {
//
//            var inputStream = resources.openRawResource(R.raw.data);
//            var scan = Scanner(inputStream)
//            try {
//                while (scan.hasNext()) {
//                    var line = scan.nextLine()
//                    photoBoothList.add(line)
////                    Log.d("PHOTO_BOOTH", line)
//                }
//            } catch (e:IOException) {
//                e.printStackTrace()
//            }
//            scan.close()
//        }).start()


        var inputStream = resources.openRawResource(R.raw.data);
        var scan = Scanner(inputStream)
        try {
            while (scan.hasNext()) {
                var line = scan.nextLine()
                photoBoothList.add(line)
//                    Log.d("PHOTO_BOOTH", line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        scan.close()

    }

    // function: Call Zepeto Rendering API
    @SuppressLint("CheckResult")
    fun ZepetoAPI() {
//        photoBoothList.shuffle()
        myProgressBar.progressON(this@PopupWriteActivity,null)
        var photobooth_id = photoBoothList.random()
        var body = ZepetoRequest(
            "booth",
            400,
            ZepetoRequest.hashCodes(arrayListOf(hashCode))
        )

//        var jsonObj = JSONObject()
//        jsonObj.put("type", "booth")
//        jsonObj.put("width", "800")
//        var strArr = ArrayList<String>()
//        strArr.add("K4R33L")
//        var jsonArr = JSONArray(strArr)
//        var jsonO = JSONObject()
//        jsonO.put("hashCodes", jsonArr)
//        jsonObj.put("target", jsonO)

        retrofit
            .zepetoAPI(photobooth_id, "application/json", body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("Response", it.toString())
                var url = it.url
                runOnUiThread {

                    Glide.with(applicationContext).load(url).into(zepetoImg)
                    myProgressBar.progressOFF()
                }
            }, {
                Log.v("Fail", "")
            })


    }

    fun createOkHttpClient(): OkHttpClient {        // monitoring HTTP log
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()
    }

    fun onCloseBtnClicked(view: View) {
        val intent: Intent = Intent()
        intent.putExtra("isAdded", false)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    // upload image on firebase storage
    fun uploadImage(pID: String, uID: String, story: String, formatted: String) {

        // Create a storage reference from our app
        var storageRef = storage.reference
        //var imagesRef: StorageReference? = storageRef.child("images")
        var imageRef = storageRef.child("images/${pID}.jpg")
        // frame
        // Get the data from an ImageView as bytes
      
        // var bitmap = Bitmap.createBitmap(zepetoImg.width, zepetoImg.height, Bitmap.Config.ARGB_8888)
//      var bitmap = (zepetoImg.drawable as BitmapDrawable).bitmap

        var bitmap:Bitmap
        var canvas:Canvas
        var baos:ByteArrayOutputStream
        var data: ByteArray

//        bitmap = Bitmap.createBitmap(frame_layout.width, frame_layout.height, Bitmap.Config.ARGB_8888)
        bitmap = background_image.bitmap
        canvas = Canvas(bitmap)
        zepetoImg.draw(canvas)
        baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        data = baos.toByteArray()
        var uploadTask = imageRef.putBytes(data)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                Log.d("LOG_URL", downloadUri.toString())
            } else {
                // Handle failures
                // ...
            }
        }.addOnSuccessListener {
            val url = URLEncoder.encode(it.toString(), "utf-8")
            val item = Post(pID, url, uID, story, formatted, latitude, longitude, null)
            postDB.child("/$pID").setValue(item)
            userDB = FirebaseDatabase.getInstance().getReference("user/$uID")
            userDB.child("/pID").setValue(pID)


            val intent: Intent = Intent()
            intent.putExtra("post", item)
            intent.putExtra("isAdded", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }


    }

    fun onSubmitPostBtnClicked(view: View) {
        if(editText1.text.length==0)
            Toast.makeText(this,"스토리를 입력하세요",Toast.LENGTH_SHORT).show()
        else {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")
            val formatted = current.format(formatter)
            Log.e("now time", formatted)

            var story: String = editText.text.toString().trim()
            story = URLEncoder.encode(story, "utf-8")

            /*
        zepetoImg 이미지뷰에 업로드 되어있는 이미지를 가져오고,
        formatted : 업로드시간, story : 이야기, 제페토 이미지, 업로드 할 때의 latitude, longitude
        총 5가지의 데이터를 DB에 저장해야 한다.
        */
            // DB에 저장하는 코드 시작

            val uID = App.prefs.getUserUID()!!
            postDB = FirebaseDatabase.getInstance().getReference("post")
            val pID = postDB.push().key!!
            uploadImage(pID, uID, story, formatted)

            //val post = Post(pID,url,uID,story,formatted,latitude,longitude,null)


            // DB에 저장하는 코드 종료
        }
    }

    fun onCameraOnBtnClicked(view: View) {

    }

    fun onRefreshBtnClicked(view: View) {

    }

    fun onShoppingBtnClicked(view: View) {

    }

    fun onCameraShotBtnClicked(view: View) {

    }
}
