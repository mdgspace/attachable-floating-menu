package com.sdsmdg.rohan.testapp

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manager = fragmentManager
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> manager.beginTransaction().replace(R.id.container, GridFragment()).commit()
                    1 -> manager.beginTransaction().replace(R.id.container, SettingsFragment()).commit()
                }
            }

        })
        manager.beginTransaction().replace(R.id.container, GridFragment()).commit()

    }

}
