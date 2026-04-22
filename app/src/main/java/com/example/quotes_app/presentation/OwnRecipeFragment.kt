package com.example.quotes_app.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quotes_app.R
import com.example.quotes_app.databinding.FragmentOwnRecipeBinding
import com.example.quotes_app.domain.Recipe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OwnRecipeFragment : Fragment() {

    private var _binding: FragmentOwnRecipeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OwnRecipeViewModel by viewModels()
    private lateinit var ownRecipeAdapter: OwnRecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOwnRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnAddRecipe.setOnClickListener {
            findNavController().navigate(R.id.action_ownRecipeFragment_to_addRecipeFragment)
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchRecipes(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchRecipes(newText ?: "")
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        ownRecipeAdapter = OwnRecipeAdapter(
            onItemClick = { ownRecipe ->
                val recipe = Recipe(
                    idMeal = ownRecipe.id,
                    strMeal = ownRecipe.title,
                    strCategory = ownRecipe.category,
                    strArea = ownRecipe.area,
                    strInstructions = ownRecipe.instructions,
                    strMealThumb = ownRecipe.imagePath
                )
                val bundle = Bundle().apply {
                    putParcelable("recipe", recipe)
                }
                findNavController().navigate(R.id.action_ownRecipeFragment_to_detailFragment, bundle)
            },
            onEditClick = { ownRecipe ->
                val bundle = Bundle().apply {
                    putParcelable("recipe_to_edit", ownRecipe)
                }
                findNavController().navigate(R.id.action_ownRecipeFragment_to_addRecipeFragment, bundle)
            },
            onDeleteClick = { ownRecipe ->
                viewModel.deleteRecipe(ownRecipe)
            }
        )
        binding.rvOwnRecipes.apply {
            adapter = ownRecipeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        viewModel.loadMore()
                    }
                }
            })
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ownRecipes.collect { recipes ->
                    if (recipes.isEmpty()) {
                        binding.tvEmpty.visibility = View.VISIBLE
                    } else {
                        binding.tvEmpty.visibility = View.GONE
                    }
                    ownRecipeAdapter.submitList(recipes)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
