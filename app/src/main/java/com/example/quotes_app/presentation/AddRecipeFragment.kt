package com.example.quotes_app.presentation

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.quotes_app.databinding.FragmentAddRecipeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddRecipeFragment : Fragment() {

    private var _binding: FragmentAddRecipeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddRecipeViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this).load(it).into(binding.ivRecipePreview)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeToEdit = arguments?.getParcelable<com.example.quotes_app.domain.OwnRecipe>("recipe_to_edit")
        if (recipeToEdit != null) {
            viewModel.setEditingId(recipeToEdit.id)
            binding.etRecipeName.setText(recipeToEdit.title)
            binding.etRecipeCategory.setText(recipeToEdit.category)
            binding.etRecipeInstructions.setText(recipeToEdit.instructions)
            binding.btnSaveRecipe.text = "Update Recipe"
            
            recipeToEdit.imagePath?.let { path ->
                selectedImageUri = Uri.parse(path)
                Glide.with(this).load(path).into(binding.ivRecipePreview)
            }
        }

        binding.btnSelectImage.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        binding.btnSaveRecipe.setOnClickListener {
            val name = binding.etRecipeName.text.toString()
            val category = binding.etRecipeCategory.text.toString()
            val instructions = binding.etRecipeInstructions.text.toString()

            if (name.isNotEmpty() && category.isNotEmpty() && instructions.isNotEmpty()) {
                viewModel.saveRecipe(name, category, instructions, selectedImageUri?.toString())
                val msg = if (recipeToEdit != null) "Recipe Updated!" else "Recipe Saved!"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
