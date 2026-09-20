package com.balajiprabhu.fragments

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

/** Hosts ViewPager2, which displays one Fragment per page. */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "MainActivity.onCreate")
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        // FragmentStateAdapter creates, saves, and restores Fragment pages.
        viewPager.adapter = LessonPagerAdapter(this)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.d(LOG_TAG, "ViewPager2.onPageSelected: position=$position")
            }
        })
    }

    private companion object {
        const val LOG_TAG = "FragmentLesson"
    }
}
