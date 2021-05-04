package com.example.startracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class HowToUseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_how_to_use, container, false)
    }

    //Make the menu back button return for the previous screen and not return for the home screen as its default
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val fm: FragmentManager = parentFragmentManager
                if (fm.backStackEntryCount > 0) {
                    fm.popBackStack()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}