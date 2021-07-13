package com.project.design.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.design.R
import com.project.design.data.model.ProductModel
import com.project.design.databinding.ProductItemBinding
import com.project.design.utils.gone
import com.project.design.utils.visible


class ProductAdapter :
    ListAdapter<ProductModel?, ProductAdapter.ProductViewHolder>(MyDiffutilCallback()) {

    lateinit var context: Context

    inner class ProductViewHolder(private val binding: ProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cp: ProductModel?, position: Int) {


            binding.priceTextPath.text = "₹" + cp?.price.toString()
            binding.textPath.setText(cp?.name)

            if (cp?.inStock == false) {
                binding.outOfStockImage.visible()
            } else {
                binding.outOfStockImage.gone()
            }
            Glide.with(context).load(cp?.image).error(R.drawable.puzzle2).into(binding.menuImg)


            binding.productCard.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(cp, position)

                }
            }

        }


    }


    open class MyDiffutilCallback : DiffUtil.ItemCallback<ProductModel?>() {
        override fun areItemsTheSame(
            oldItem: ProductModel,
            newItem: ProductModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ProductModel,
            newItem: ProductModel
        ): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding: ProductItemBinding =
            ProductItemBinding.inflate(layoutInflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    private var onItemClickListener: ((ProductModel?, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (ProductModel?, Int) -> Unit) {
        onItemClickListener = listener
    }


}
