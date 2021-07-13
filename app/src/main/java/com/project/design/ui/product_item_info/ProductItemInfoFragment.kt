package com.project.design.ui.product_item_info

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.marcoscg.dialogsheet.DialogSheet
import com.project.design.R
import com.project.design.data.model.ProductModel
import com.project.design.databinding.ProductItemInfoFragmentBinding
import com.project.design.ui.activities.MainActivity
import com.project.design.utils.Constants
import com.project.design.utils.checkAboveKitkat
import com.project.design.utils.getStatusBarHeight
import com.project.design.utils.showCustomDialog
import com.thecode.aestheticdialogs.DialogAnimation
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProductItemInfoFragment : Fragment() {


    private val viewModel: ProductItemInfoViewModel by lazy {
        ViewModelProvider(this).get(ProductItemInfoViewModel::class.java)
    }

    private lateinit var binding: ProductItemInfoFragmentBinding
    lateinit var behaviour: BottomSheetBehavior<ConstraintLayout>
    private var productModel: ProductModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            productModel = it.getSerializable(Constants.USERS_BUNDLE_OBJ) as ProductModel?
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProductItemInfoFragmentBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        behaviour = BottomSheetBehavior.from(binding.btmListLay.root)
        behaviour.state = BottomSheetBehavior.STATE_COLLAPSED



        setData()
        setMyClickListener()
        setSlidingBehaviour()
    }

    private fun setMyClickListener() {
        binding.buynow.setOnClickListener {

            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
           // checkBottomSheetValue()
        }

        binding.btmListLay.submitCoins.setOnClickListener {
            checkBottomSheetValue()
        }


    }

    private fun checkBottomSheetValue() {

        if (binding.btmListLay.editTextCoinsAmount.text.isNullOrBlank()) {
            activity?.showCustomDialog(
                "Invalid input",
                "Please choose valid quantity.",
                true,
                DialogStyle.RAINBOW,
                DialogType.ERROR,
                DialogAnimation.SHRINK
            )
        } else {

            if (binding.btmListLay.editTextCoinsAmount.text.toString().toInt() < 1) {
                activity?.showCustomDialog(
                    "Invalid input",
                    "Minimun order of 1 product is needed to be placed.",
                    true,
                    DialogStyle.RAINBOW,
                    DialogType.WARNING,
                    DialogAnimation.SHRINK
                )
            } else {


                val prodAmount =
                    binding.btmListLay.editTextCoinsAmount.text.toString().toInt().toDouble()




                BottomSheetBehavior.from(binding.btmListLay.root).state =
                    BottomSheetBehavior.STATE_COLLAPSED

                showBuyProductBottomSheet(prodAmount)


            }

        }

    }

    private fun showBuyProductBottomSheet(prodAmount: Double) {


        val amount = prodAmount.toInt() * productModel?.price!!
        context?.let {
            DialogSheet(it)
                .setTitle("Place order")
                .setMessage("Order Amount: ₹${prodAmount * productModel?.price!!}")
                .setColoredNavigationBar(true)
                .setTitleTextSize(20) // In SP
                .setCancelable(true)
                .setPositiveButton("Buy") {

                    productModel!!.id?.let { it1 ->
                        (activity as MainActivity).startPayment(amount.toString(),
                            it1
                        )
                    }

                }
                .setNegativeButton(android.R.string.cancel) {
                }
                .setRoundedCorners(true)
                .setBackgroundColor(Color.WHITE)
                .setButtonsColorRes(R.color.primary_color)
                .setNegativeButtonColorRes(R.color.red)
                .show()
        }

    }


    private fun setData() {
        binding.prodDesc.text = productModel?.desc
        binding.prodName.text = productModel?.name
        binding.prodPrice.text = "₹"+productModel?.price.toString()
        binding.btmListLay.coinsText.text = "₹"+productModel?.price.toString()
    }

    private fun setSlidingBehaviour() {
        val behavior = BottomSheetBehavior.from(binding.bottomSheet)
        if (checkAboveKitkat()) {

            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                        binding.bottomSheet.setPadding(0, 0, 0, 0)

                    } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        activity?.getStatusBarHeight()?.let { bottomSheet.setPadding(0, it, 0, 0) }

                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                    //if (slideOffset < 0.5F) {
                    //     binding.tempView.alpha = 0.5F

                    //  } else {
                    //   binding.tempView.alpha = (slideOffset)

                    //  }
                    bottomSheet.setPadding(
                        0,
                        (slideOffset * activity?.getStatusBarHeight()!!).toInt(),
                        0,
                        0
                    )
                }
            })
        }

    }


}