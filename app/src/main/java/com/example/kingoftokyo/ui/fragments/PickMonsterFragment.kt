package com.example.kingoftokyo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kingoftokyo.R

class PickMonsterFragment : Fragment() {
    var selectedMonster: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pick_monster_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nextButton: Button = view.findViewById(R.id.nextButton)
        val monster1: LinearLayout = view.findViewById(R.id.monster1)
        val monster2: LinearLayout = view.findViewById(R.id.monster2)
        val monster3: LinearLayout = view.findViewById(R.id.monster3)
        val monster4: LinearLayout = view.findViewById(R.id.monster4)

        monster1.setOnClickListener {
            // Update selectedMonster value and UI
            selectedMonster = 0
            setSelectedMonster(monster1, monster2, monster3, monster4)
        }

        monster2.setOnClickListener {
            selectedMonster = 1
            setSelectedMonster(monster2, monster1, monster3, monster4)
        }

        monster3.setOnClickListener {
            selectedMonster = 2
            setSelectedMonster(monster3, monster1, monster2, monster4)
        }

        monster4.setOnClickListener {
            selectedMonster = 3
            setSelectedMonster(monster4, monster1, monster2, monster3)
        }

        nextButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedMonster", selectedMonster)
            }
            findNavController().navigate(R.id.action_pickMonsterFragment_to_gameFragment, bundle)
        }
    }

    private fun setSelectedMonster(selected: LinearLayout, vararg others: LinearLayout) {
        selected.isSelected = true
        others.forEach { it.isSelected = false }
    }
}
