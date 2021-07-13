package com.project.design.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.design.R
import com.project.design.data.model.OrderModel
import com.project.design.databinding.OrderRecordItemBinding


class OrderRecordAdapter :
    ListAdapter<OrderModel?, OrderRecordAdapter.WithdrawalRecordViewHolder>(MyDiffutilCallback()) {

    lateinit var context: Context

    inner class WithdrawalRecordViewHolder(private val binding: OrderRecordItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cp: OrderModel?, position: Int) {

            binding.purchaseDateText.setText(cp?.date)

            binding.paymentTime.setText(cp?.time)
            binding.orderAmount.text = cp?.paidAmount

            Glide.with(context).load(cp?.prodImg).error(R.drawable.puzzle2).into(binding.prodImg)
            binding.orderId.text = cp?.id


            if(cp?.isDelivered==true){
                binding.moneySendButton.text = "Delivered"
                binding.moneySendButton.backgroundTintList= ColorStateList.valueOf(Color.GREEN)
            }
            else{
                binding.moneySendButton.text = "Preparing for dispatch"
            }


            binding.loanApplyCard.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(cp, position)
                }
            }


        }

    }


    open class MyDiffutilCallback : DiffUtil.ItemCallback<OrderModel?>() {
        override fun areItemsTheSame(
            oldItem: OrderModel,
            newItem: OrderModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: OrderModel,
            newItem: OrderModel
        ): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WithdrawalRecordViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding: OrderRecordItemBinding =
            OrderRecordItemBinding.inflate(layoutInflater, parent, false)
        return WithdrawalRecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WithdrawalRecordViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    private var onItemClickListener: ((OrderModel?, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (OrderModel?, Int) -> Unit) {
        onItemClickListener = listener
    }


    private var onButtonItemClickListener: ((OrderModel?, Int) -> Unit)? = null
    fun setOnButtonItemClickListener(listener: (OrderModel?, Int) -> Unit) {
        onButtonItemClickListener = listener
    }


}