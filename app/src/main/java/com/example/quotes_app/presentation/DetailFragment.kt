package com.example.quotes_app.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.quotes_app.R
import com.example.quotes_app.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()

    private var isFavorite = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipe = arguments?.getParcelable<com.example.quotes_app.domain.Recipe>("recipe")
        if (recipe == null) return

        binding.tvTitle.text = recipe.strMeal
        binding.tvCategory.text = "${recipe.strCategory ?: ""} - ${recipe.strArea ?: ""}"
        binding.tvInstructions.text = recipe.strInstructions ?: "No instructions available."

        Glide.with(this)
            .load(recipe.strMealThumb)
            .into(binding.ivRecipeThumb)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isFavorite(recipe.idMeal).collect { favorite ->
                    isFavorite = favorite
                    if (favorite) {
                        binding.fabFavorite.setImageResource(android.R.drawable.btn_star_big_on)
                        binding.fabFavorite.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFD700"))
                    } else {
                        binding.fabFavorite.setImageResource(android.R.drawable.btn_star_big_off)
                        binding.fabFavorite.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#9E9E9E"))
                    }
                }
            }
        }

        binding.fabFavorite.setOnClickListener {
            viewModel.toggleFavorite(recipe, isFavorite)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
