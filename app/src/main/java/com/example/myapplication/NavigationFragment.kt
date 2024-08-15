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
                    loadFragment(homeFragment!!)
                    true
                }
                R.id.explore -> {
                    if (exploreFragment == null) {
                        exploreFragment = ExploreFragment()
                    }
                    loadFragment(exploreFragment!!)
                    true
                }
                R.id.mylist -> {
                    if (myListFragment == null) {
                        myListFragment = MyListFragment()
                    }
                    loadFragment(myListFragment!!)
                    true
                }
                else -> false
            }
        }

        // Load the default fragment
        if (savedInstanceState == null) {
            homeFragment = HomeFragment()
            loadFragment(homeFragment!!)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view2, fragment)
            .commit()
    }

}