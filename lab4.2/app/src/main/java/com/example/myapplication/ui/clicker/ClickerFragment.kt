package com.example.myapplication.ui.clicker

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentClickerBinding
import com.example.myapplication.ui.GameViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ClickerFragment : Fragment(R.layout.fragment_clicker) {
    private lateinit var binding: FragmentClickerBinding
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding = FragmentClickerBinding.bind(view)

        binding.cookieButton.setOnClickListener {
            gameViewModel.onCookieClicked()
            animateCookie()
        }

        gameViewModel.gameState
            .onEach { state ->
                    binding.count.text = getString(R.string.cookie_count, state.count)
                    binding.perSecond.text = getString(R.string.per_second, state.cookiesPerSecond)
                    binding.time.text = getString(R.string.time, state.elapsedTime)
                    binding.averageSpeed.text = getString(R.string.average_speed, state.cookiesPerSecond * 60.0)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun animateCookie() {
        binding.cookieButton.animate()
            .scaleX(0.8f).scaleY(0.8f)
            .setDuration(100)
            .withEndAction {
                binding.cookieButton.animate().scaleX(1f).scaleY(1f).duration = 100
            }
    }
}

