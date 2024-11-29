package com.example.myapplication.ui.buildings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentBuildingsBinding
import com.example.myapplication.ui.GameViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BuildingsFragment : Fragment(R.layout.fragment_buildings) {
    private var _binding: FragmentBuildingsBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuildingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BuildingsAdapter { building ->
            gameViewModel.buyBuilding(building)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        gameViewModel.gameState
            .onEach { gameState ->
                adapter.submitList(gameState.buildings)
                gameViewModel.updateBuildingsAvailability()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        gameViewModel.toast
            .onEach { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}