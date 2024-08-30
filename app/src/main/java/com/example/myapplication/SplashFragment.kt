package com.example.myapplication

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rotatingImageView: ImageView = view.findViewById(R.id.imageViewFrame)

        val rotateAnimation = ObjectAnimator.ofFloat(rotatingImageView, "rotation", 0f, 360f)
        rotateAnimation.duration = 1000
        rotateAnimation.repeatCount = ObjectAnimator.INFINITE
        rotateAnimation.repeatMode = ObjectAnimator.RESTART
        rotateAnimation.start()

        viewLifecycleOwner.lifecycleScope.launch {
            delay(2000)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, LoginFragment())
                .commit()
        }
    }

}