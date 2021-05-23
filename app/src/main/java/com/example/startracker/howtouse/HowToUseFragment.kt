package com.example.startracker.howtouse

import CustomExpandableListAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.startracker.R
import com.example.startracker.databinding.FragmentHowToUseBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.set


class HowToUseFragment : Fragment() {

    private lateinit var binding: FragmentHowToUseBinding

    lateinit var faq1:String
    lateinit var faq2:String
    lateinit var faq3:String
    lateinit var faq4:String
    lateinit var faqAns1:String
    lateinit var faqAns2:String
    lateinit var faqAns3:String
    lateinit var faqAns4:String

    val data: HashMap<String, List<String>>
        get() {
            val expandableListDetail =
                HashMap<String, List<String>>()

            //child
            val ans1: MutableList<String> = java.util.ArrayList()
            ans1.add(faqAns1)
            val ans2: MutableList<String> = java.util.ArrayList()
            ans2.add(faqAns2)
            val ans3: MutableList<String> = java.util.ArrayList()
            ans3.add(faqAns3)
            val ans4: MutableList<String> = java.util.ArrayList()
            ans4.add(faqAns4)

            expandableListDetail[faq1] = ans1
            expandableListDetail[faq2] = ans2
            expandableListDetail[faq3] = ans3
            expandableListDetail[faq4] = ans4
            return expandableListDetail
        }

    private var expandableListView: ExpandableListView? = null
    private var adapter: ExpandableListAdapter? = null
    private var titleList: List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_how_to_use, container, false
        )

        faq1 = resources.getString(R.string.faq1)
        faq2 = resources.getString(R.string.faq2)
        faq3 = resources.getString(R.string.faq3)
        faq4 = resources.getString(R.string.faq4)

        faqAns1 = resources.getString(R.string.faq_ans1)
        faqAns2 = resources.getString(R.string.faq_ans2)
        faqAns3 = resources.getString(R.string.faq_ans3)
        faqAns4 = resources.getString(R.string.faq_ans4)

        expandableListView = binding.expandableListView

        if (expandableListView != null) {
            val listData = data
            val list: List<String> = listOf(faq1,faq2,faq3,faq4)
            titleList = ArrayList(list)
            adapter = CustomExpandableListAdapter(
                requireContext(),
                titleList as ArrayList<String>,
                listData
            )
            expandableListView!!.setAdapter(adapter)
            expandableListView!!.setOnGroupExpandListener { groupPosition ->
            }
            expandableListView!!.setOnGroupCollapseListener { groupPosition ->
            }
            expandableListView!!.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
                if(groupPosition == 3-1){
                    val uri: Uri =
                        Uri.parse("https://www.ngdc.noaa.gov/geomag/calculators/magcalc.shtml?") // missing 'http://' will cause crashed
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
                true
            }
        }
        return binding.root
    }

    //Make the menu back button return for the previous screen and
    // not return for the home screen as its default
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