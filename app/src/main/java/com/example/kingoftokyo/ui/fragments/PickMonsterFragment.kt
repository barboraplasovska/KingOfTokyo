package com.example.kingoftokyo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
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

        var nextButton: Button = view.findViewById(R.id.nextButton)

        var monster1: LinearLayout = view.findViewById(R.id.monster1)
        var monster2: LinearLayout = view.findViewById(R.id.monster2)
        var monster3: LinearLayout = view.findViewById(R.id.monster3)
        var monster4: LinearLayout = view.findViewById(R.id.monster4)

        monster1.setOnClickListener{
            monster2.isSelected = false
            monster3.isSelected = false
            monster4.isSelected = false

            monster1.isSelected = true
            selectedMonster = 0
        }

        monster2.setOnClickListener{
            monster1.isSelected = false
            monster3.isSelected = false
            monster4.isSelected = false

            monster2.isSelected = true
            selectedMonster = 1
        }

        monster3.setOnClickListener{
            monster1.isSelected = false
            monster2.isSelected = false
            monster4.isSelected = false

            monster3.isSelected = true
            selectedMonster = 2
        }

        monster4.setOnClickListener{
            monster1.isSelected = false
            monster2.isSelected = false
            monster3.isSelected = false

            monster4.isSelected = true
            selectedMonster = 3
        }

        nextButton.setOnClickListener {
            // Navigate to the next screen or perform any other action
        }
    }
}
