package com.freshlybuilt

import android.content.pm.PackageManager
import android.graphics.Color.YELLOW
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.freshlybuilt.Data.Preference
import com.freshlybuilt.Fragments.Feed
import com.freshlybuilt.Fragments.Profile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.activity_base.*


class Base : AppCompatActivity() {
    private val fLogin = Login()
    private val fFeed = Feed()
    private val fProfile = Profile()

    lateinit var activeFragment : Fragment
    private val fManager = supportFragmentManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        activeFragment = fFeed

        fManager.beginTransaction().apply {
            add(R.id.baseFragFrame,fFeed,"Feed").show(fFeed)
            add(R.id.baseFragFrame,fProfile,"Feed").hide(fProfile)
            //add(R.id.baseFragFrame,fFeed,"Feed").show(fFeed)
        }.commit()

        // main fragment handler
        main_navigation.menu.getItem(1).setChecked(true)
        main_navigation.setOnNavigationItemSelectedListener {item ->
            when (item.itemId){
                R.id.nav_profile ->{
                    fManager.beginTransaction().hide(activeFragment).show(fProfile).commit()
                    activeFragment = fProfile
                    true
                }
                R.id.nav_feed ->{
                    fManager.beginTransaction().hide(activeFragment).show(fFeed).commit()
                    activeFragment = fFeed
                    true
                }
                R.id.nav_tag->{
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!Preference(this).rememberMe()){
            Preference(this).sessionClose()
        }
    }


    private fun toast(status:Int){
        val warning: String = "test toast"
        val tTime : Int = Toast.LENGTH_LONG
        val toast : Toast = Toast.makeText(applicationContext,warning,tTime)
        toast.show()
    }

    fun transaction(fragment : Fragment){
        val fManager = supportFragmentManager
        val fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.baseFragFrame,fragment).addToBackStack("backStack")
        fTransaction.commit()
    }




}
