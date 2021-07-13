package com.project.design.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.project.design.data.model.Purchase
import com.project.design.databinding.PurchaseItemBinding
import com.project.design.utils.getDaysRemaining


class PurchaseAdapter :
    ListAdapter<Purchase?, PurchaseAdapter.PurchaseViewHolder>(MyDiffutilCallback()) {

    lateinit var context: Context

    inner class PurchaseViewHolder(private val binding: PurchaseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cp: Purchase?, position: Int) {
            binding.userIdText.setText(cp?.userId)
            binding.purchaseIdText.setText(cp?.id)
            binding.purchaseDateText.setText(cp?.purchaseDate)
            binding.puchaseAmountText.setText(cp?.purchaseAmount.toString())
            binding.purchaseTimeText.setText(cp?.purchaseTime)
            binding.daysRemaining.text = getDaysRemaining(cp?.endDate.toString()).toString()
        }

    }


    open class MyDiffutilCallback : DiffUtil.ItemCallback<Purchase?>() {
        override fun areItemsTheSame(
            oldItem: Purchase,
            newItem: Purchase
        ): Boolean {
            return oldItem.purchaseUid == newItem.purchaseUid
        }

        override fun areContentsTheSame(
            oldItem: Purchase,
            newItem: Purchase
        ): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding: PurchaseItemBinding =
            PurchaseItemBinding.inflate(layoutInflater, parent, false)
        return PurchaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PurchaseViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    private var onItemClickListener: ((Purchase?, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (Purchase?, Int) -> Unit) {
        onItemClickListener = listener
    }

}
