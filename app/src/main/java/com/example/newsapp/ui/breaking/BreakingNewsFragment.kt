package com.example.newsapp.ui.breaking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.adapter.BreakingNewsAdapter
import com.example.newsapp.adapter.LoadingStateAdapter
import com.example.newsapp.databinding.FragmentBreakingBinding
import com.example.newsapp.utils.ViewModelFactory
import kotlinx.coroutines.launch

class BreakingNewsFragment : Fragment() {

    private var _binding: FragmentBreakingBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BreakingNewsAdapter

    private val viewModel by viewModels<BreakingNewsViewModel>{
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreakingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoading(true)

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvBreakingNews.layoutManager = layoutManager
        adapter = BreakingNewsAdapter()

        binding.rvBreakingNews.adapter =
            adapter.withLoadStateHeaderAndFooter(
                footer = LoadingStateAdapter { adapter.retry() },
                header = LoadingStateAdapter { adapter.retry() }
            )

        lifecycleScope.launch {
            try {
                showLoading(false)
                viewModel.getBreakingNews().observe(viewLifecycleOwner) {
                    adapter.submitData(lifecycle, it)
                }
            }catch (e:Exception){
                showToast(e.message.toString())
            }
        }

    }

        private fun showLoading(isLoading: Boolean) {
            binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        private fun showToast(message: String) {
            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
}