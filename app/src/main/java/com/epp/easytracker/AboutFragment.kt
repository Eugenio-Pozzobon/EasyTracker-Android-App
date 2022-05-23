package com.epp.easytracker

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //This fragment is just texts on screen with no buttons. Just inflate.
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onResume() {
        super.onResume()
        // this allow user click in web links inside textView
        requireView().findViewById<TextView>(R.id.about_text)
            .movementMethod = LinkMovementMethod.getInstance()
    }

}


