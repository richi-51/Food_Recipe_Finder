package com.example.quotes_app.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.quotes_app.R
import com.example.quotes_app.domain.Recipe
import com.example.quotes_app.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RandomRecipeFragment : Fragment() {

    private val viewModel: RandomRecipeViewModel by viewModels()
    private var currentRecipe: Recipe? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_random_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btn = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_surprise_me)
        val card = view.findViewById<com.google.android.material.card.MaterialCardView>(R.id.cv_random_result)
        val name = view.findViewById<android.widget.TextView>(R.id.tv_random_name)
        val category = view.findViewById<android.widget.TextView>(R.id.tv_random_category)
        val image = view.findViewById<android.widget.ImageView>(R.id.iv_random_thumb)
        val progress = view.findViewById<android.widget.ProgressBar>(R.id.pb_loading)

        // Button click
        btn.setOnClickListener {
            viewModel.getRandomRecipe()
        }

        // Observe data
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.randomRecipe.collect { state ->
                when (state) {

                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE
                        card.visibility = View.INVISIBLE
                    }

                    is Resource.Success -> {
                        progress.visibility = View.GONE
                        card.visibility = View.VISIBLE

                        val recipe = state.data
                        currentRecipe = recipe

                        name.text = recipe?.strMeal
                        category.text = recipe?.strCategory
                        image.load(recipe?.strMealThumb)
                    }

                    is Resource.Error -> {
                        progress.visibility = View.GONE
                        card.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }

        card.setOnClickListener {
            currentRecipe?.let { recipe ->
                val bundle = Bundle().apply {
                    putParcelable("recipe", recipe)
                }
                findNavController().navigate(
                    R.id.action_randomRecipeFragment_to_detailFragment,
                    bundle
                )
            }
        }
    }
}