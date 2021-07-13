package com.project.design.ui.withdrawal


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.project.design.adapters.WithdrawalRecordAdapter
import com.project.design.data.model.User
import com.project.design.data.model.Withdrawal
import com.project.design.databinding.WithdrawalRecordFragmentBinding
import com.project.design.ui.activities.MainActivity
import com.project.design.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class WithdrawalRecordFragment : Fragment() {


    private val viewModel: WithdrawalRecordViewModel by lazy {
        ViewModelProvider(this).get(WithdrawalRecordViewModel::class.java)
    }
    lateinit var binding: WithdrawalRecordFragmentBinding

    @Inject
    lateinit var withdrawalRecordAdapter: WithdrawalRecordAdapter

    lateinit var arrayList: MutableList<Withdrawal?>
    var user: User? = null
    var position: Int = 0
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
        binding = WithdrawalRecordFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arrayList = ArrayList<Withdrawal?>()
        observeData()
        getData()
        setData()
        clickListeners()

    }

    private fun clickListeners() {
        withdrawalRecordAdapter.setOnImageItemClickListener { withdrawal, pos ->

            context?.copyToClipboard(withdrawal?.upiCode.toString())
            context?.toast("Upi id copied")
        }

        withdrawalRecordAdapter.setOnItemClickListener { withdrawal, pos ->
            context?.copyToClipboard(withdrawal?.upiCode.toString())
            context?.toast("Upi id copied")
        }
        withdrawalRecordAdapter.setOnButtonItemClickListener { withdrawal, pos ->

            binding.progressBar.visible()
            CoroutinesHelper.delayWithMain(1000) {

                if (withdrawal != null) {

                    viewModel.setPaymentDoneFor(withdrawal)
                    position = pos
                }else{
                    binding.progressBar.gone()
                    context?.toast("Withdrawal request is null")
                }

            }

        }


    }


    private fun observeData() {

        viewModel.withdrawalRecordLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()

                    it.data?.let { list ->

                        arrayList.addAll(list)

                        withdrawalRecordAdapter.submitList(arrayList)


                        if (list.isEmpty()) {
                            binding.withdrawalRecordRc.gone()
                            binding.emptyLay.visible()
                        } else {
                            binding.withdrawalRecordRc.visible()
                            binding.emptyLay.gone()
                        }

                    }
                }
                is ResponseState.Loading -> {
                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    binding.withdrawalRecordRc.gone()
                    binding.emptyLay.visible()
                    it.message?.let { it1 -> context?.toast(it1) }

                }

            }
        })

        viewModel.withdrawalDoneLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()

                    it.data?.let { msg ->
                        binding.root.snackbar(msg)

                        arrayList.removeAt(position)
//                        withdrawalRecordAdapter.notifyItemRangeRemoved(position,withdrawalRecordAdapter.currentList.size)
                        withdrawalRecordAdapter.notifyDataSetChanged()

                        if(arrayList.isEmpty()){
                            binding.withdrawalRecordRc.gone()
                            binding.emptyLay.visible()
                        }


                    }
                }
                is ResponseState.Loading -> {
                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()

                    it.message?.let { msg ->
                        binding.root.snackbar(msg)

                    }

                }

            }
        })

    }

    private fun getData() {
        viewModel.getWithdrawalRecords()
        binding.progressBar.visible()

    }

    private fun setData() {
        binding.withdrawalRecordRc.apply {
            adapter = withdrawalRecordAdapter
        }
    }


    private fun startPayment(withdrawal: Withdrawal) {
        try {
            //    .setPayeeVpa("pranavchoudhary500@okaxis")

            val uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", "9414632426s@okicici")
                .appendQueryParameter("pn", "User name")
                .appendQueryParameter("tn", "Withdrawal amount send")
                .appendQueryParameter("am", "1")
                .appendQueryParameter("cu", "INR")
                .build()


            val upiPayIntent = Intent(Intent.ACTION_VIEW)
            upiPayIntent.data = uri

            // will always show a dialog to user to choose an app
            val chooser = Intent.createChooser(upiPayIntent, "Pay with")

            // check if intent resolves
            if (null != activity?.packageManager?.let { chooser.resolveActivity(it) }) {
                startActivityForResult(chooser, Constants.UPI_PAYMENT)
            } else {
                context?.toast("No UPI app found, please install one to continue")
            }


        } catch (e: Exception) {
            e.message?.let { context?.toast(it) }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.UPI_PAYMENT -> if (Activity.RESULT_OK == resultCode || resultCode == 11) {
                if (data != null) {
                    val trxt = data.getStringExtra("response")
                    Log.d("UPI", "onActivityResult: $trxt")
                    val dataList = ArrayList<String>()
                    if (trxt != null) {
                        dataList.add(trxt)
                    }
                    upiPaymentDataOperation(dataList)
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null")
                    val dataList = ArrayList<String>()
                    dataList.add("nothing")
                    upiPaymentDataOperation(dataList)
                }
            } else {
                Log.d(
                    "UPI",
                    "onActivityResult: " + "Return data is null"
                ) //when user simply back without payment
                val dataList = ArrayList<String>()
                dataList.add("nothing")
                upiPaymentDataOperation(dataList)
            }
        }
    }

    private fun upiPaymentDataOperation(data: ArrayList<String>) {
        if ((activity as MainActivity).networkHelper.isNetworkConnected()) {

            if (data.isNotEmpty()) {
                var str: String? = data[0]
                Log.d("UPIPAY", "upiPaymentDataOperation: " + str!!)
                var paymentCancel = ""
                if (str == null) str = "discard"
                var status = ""
                var approvalRefNo = ""
                val response =
                    str.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (i in response.indices) {
                    val equalStr =
                        response[i].split("=".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    if (equalStr.size >= 2) {
                        if (equalStr[0].toLowerCase() == "Status".toLowerCase()) {
                            status = equalStr[1].toLowerCase()
                        } else if (equalStr[0].toLowerCase() == "ApprovalRefNo".toLowerCase() || equalStr[0].toLowerCase() == "txnRef".toLowerCase()) {
                            approvalRefNo = equalStr[1]
                        }
                    } else {
                        paymentCancel = "Payment cancelled by user."
                    }
                }

                if (status == "success") {
                    //Code to handle successful transaction here.

                    context?.toast("Transaction successful.")
                    Log.d("UPI", "responseStr: $approvalRefNo")
                } else if ("Payment cancelled by user." == paymentCancel) {
                    context?.toast("Payment cancelled by user.")
                } else {

                    context?.toast("Transaction failed.Please try again")

                }
            } else {
                context?.toast("Payment cancelled or UPI code is invalid")
            }
        } else {


            context?.toast("Internet connection is not available. Please check and try again")
        }
    }


}