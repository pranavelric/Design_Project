package com.project.design.ui.product_info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.design.R
import com.project.design.databinding.FragmentProdInfoBinding


class ProdInfoFragment : Fragment() {


    private lateinit var binding: FragmentProdInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProdInfoBinding.inflate(inflater, container, false)
        return binding.root

    }




}