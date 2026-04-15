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
import com.example.quotes_app.databinding.FragmentHomeBinding
import com.example.quotes_app.utils.Resource
import com.example.quotes_app.utils.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var themeManager: ThemeManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        themeManager = ThemeManager(requireContext())

        setupRecyclerView()
        setupSearchView()
        setupThemeToggle()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter { recipe ->
            val bundle = Bundle().apply {
                putParcelable("recipe", recipe)
            }
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
        }
        binding.rvRecipes.apply {
            adapter = recipeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val searchQuery = query ?: ""
                viewModel.searchRecipes(searchQuery)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun setupThemeToggle() {
        updateThemeIcon()
        binding.btnThemeToggle.setOnClickListener {
            val isDark = themeManager.isDarkMode()
            themeManager.setDarkMode(!isDark)
            updateThemeIcon()
            requireActivity().recreate()
        }
    }

    private fun updateThemeIcon() {
        if (themeManager.isDarkMode()) {
            binding.btnThemeToggle.setImageResource(android.R.drawable.ic_menu_day)
        } else {
            binding.btnThemeToggle.setImageResource(android.R.drawable.ic_menu_recent_history) // Using a moon-like icon if possible, or just another one
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.recipes.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.loadingLayout.visibility = View.VISIBLE
                            binding.tvError.visibility = View.GONE
                        }
                        is Resource.Success -> {
                            binding.loadingLayout.visibility = View.GONE
                            binding.tvError.visibility = View.GONE
                            recipeAdapter.submitList(resource.data)
                        }
                        is Resource.Error -> {
                            binding.loadingLayout.visibility = View.GONE
                            binding.tvError.visibility = View.VISIBLE
                            binding.tvError.text = resource.message
                            recipeAdapter.submitList(emptyList())
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
