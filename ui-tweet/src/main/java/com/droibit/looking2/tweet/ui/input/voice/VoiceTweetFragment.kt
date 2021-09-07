package com.droibit.looking2.tweet.ui.input.voice

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH
import android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL
import android.speech.RecognizerIntent.EXTRA_PROMPT
import android.speech.RecognizerIntent.EXTRA_RESULTS
import android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.wear.widget.CircularProgressLayout
import com.droibit.looking2.tweet.R
import com.droibit.looking2.tweet.databinding.FragmentTweetVoiceBinding
import com.droibit.looking2.tweet.ui.TweetHostViewModel
import com.droibit.looking2.tweet.ui.input.TweetViewModel
import com.droibit.looking2.ui.common.Activities.Confirmation.SuccessIntent as SuccessConfirmationIntent
import com.droibit.looking2.ui.common.ext.observeEvent
import com.droibit.looking2.ui.common.view.ShapeAwareContentPadding
import com.droibit.looking2.ui.common.widget.PopBackSwipeDismissCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class VoiceTweetFragment :
    Fragment(),
    CircularProgressLayout.OnTimerFinishedListener {

    @field:[Inject Named("waitDurationMillis")]
    @JvmField
    var waitDurationMillis: Long = 0

    @Inject
    lateinit var swipeDismissCallback: PopBackSwipeDismissCallback

    @Inject
    lateinit var contentPadding: ShapeAwareContentPadding

    private val viewModel: TweetViewModel by viewModels()

    private val hostViewModel: TweetHostViewModel by hiltNavGraphViewModels(R.id.navGraphTweet)

    private var _binding: FragmentTweetVoiceBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val recognizeSpeech = registerForActivityResult(StartActivityForResult()) {
        onRecognizeSpeechResult(it.resultCode, it.data)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTweetVoiceBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.fragment = this
            it.viewModel = this.viewModel
            it.strings = hostViewModel.layoutString
            it.contentPadding = contentPadding
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.circularProgress.apply {
            onTimerFinishedListener = this@VoiceTweetFragment
            totalTime = waitDurationMillis
        }
        binding.swipeDismissLayout.addCallback(swipeDismissCallback)

        observeTweetCompleted()

        if (savedInstanceState == null) {
            navigateRecognizeSpeech()
        }
    }

    override fun onDestroyView() {
        binding.circularProgress.apply {
            if (isTimerRunning) {
                stopTimer()
            }
        }
        _binding = null
        super.onDestroyView()
    }

    private fun observeTweetCompleted() {
        viewModel.tweetCompleted.observeEvent(viewLifecycleOwner) {
            val intent = SuccessConfirmationIntent(requireContext(), messageResId = null)
            startActivity(intent)

            findNavController().popBackStack(R.id.navGraphTweet, inclusive = true)
        }
    }

    private fun navigateRecognizeSpeech() {
        try {
            val intent = Intent(ACTION_RECOGNIZE_SPEECH)
                .putExtra(EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_FREE_FORM)
                .putExtra(EXTRA_PROMPT, hostViewModel.layoutString.title)
            recognizeSpeech.launch(intent)
        } catch (e: ActivityNotFoundException) {
            findNavController().popBackStack()
        }
    }

    private fun onRecognizeSpeechResult(resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            findNavController().popBackStack()
            return
        }

        val spokenText = data?.getStringArrayListExtra(EXTRA_RESULTS)?.firstOrNull()
        if (!spokenText.isNullOrEmpty()) {
            showTweet(tweet = spokenText)
        } else {
            navigateRecognizeSpeech()
        }
    }

    private fun showTweet(tweet: String) {
        viewModel.tweetText.value = tweet
        binding.circularProgress.startTimer()
    }

    fun onCancelTweet() {
        viewModel.tweetText.value = null
        binding.circularProgress.stopTimer()
        navigateRecognizeSpeech()
    }

    override fun onTimerFinished(layout: CircularProgressLayout) {
        viewModel.tweet()
    }
}