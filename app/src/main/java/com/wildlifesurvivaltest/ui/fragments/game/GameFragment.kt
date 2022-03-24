package com.wildlifesurvivaltest.ui.fragments.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wildlifesurvivaltest.R
import com.wildlifesurvivaltest.data.room.entity.Answer
import com.wildlifesurvivaltest.databinding.FragmentGameBinding
import com.wildlifesurvivaltest.ui.base.BaseFragment
import com.wildlifesurvivaltest.ui.fragments.game.adapter.GameRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.gms.ads.AdRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.wildlifesurvivaltest.utils.extentions.setOnClickListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.FullScreenContentCallback

import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GameFragment : BaseFragment() {

    private lateinit var binding: FragmentGameBinding
    private val viewModel: GameViewModel by viewModels()
    private val adapter by lazy { GameRecyclerViewAdapter() }

    private var currentQuestionNumber: Int = 0
    private var totalQuestionsNumber: Int = 0
    private var currentAnswerPoints: Int = 0
    private var clickedAnswerPosition: Int? = null
    private var currentAnswerList: MutableList<Answer?> = mutableListOf()
    private var eventList: MutableMap<String, String> = mutableMapOf()

    private var interstitialAd: InterstitialAd? = null
    private lateinit var fullScreenContentCallback: FullScreenContentCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater)

        fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
            }
        }
        loadVideoAd()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        loadQuestionInformation()
        loadBannerAd()
    }

    override fun initUI() {
        setupAdapter()
        setupNextListener()
    }

    private fun setupAdapter() {
        binding.rvAnswers.adapter = adapter
        adapter.listener = { position, points ->
            if (clickedAnswerPosition != null) {
                currentAnswerList[clickedAnswerPosition!!]?.pressed = false
                adapter.notifyItemChanged(clickedAnswerPosition!!)
            }
            clickedAnswerPosition = position
            currentAnswerPoints = points
        }
    }

    private fun setupNextListener() {
        binding.btnNext.setOnClickListener(500) {
            if (clickedAnswerPosition == null) {
                toast(R.string.toast_choose_error)
                return@setOnClickListener
            }
            if (!viewModel.isConnected) {
                showErrorDialog(
                    R.string.dialog_connection_error_title,
                    R.string.dialog_connection_error_description,
                    R.string.dialog_connection_error_button
                )
                return@setOnClickListener
            }

            val eventName = eventList[currentQuestionNumber.toString()]
            if (eventName != null) sendAnswerEvent(eventName, clickedAnswerPosition ?: 404)

            clickedAnswerPosition = null
            viewModel.points += currentAnswerPoints

            if (currentQuestionNumber % 20 == 0) {
                if (interstitialAd != null) interstitialAd?.show(requireActivity())
                if (totalQuestionsNumber == viewModel.lastQuestion + 1) {
                    navigate(GameFragmentDirections.actionGameFragmentToResultFragment())
                }
            }

            if (totalQuestionsNumber > viewModel.lastQuestion + 1) {
                viewModel.lastQuestion++
                viewModel.loadQuestion()
            }
        }
    }

    private fun initObservers() {

        viewModel.totalQuestionsNumber.observe(viewLifecycleOwner) {
            totalQuestionsNumber = it
            if (currentQuestionNumber != 0) initQuestionCounter()
            else {
                for (event in 1..totalQuestionsNumber) {
                    eventList[event.toString()] =
                        getString(R.string.event_name_question_form, event)
                }
            }
        }

        viewModel.currentQuestion.observe(viewLifecycleOwner) {
            setCardViewAnim(R.anim.fade_out)

            if (it == null) return@observe
            currentAnswerList = it.answerList.toMutableList()
            currentQuestionNumber = it.id + 1

            refreshAdapterData()
            initQuestion(it.question)
            if (totalQuestionsNumber != 0) initQuestionCounter()
            changeButtonText(totalQuestionsNumber == currentQuestionNumber)

            setCardViewAnim(R.anim.fade_in)
        }
    }

    private fun setCardViewAnim(@AnimRes animRes: Int) {
        val animationIn: Animation = AnimationUtils.loadAnimation(requireContext(), animRes)
        binding.cvQuestion.startAnimation(animationIn)
    }

    private fun initQuestion(question: String) {
        binding.tvQuestion.text = question
    }

    private fun initQuestionCounter() {
        binding.tvQuestionsCounter.text =
            getString(R.string.game_questions_counter, currentQuestionNumber, totalQuestionsNumber)
    }

    private fun changeButtonText(isFinish: Boolean) {
        binding.btnNext.text = if (isFinish) getText(R.string.game_button_finish)
        else getText(R.string.game_button_next)
    }

    private fun refreshAdapterData() {
        adapter.submitList(currentAnswerList)
    }

    private fun loadQuestionInformation() {
        viewModel.getTotalQuestionsNumber()
        viewModel.loadQuestion()
    }

    private fun loadBannerAd() {
        lifecycleScope.launch(Dispatchers.IO) {
            val adRequest: AdRequest = AdRequest.Builder().build()
            withContext(Dispatchers.Main) {
                binding.adView.loadAd(adRequest)
            }
        }
    }

    private fun loadVideoAd() {
        lifecycleScope.launch(Dispatchers.IO) {
            val adRequest: AdRequest = AdRequest.Builder().build()
            withContext(Dispatchers.Main) {
                InterstitialAd.load(
                    requireContext(),
                    getString(R.string.video_ad_unit_id),
                    adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(ad: InterstitialAd) {
                            interstitialAd = ad
                            interstitialAd!!.fullScreenContentCallback = fullScreenContentCallback
                        }
                    })
            }
        }
    }


}