package com.example.healthyliving.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthyliving.R
import com.example.healthyliving.databinding.FragmentResepBinding
import com.example.healthyliving.remote.response.ResepItem
import com.example.healthyliving.remote.response.UserPreference
import com.example.healthyliving.ui.activity.DetailRecipeActivity
import com.example.healthyliving.ui.adapter.RecipeAdapter
import com.example.healthyliving.ui.viewmodel.LoginViewModel
import com.example.healthyliving.ui.viewmodel.ResepViewModel
import com.example.healthyliving.ui.viewmodel.ViewModelFactory

class ResepFragment(private val dataStore: DataStore<Preferences>) : Fragment() {

    private lateinit var binding: FragmentResepBinding
    private lateinit var token: String
    private val viewModel: ResepViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().title = "Resep"
        binding = FragmentResepBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvResep.layoutManager = layoutManager

        val bottomNavigationBarHeight = resources.getDimensionPixelSize(R.dimen.bottom_navigation_bar_height)
        val layoutParams = binding.root.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, 0, 0, bottomNavigationBarHeight)
        binding.root.layoutParams = layoutParams

        val pref = UserPreference.getInstance(dataStore)
        val loginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[LoginViewModel::class.java]

        loginViewModel.getToken().observe(viewLifecycleOwner) {
            token = it
            viewModel.getRecipe(token)
        }
        viewModel.listRecipe.observe(viewLifecycleOwner) { listUser ->
            listUser?.let{setData(it)}
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        return binding.root
    }

    private fun showSelectedRecipe(detailRecipe: ResepItem) {
        val intent = Intent(requireContext(), DetailRecipeActivity::class.java)

        intent.putExtra(DetailRecipeActivity.EXTRA_RECIPE, detailRecipe)
        startActivity(intent)
    }

    private fun setData(Items: List<ResepItem>) {
        val adapter = RecipeAdapter(Items)
        binding.rvResep.adapter = adapter
        binding.rvResep.visibility = View.VISIBLE

        adapter.setOnItemClickCallback(object : RecipeAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ResepItem) {
                showSelectedRecipe(data)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}