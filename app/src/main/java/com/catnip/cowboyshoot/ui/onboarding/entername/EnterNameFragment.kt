package com.catnip.cowboyshoot.ui.onboarding.entername

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.catnip.cowboyshoot.databinding.FragmentEnterNameBinding
import com.catnip.cowboyshoot.ui.onboarding.OnFinishNavigateListener


class EnterNameFragment : Fragment(), OnFinishNavigateListener {

    private lateinit var binding: FragmentEnterNameBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEnterNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onFinishNavigateListener() {
        val name = binding.etName.text.toString().trim()
        if(name.isEmpty()){
            Toast.makeText(requireContext(), "Please input your name !", Toast.LENGTH_SHORT).show()
        }else{
            navigateToMenu(name)
        }
    }

    private fun navigateToMenu(name : String) {
        //todo : navigate to menu
        Toast.makeText(requireContext(), "Name : $name", Toast.LENGTH_SHORT).show()
    }
}

