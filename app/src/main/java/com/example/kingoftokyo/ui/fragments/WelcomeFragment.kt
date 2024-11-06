package com.example.kingoftokyo.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kingoftokyo.R

class WelcomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.welcome_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("WelcomeFragment", "onViewCreated called")

        val startButton: Button = view.findViewById(R.id.startButton)

        startButton.setOnClickListener {
            Log.d("WelcomeFragment", "Start Button Clicked")
            findNavController().navigate(R.id.action_welcomeFragment_to_pickMonsterFragment)
        }
    }
}
