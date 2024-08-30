package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class NavigationFragment : Fragment(R.layout.fragment_navigation) {

    private var homeFragment: HomeFragment? = null
    private var exploreFragment: ExploreFragment? = null
    private var myListFragment: MyListFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    if (homeFragment == null) {
                        homeFragment = HomeFragment()
                    }
                    loadFragment(homeFragment!!, "HomeFragment")
                    true
                }
                R.id.explore -> {
                    if (exploreFragment == null) {
                        exploreFragment = ExploreFragment()
                    }
                    loadFragment(exploreFragment!!, "ExploreFragment")
                    true
                }
                R.id.mylist -> {
                    if (myListFragment == null) {
                        myListFragment = MyListFragment()
                    }
                    loadFragment(myListFragment!!, "MyListFragment")
                    true
                }
                else -> false
            }
        }

        // Load the default fragment
        if (savedInstanceState == null) {
            homeFragment = HomeFragment()
            loadFragment(homeFragment!!, "HomeFragment")
        }
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        parentFragmentManager.beginTransaction().apply {
            if (parentFragmentManager.findFragmentByTag(tag) != null) {
                show(parentFragmentManager.findFragmentByTag(tag)!!)
            } else {
                add(R.id.fragment_container_view2, fragment, tag)
            }

            homeFragment?.let {
                if (it != fragment) hide(
                    parentFragmentManager.findFragmentByTag(
                        "HomeFragment"
                    )!!
                )
            }
            exploreFragment?.let {
                if (it != fragment) hide(
                    parentFragmentManager.findFragmentByTag(
                        "ExploreFragment"
                    )!!
                )
            }
            myListFragment?.let {
                if (it != fragment) hide(
                    parentFragmentManager.findFragmentByTag(
                        "MyListFragment"
                    )!!
                )
            }
            commit()
        }
    }
}