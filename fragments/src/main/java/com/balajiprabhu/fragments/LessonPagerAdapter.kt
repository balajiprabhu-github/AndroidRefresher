package com.balajiprabhu.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/** Maps each ViewPager2 position to a Fragment. */
class LessonPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment = LessonFragment.newInstance(
        "Fragment page ${position + 1}",
        when (position) {
            0 -> "Swipe between pages and watch the lifecycle in Logcat."
            1 -> "A Fragment can survive after its View is destroyed."
            else -> "Clear view references in onDestroyView to avoid leaks."
        }
    )
}
