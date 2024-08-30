package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var btnSignIn : Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSignIn = view.findViewById(R.id.btnSignIn)
        btnSignIn.setOnClickListener { parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, NavigationFragment(), "NavigationFragment")
            .commit() }
    }

}