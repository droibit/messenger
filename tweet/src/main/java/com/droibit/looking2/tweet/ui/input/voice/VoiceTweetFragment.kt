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
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.wear.widget.CircularProgressLayout
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.core.ui.widget.PopBackSwipeDismissCallback
import com.droibit.looking2.core.util.ext.observeIfNotConsumed
import com.droibit.looking2.tweet.databinding.FragmentTweetVoiceBinding
import com.droibit.looking2.tweet.ui.input.TweetLayoutString
import com.droibit.looking2.tweet.ui.input.TweetViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import javax.inject.Named
import com.droibit.looking2.ui.Activities.Confirmation.SuccessIntent as SuccessConfirmationIntent

private const val REQUEST_CODE_SPEECH = 1

class VoiceTweetFragment : DaggerFragment(),
    CircularProgressLayout.OnTimerFinishedListener {

    @Inject
    lateinit var layoutString: TweetLayoutString

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @field:[Inject Named("waitDurationMillis")]
    @JvmField
    var waitDurationMillis: Long = 0

    @Inject
    lateinit var swipeDismissCallback: PopBackSwipeDismissCallback

    @Inject
    lateinit var contentPadding: ShapeAwareContentPadding

    private val viewModel: TweetViewModel by viewModels { viewModelFactory }

    private lateinit var binding: FragmentTweetVoiceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTweetVoiceBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.fragment = this
            it.viewModel = this.viewModel
            it.strings = layoutString
            it.contentPadding = contentPadding
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeDismissLayout.addCallback(swipeDismissCallback)

        binding.circularProgress.apply {
            onTimerFinishedListener = this@VoiceTweetFragment
            totalTime = waitDurationMillis
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeTweetCompleted()

        if (savedInstanceState == null) {
            navigateRecognizeSpeech()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_SPEECH -> onRecognizeSpeechResult(resultCode, data)
        }
    }

    private fun observeTweetCompleted() {
        viewModel.tweetCompleted.observeIfNotConsumed(viewLifecycleOwner) {
            val intent = SuccessConfirmationIntent(requireContext(), messageResId = null)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        binding.swipeDismissLayout.removeCallback(swipeDismissCallback)
        binding.circularProgress.onTimerFinishedListener = null
        super.onDestroyView()
    }

    private fun navigateRecognizeSpeech() {
        try {
            val intent = Intent(ACTION_RECOGNIZE_SPEECH)
                .putExtra(EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_FREE_FORM)
                .putExtra(EXTRA_PROMPT, layoutString.title)
            startActivityForResult(intent, REQUEST_CODE_SPEECH)
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