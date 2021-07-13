package com.project.design.ui.purchase

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.project.design.R
import com.project.design.adapters.PurchaseAdapter
import com.project.design.data.model.Notifications
import com.project.design.data.model.Purchase
import com.project.design.data.model.User
import com.project.design.databinding.PurcahseFragmentBinding
import com.project.design.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class EarningFragment : Fragment() {

    companion object {
        fun newInstance() = EarningFragment()
    }

    private val viewModel: PurcahseViewModel by lazy {
        ViewModelProvider(this).get(PurcahseViewModel::class.java)
    }


    @Inject
    lateinit var purchaseAdapter: PurchaseAdapter

    lateinit var binding: PurcahseFragmentBinding
    lateinit var message: String
    lateinit var amount: String

    val mList: ArrayList<Purchase?> = ArrayList()
    lateinit var date: String


    private var user: User? = null

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
        binding = PurcahseFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData()
        setOnClickListener()
        getData()
        observeData()

    }

    private fun observeData() {


        viewModel.dateWisePurchase.observe(viewLifecycleOwner, { dt ->
            when (dt) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()

                    dt.data?.let { list ->
                        if (!list.isEmpty()) {
                            binding.purchaseRc.visible()
                            mList.clear()
                            mList.addAll(list)
                            submitListToRC()

                            binding.emptyLay.gone()

                        } else {
                            mList.clear()
                            binding.purchaseRc.gone()
                            binding.emptyLay.visible()

                        }
                    }
                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()

                }
                is ResponseState.Loading -> {
                }

            }

        })

        viewModel.allPrevPurchases.observe(viewLifecycleOwner, { at ->
            when (at) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()

                    at.data?.let { list ->
                        if (!list.isEmpty()) {
                            binding.purchaseRc.visible()
                            mList.clear()
                            mList.addAll(list)
                            submitListToRC()
                            binding.emptyLay.gone()
                            message = "Give coins to user for all purchases"
                        } else {
                            mList.clear()
                            binding.purchaseRc.gone()
                            binding.emptyLay.visible()

                        }
                    }
                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()

                }
                is ResponseState.Loading -> {
                }

            }

        })

        viewModel.sendCoinsLiveData.observe(viewLifecycleOwner, { at ->
            when (at) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()

                    at.data?.let { list ->
                        binding.root.snackbar(list)
                    }

                    val notifications = Notifications()
                    notifications.id = user?.uid.toString()
                    notifications.notifDate = getTodaysDate()
                    notifications.notifTitle = "Coins received"
                    notifications.notifMessage = "You have earned ${amount} coins for your each investment on ${date}."
                    notifications.notifTime = getCurrentTime()
                    notifications.userId = user?.uid.toString()
                    viewModel.sendNotificationToAllPurchaseUsers(notifications)



                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    at.message?.let { binding.root.snackbar(it) }
                }
                is ResponseState.Loading -> {
                }

            }

        })

    }

    private fun submitListToRC() {

        purchaseAdapter.submitList(mList)
        purchaseAdapter.notifyDataSetChanged()
    }

    private fun getData() {

    }

    private fun setData() {
        binding.topLay.dateText.setText(getTodaysDate())
        binding.purchaseRc.apply {
            adapter = purchaseAdapter
        }
        message = "Give coins to user"
        date = getTodaysDate()
        viewModel.getDateWisePurchse(date)

    }

    private fun setOnClickListener() {
        binding.topLay.selectDateBtn.setOnClickListener {
            showDatePicker()
        }
        binding.topLay.showAllPurchaseBtn.setOnClickListener {
            binding.progressBar.visible()
            viewModel.getAllPurchase()
        }
        binding.topLay.giveCoinAmount.setOnClickListener {
            coinAmountDialog()
        }
    }

    private fun showDatePicker() {
        val c: Calendar = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog = context?.let {
            DatePickerDialog(
                it,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->


                    val c: Calendar = Calendar.getInstance()
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val format1 = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val formatted: String = format1.format(c.getTime())
                    binding.topLay.dateText.setText(formatted)
                    viewModel.getDateWisePurchse(formatted)
                    binding.progressBar.visible()
                    message = "Give coins to purchase made on ${formatted}"
                    date = formatted
                },
                mYear,
                mMonth,
                mDay
            )
        }
        datePickerDialog?.show()
    }


    private fun coinAmountDialog() {

        val view: View = layoutInflater.inflate(R.layout.give_coins_dialog, null);
        val alertDialog: AlertDialog? = context?.let { AlertDialog.Builder(it).create() }
        alertDialog?.setTitle("Give coins");
        alertDialog?.setIcon(R.drawable.ic_coin_gold_svgrepo_com);
        alertDialog?.setCancelable(true);
        alertDialog?.setMessage(message);


        val etComments: TextInputEditText = view.findViewById(R.id.edit_text_coins)


        alertDialog?.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "OK",
            DialogInterface.OnClickListener { dialog, which ->

                // send today earning, coins to user, total earned for user
                val amt = etComments.text.toString()
                if (amt.isNullOrBlank()) {
                    etComments.error = "Please enter amount!"
                    Log.d("RRR", "coinAmountsadsaDialog: ")
                } else {
                    amount = amt
                    viewModel.sendTodaysEarningToAllPurchases(amt.toInt(), date)
                    binding.progressBar.visible()

                    dialog.dismiss()
                }
            });

        alertDialog?.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            "Cancel",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            });


        alertDialog?.setView(view);
        alertDialog?.show();


    }


}