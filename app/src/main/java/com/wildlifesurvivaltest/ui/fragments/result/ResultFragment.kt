package com.wildlifesurvivaltest.ui.fragments.result

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.os.ConfigurationCompat
import androidx.fragment.app.viewModels
import com.wildlifesurvivaltest.R
import com.wildlifesurvivaltest.databinding.FragmentResultBinding
import com.wildlifesurvivaltest.ui.base.BaseFragment
import com.wildlifesurvivaltest.utils.MAX_POINTS
import com.wildlifesurvivaltest.utils.extentions.setOnClickListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.wildlifesurvivaltest.utils.extentions.disable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : BaseFragment() {

    private lateinit var binding: FragmentResultBinding
    private val viewModel: ResultViewModel by viewModels()

    private lateinit var backPressedCallback: OnBackPressedCallback
    private var resultText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        viewModel.initBilling()
        viewModel.refreshGameInfo()
    }

    override fun onResume() {
        super.onResume()
        backPressedCallback = requireActivity()
            .onBackPressedDispatcher
            .addCallback { activity?.finish() }
    }

    override fun onPause() {
        super.onPause()
        backPressedCallback.remove()
    }

    override fun initUI() {
        setupButtons()
        setupTextResult()
    }

    private fun initObservers() {
        viewModel.skuDetails.observe(viewLifecycleOwner) { scuDetails ->
            if (scuDetails?.price != null) {
                binding.tvResultNotice.text = getString(R.string.result_text_full, scuDetails.price)
            } else {
                binding.tvResultNotice.text = getString(R.string.result_text_notice)
                binding.btnPay.disable()
            }
        }
    }

    private fun setupButtons() = binding.run {
        btnRestart.setOnClickListener(500) {
            if (!viewModel.isConnected) {
                showErrorDialog(
                    R.string.dialog_connection_error_title,
                    R.string.dialog_connection_error_description,
                    R.string.dialog_connection_error_button
                )
                return@setOnClickListener
            }
            navigateBack()
            viewModel.gameBegun = true
            sendEvent(getString(R.string.event_name_restart))
        }
        btnShare.setOnClickListener(500) {
            share()
            sendEvent(FirebaseAnalytics.Event.SHARE)
        }
        btnPay.setOnClickListener(500) {
            viewModel.launchBillingFlow(requireActivity())
            sendEvent(FirebaseAnalytics.Event.PURCHASE)
        }
        btnRateUs.setOnClickListener(500) {
            rateUs()
            sendEvent(getString(R.string.event_name_rate_us))
        }
    }

    private fun setupTextResult() {
        val result = ((viewModel.points.toDouble() / MAX_POINTS) * 100).toInt()
        resultText = when {
            result < 20 -> getString(R.string.result_text_result_definitely_not_survive, result)
            result < 40 -> getString(R.string.result_text_result_probably_not_survive, result)
            result < 60 -> getString(R.string.result_text_result_close_to_survive, result)
            result < 80 -> getString(R.string.result_text_result_survive, result)
            else -> getString(R.string.result_text_result_definitely_survive, result)
        }
        binding.tvResultDescription.text = resultText
    }

    private fun share() {
        val shareText = getString(R.string.result_test_share, resultText)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = SHARE_TEXT_TYPE
        }

        startActivity(Intent.createChooser(sendIntent, null))
    }

    private fun rateUs() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URI)))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_URI)))
        }
    }

    private fun sendEvent(eventName: String) {
        val bundle = Bundle().apply {
            putString(
                getString(R.string.event_parameter_locale),
                ConfigurationCompat.getLocales(resources.configuration)[0].language
            )
        }
        FirebaseAnalytics.getInstance(requireContext()).logEvent(eventName, bundle)
    }

    companion object {
        private const val SHARE_TEXT_TYPE = "text/plain"
        private const val MARKET_URI = "market://details?id=com.wildlifesurvivaltest"
        private const val GOOGLE_PLAY_URI =
            "https://play.google.com/store/apps/details?id=com.wildlifesurvivaltest"
    }

}