package com.sdsmdg.rohan.testapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sdsmdg.rohan.attachablefloatingmenu.FloatingMenuManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.activity_main, null)
        setContentView(view)
        FloatingMenuManager(this, view)
    }
}
