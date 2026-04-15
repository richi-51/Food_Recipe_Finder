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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.quotes_app.R
import com.example.quotes_app.databinding.FragmentRandomRecipeBinding
import com.example.quotes_app.domain.Recipe
import com.example.quotes_app.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RandomRecipeFragment : Fragment() {

    private var _binding: FragmentRandomRecipeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RandomRecipeViewModel by viewModels()
    private var currentRecipe: Recipe? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRandomRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSurpriseMe.setOnClickListener {
            viewModel.getRandomRecipe()
        }

        binding.cvRandomResult.setOnClickListener {
            currentRecipe?.let { recipe ->
                val bundle = Bundle().apply {
                    putParcelable("recipe", recipe)
                }
                findNavController().navigate(R.id.action_randomRecipeFragment_to_detailFragment, bundle)
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.randomRecipe.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.pbLoading.visibility = View.VISIBLE
                            binding.cvRandomResult.visibility = View.INVISIBLE
                        }
                        is Resource.Success -> {
                            binding.pbLoading.visibility = View.GONE
                            binding.cvRandomResult.visibility = View.VISIBLE
                            currentRecipe = resource.data
                            
                            binding.tvRandomName.text = resource.data?.strMeal
                            binding.tvRandomCategory.text = resource.data?.strCategory
                            
                            Glide.with(this@RandomRecipeFragment)
                                .load(resource.data?.strMealThumb)
                                .into(binding.ivRandomThumb)
                        }
                        is Resource.Error -> {
                            binding.pbLoading.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
