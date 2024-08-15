package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var btnSignIn : Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSignIn = view.findViewById(R.id.btnSignIn)
        btnSignIn.setOnClickListener { parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, NavigationFragment())
            .commit() }
    }

}