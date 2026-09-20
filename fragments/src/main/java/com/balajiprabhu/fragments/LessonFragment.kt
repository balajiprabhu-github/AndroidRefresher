package com.balajiprabhu.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class LessonFragment : Fragment() {
    private val title get() = requireArguments().getString(ARG_TITLE).orEmpty()
    private val description get() = requireArguments().getString(ARG_DESCRIPTION).orEmpty()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        log("onAttach: attached to Activity")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate: Fragment exists; View does not exist yet")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        log("onCreateView: creating this page's View")
        return inflater.inflate(R.layout.fragment_lesson, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log("onViewCreated: safe place to access Views")
        view.findViewById<TextView>(R.id.titleText).text = title
        view.findViewById<TextView>(R.id.descriptionText).text = description
    }

    override fun onStart() { super.onStart(); log("onStart: becoming visible") }
    override fun onResume() { super.onResume(); log("onResume: visible and interactive") }
    override fun onPause() { log("onPause: losing focus"); super.onPause() }
    override fun onStop() { log("onStop: no longer visible"); super.onStop() }

    override fun onDestroyView() {
        // The Fragment can survive while ViewPager2 destroys this View.
        // Clear bindings/listeners here in a real Fragment.
        log("onDestroyView: View destroyed; Fragment may survive")
        super.onDestroyView()
    }

    override fun onDestroy() { log("onDestroy: Fragment object destroyed"); super.onDestroy() }
    override fun onDetach() { log("onDetach: detached from Activity"); super.onDetach() }

    private fun log(message: String) = Log.d(LOG_TAG, "$title — $message")

    companion object {
        private const val LOG_TAG = "FragmentLesson"
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"

        fun newInstance(title: String, description: String) = LessonFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_DESCRIPTION, description)
            }
        }
    }
}
