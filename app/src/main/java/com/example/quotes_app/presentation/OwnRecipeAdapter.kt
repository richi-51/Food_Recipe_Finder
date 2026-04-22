package com.example.quotes_app.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quotes_app.databinding.ItemRecipeBinding
import com.example.quotes_app.domain.OwnRecipe

class OwnRecipeAdapter(
    private val onItemClick: (OwnRecipe) -> Unit,
    private val onEditClick: (OwnRecipe) -> Unit,
    private val onDeleteClick: (OwnRecipe) -> Unit
) : ListAdapter<OwnRecipe, OwnRecipeAdapter.OwnRecipeViewHolder>(OwnRecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnRecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OwnRecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OwnRecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OwnRecipeViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: OwnRecipe) {
            binding.tvTitle.text = recipe.title
            binding.tvCategory.text = recipe.category
            
            binding.btnEdit.visibility = android.view.View.VISIBLE
            binding.btnDelete.visibility = android.view.View.VISIBLE
            
            Glide.with(binding.root)
                .load(recipe.imagePath)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(binding.ivThumb)

            binding.root.setOnClickListener {
                onItemClick(recipe)
            }
            binding.btnEdit.setOnClickListener {
                onEditClick(recipe)
            }
            binding.btnDelete.setOnClickListener {
                onDeleteClick(recipe)
            }
        }
    }

    class OwnRecipeDiffCallback : DiffUtil.ItemCallback<OwnRecipe>() {
        override fun areItemsTheSame(oldItem: OwnRecipe, newItem: OwnRecipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OwnRecipe, newItem: OwnRecipe): Boolean {
            return oldItem == newItem
        }
    }
}
