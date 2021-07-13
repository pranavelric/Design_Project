package com.project.design.ui.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.badge.BadgeDrawable
import com.project.design.R
import com.project.design.adapters.ProductAdapter
import com.project.design.data.model.User
import com.project.design.databinding.MainFragmentBinding
import com.project.design.ui.activities.MainActivity
import com.project.design.utils.*
import com.project.design.utils.Constants.USERS_BUNDLE_OBJ
import dagger.hilt.android.AndroidEntryPoint
import me.ibrahimsn.lib.OnItemSelectedListener
import javax.inject.Inject


@AndroidEntryPoint
class MainFragment : Fragment() {

    private var user: User? = null


    @Inject
    lateinit var sharedPrefrences: MySharedPrefrences

    @Inject
    lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            user = it.getSerializable(USERS_BUNDLE_OBJ) as User?
        }


    }


    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private lateinit var binding: MainFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = MainFragmentBinding.inflate(inflater, container, false)
        binding.toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }



        setHasOptionsMenu(true)
        observeData()

        setClickListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    private fun observeData() {

        viewModel.notifSize()

        viewModel.getProduct()
        viewModel.getProducts.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    it.data?.let { list ->

                        if (list.isEmpty()) {
                            binding.emptyLay.visible()
                        } else {
                            binding.emptyLay.gone()
                        }
                        productAdapter.submitList(list)
                    }
                }
                is ResponseState.Error -> {
                    it.message?.let { it1 -> context?.toast(it1) }
                    binding.emptyLay.visible()
                }
                is ResponseState.Loading -> {
                }

            }
        })

    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun initBadge(count: Int, show: Boolean) {
        val b = BadgeDrawable.create(requireContext())
        if (show) {
            b.number = count
            b.maxCharacterCount = 99
            b.badgeGravity = BadgeDrawable.TOP_END
            b.verticalOffset = 20
            b.horizontalOffset = 20
            b.backgroundColor = Color.RED
            b.badgeTextColor = Color.WHITE
            b.isVisible = true
            //      val target: View = binding.root.findViewById(R.id.withdrawal_requests)
            //      BadgeUtils.attachBadgeDrawable(b, target)
        } else {
            b.isVisible = false
        }


    }


    private fun setData() {

        binding.productRc.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(context, 2)
        }

    }


    private fun setClickListeners() {


        binding.bottomBar.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelect(pos: Int): Boolean {
                when (pos) {
                    0 -> {
                        return true
                    }
                    1 -> {
                        val bundle = Bundle().apply {
                            putSerializable(USERS_BUNDLE_OBJ, user)
                        }

                        findNavController().navigate(
                            R.id.action_mainFragment_to_profileFragment,
                            bundle
                        )

                        return true
                    }
                    2 -> {
                        val bundle = Bundle().apply {
                            putSerializable(USERS_BUNDLE_OBJ, user)
                        }

                        findNavController().navigate(
                            R.id.action_mainFragment_to_earningFragment,
                            bundle
                        )

                        return true
                    }
//                    3->{
//                        val bundle = Bundle().apply {
//                            putSerializable(USERS_BUNDLE_OBJ, user)
//                        }
//
//                        findNavController().navigate(R.id.action_mainFragment_to_settingsFragment,bundle)
//                        return true
//                    }
                    else -> {

                        return false
                    }
                }

            }
        }





        binding.quizGame.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable(USERS_BUNDLE_OBJ, user)
            }
            //    findNavController().navigate(R.id.action_mainFragment_to_quizFragment, bundle)

        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_profile -> {
                val bundle = Bundle().apply {
                    putSerializable(USERS_BUNDLE_OBJ, user)
                }
                findNavController().navigate(R.id.action_mainFragment_to_profileFragment, bundle)
                return true
            }
            R.id.action_logout -> {

//                (activity as MainActivity).googleSignInClient.signOut()
                (activity as MainActivity).firebaseAuth.signOut()
                findNavController().navigate(R.id.action_mainFragment_to_loginFragment)

                return true
            }
//            R.id.action_share -> {
//                 activity?.share("Playstore link", "text")
//
//
//
//
//                return true
//            }
//            R.id.action_refer->{
//
//            return false
//            }

            else -> {
                return false
            }
        }
    }


    override fun onStart() {
        super.onStart()
        (activity as MainActivity).setFullScreenWithBtmNav()

    }


}