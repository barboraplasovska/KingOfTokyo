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
        val monsterDemon: LinearLayout = view.findViewById(R.id.monsterDemon)
        val monsterDragon: LinearLayout = view.findViewById(R.id.monsterDragon)
        val monsterLizard: LinearLayout = view.findViewById(R.id.monsterLizard)
        val monsterRobot: LinearLayout = view.findViewById(R.id.monsterRobot)

        selectedMonster = 0
        setSelectedMonster(monsterDemon, monsterDragon, monsterLizard, monsterRobot)

        monsterDemon.setOnClickListener {
            selectedMonster = 0
            setSelectedMonster(monsterDemon, monsterDragon, monsterLizard, monsterRobot)
        }

        monsterDragon.setOnClickListener {
            selectedMonster = 1
            setSelectedMonster(monsterDragon, monsterDemon, monsterLizard, monsterRobot)
        }

        monsterRobot.setOnClickListener {
            selectedMonster = 2
            setSelectedMonster(monsterRobot, monsterDemon, monsterDragon, monsterLizard)
        }

        monsterLizard.setOnClickListener {
            selectedMonster = 3
            setSelectedMonster(monsterLizard, monsterDemon, monsterDragon, monsterRobot)
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
