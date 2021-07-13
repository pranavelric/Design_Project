package com.project.design.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.design.databinding.SettingsFragmentBinding

class SettingsFragment : Fragment() {


    private val viewModel: SettingsViewModel by lazy {
        ViewModelProvider(this).get(SettingsViewModel::class.java)
    }

    private lateinit var binding: SettingsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingsFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}