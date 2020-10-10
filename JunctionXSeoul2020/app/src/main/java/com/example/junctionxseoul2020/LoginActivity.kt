package com.example.junctionxseoul2020

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import com.example.junctionxseoul2020.data.PostManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    val handler :mHandler = mHandler()
    val postManager: PostManager = PostManager(mHandler())
    var isThreadEnd = false
    var isThreadEnd_post = false


    val myProgressBar: MyProgressBar = MyProgressBar()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        googleSignIn()
    }

    fun endActivity() {
        myProgressBar.progressOFF()
        val intent: Intent = Intent(this,MainActivity::class.java)
        intent.putExtra("postManager", postManager.vec)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    inner class mHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val bundle: Bundle = msg.data
            if (!(bundle.isEmpty)) {
                if (bundle.containsKey("isThreadEnd")) {
                    isThreadEnd = bundle.getBoolean("isThreadEnd")
                }
                if (bundle.containsKey("isThreadEnd_post")) {
                    isThreadEnd_post = bundle.getBoolean("isThreadEnd_post")
                }

                if (isThreadEnd && isThreadEnd_post) {
                    endActivity()
                }
            }
        }
    }

    inner class mThread: Thread() {
        override fun run() {
            val message: Message = handler.obtainMessage()
            val bundle: Bundle = Bundle()

            postManager.readPost()


            bundle.putBoolean("isThreadEnd", true)
            message.data = bundle
            handler.sendMessage(message)
        }
    }

    // 구글 로그인
    private fun googleSignIn(){
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //구글 로그인 버튼
        loginBtn.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e)
            }
        } else if (requestCode == RC_SIGN_UP) {
            myProgressBar.progressON(this@LoginActivity, null)
            val thread: mThread = mThread()
            thread.start()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser
                    //updateUI(user)

                    val rdb = FirebaseDatabase.getInstance().getReference("user")
                    rdb.child("/${user?.uid}")
                        .addListenerForSingleValueEvent( object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
//                                TODO("Not yet implemented")
                            }
                            override fun onDataChange(p0: DataSnapshot) {
                                val userCheck = p0.value
                                if (userCheck == null) {
                                    val intent = Intent(this@LoginActivity, HashCodeActivity::class.java)
                                    startActivityForResult(intent, RC_SIGN_UP)
                                } else {
                                    myProgressBar.progressON(this@LoginActivity, null)
                                    val thread: mThread = mThread()
                                    thread.start()
                                }
                            }
                        })
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    //Snackbar.make(view, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }

    companion object {
        private const val RC_SIGN_IN = 123
        private const val RC_SIGN_UP = 125
    }
}