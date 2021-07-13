package com.project.design.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.design.data.model.Withdrawal
import com.project.design.databinding.WithdrawalRecordItemBinding

class WithdrawalRecordAdapter :
    ListAdapter<Withdrawal?, WithdrawalRecordAdapter.WithdrawalRecordViewHolder>(MyDiffutilCallback()) {

    lateinit var context: Context

    inner class WithdrawalRecordViewHolder(private val binding: WithdrawalRecordItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cp: Withdrawal?, position: Int) {

            binding.purchaseDateText.setText(cp?.date)
            binding.paymentAmount.setText(cp?.amount + " ₹")
            binding.paymentId.setText(cp?.id)
            binding.userId.setText(cp?.userId)
            binding.withdrawalStatusText.setText(cp?.status)
            binding.withdrawalUpiText.setText(cp?.upiCode)
            binding.paymentTime.setText(cp?.time)

            binding.copyUpi.setOnClickListener {

                onImageItemClickListener?.let {
                    it(cp, position)
                }
            }

            binding.loanApplyCard.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(cp, position)
                }
            }

            binding.moneySendButton.setOnClickListener {
                onButtonItemClickListener?.let { click ->
                    click(cp, position)
                }
            }


        }

    }


    open class MyDiffutilCallback : DiffUtil.ItemCallback<Withdrawal?>() {
        override fun areItemsTheSame(
            oldItem: Withdrawal,
            newItem: Withdrawal
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Withdrawal,
            newItem: Withdrawal
        ): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WithdrawalRecordViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding: WithdrawalRecordItemBinding =
            WithdrawalRecordItemBinding.inflate(layoutInflater, parent, false)
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


    private var onItemClickListener: ((Withdrawal?, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (Withdrawal?, Int) -> Unit) {
        onItemClickListener = listener
    }

    private var onImageItemClickListener: ((Withdrawal?, Int) -> Unit)? = null
    fun setOnImageItemClickListener(listener: (Withdrawal?, Int) -> Unit) {
        onImageItemClickListener = listener
    }

    private var onButtonItemClickListener: ((Withdrawal?, Int) -> Unit)? = null
    fun setOnButtonItemClickListener(listener: (Withdrawal?, Int) -> Unit) {
        onButtonItemClickListener = listener
    }


}