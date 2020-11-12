package com.freshlybuilt

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils.replace
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.freshlybuilt.Fragments.Feed
import com.freshlybuilt.Fragments.Login
import com.freshlybuilt.R.id.baseNavFrame
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.jar.Manifest


class Base : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)


        val fLogin = Login()
        val fFeed = Feed()
        //runtime permission check





        //logging and signing check
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail().build()
        val mGoogleSignInClient : GoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val account = GoogleSignIn.getLastSignedInAccount(this)

        //default screen
        Transaction(fLogin)


        // main fragment handler
        main_navigation.setOnNavigationItemSelectedListener {item ->
            when (item.itemId){
                R.id.nav_profile ->{
                    Transaction(fLogin)
                    true
                }
                R.id.nav_feed ->{
                    Transaction(fFeed)
                    true
                }
                R.id.nav_tag->{
                    true
                }
                else -> false
            }

        }

    }

    override fun onResume() {
        super.onResume()
        val permission : Boolean = permissionCheck()
        if (permission) {

        }
    }

    private fun permissionCheck(): Boolean {
        val pInternet: Boolean = checkSelfPermission(android
            .Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
        val pReadExtStorage: Boolean = checkSelfPermission(android
            .Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val pWriteExtStorage: Boolean = checkSelfPermission(android
            .Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        if (pInternet && pReadExtStorage && pWriteExtStorage) {
            return true
        }
        return false
    }

    private fun toast(status:Int){
        val warning: String = "test toast"
        val tTime : Int = Toast.LENGTH_LONG
        val toast : Toast = Toast.makeText(applicationContext,warning,tTime)
        toast.show()
    }

    private fun Transaction(fragment : Fragment){
        val fManager = supportFragmentManager
        val fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.baseFragFrame,fragment)
        fTransaction.commit()
    }
}
