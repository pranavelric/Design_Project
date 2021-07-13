package com.project.design.ui.order_history

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.design.R
import com.project.design.adapters.OrderRecordAdapter
import com.project.design.data.model.User
import com.project.design.databinding.OrderHistoryFragmentBinding
import com.project.design.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class OrderHistoryFragment : Fragment() {


    private var user: User? = null
    private val viewModel: OrderHistoryViewModel by lazy {
        ViewModelProvider(this).get(OrderHistoryViewModel::class.java)
    }

    private lateinit var binding: OrderHistoryFragmentBinding

    @Inject
    lateinit var orderRecordAdapter: OrderRecordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            user = it.getSerializable(Constants.USERS_BUNDLE_OBJ) as User?
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = OrderHistoryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        getData()
        setData()
        observeData()

    }

    private fun observeData() {
        viewModel.getOrderProducts.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()

                    it.data?.let { list->
                        if(list.isEmpty()){
                            binding.emptyLay.visible()
                        }
                        else{
                            binding.emptyLay.gone()
                        }
                        orderRecordAdapter.submitList(list)
                    }
                }
                is ResponseState.Loading -> {
                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    binding.emptyLay.visible()
                    it.message?.let { it1 -> context?.toast(it1) }
                }

            }

        })

    }

    private fun setData() {
        binding.orderrd.apply {
            adapter = orderRecordAdapter

        }
    }

    private fun getData() {
        user?.uid?.let { viewModel.getOrderProduct(it) }
    }


}