package com.droibit.looking2.timeline.ui.content.mylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.droibit.looking2.core.model.tweet.UserList
import com.droibit.looking2.core.util.ext.exhaustive
import com.droibit.looking2.core.util.ext.navigateSafely
import com.droibit.looking2.core.util.ext.showToast
import com.droibit.looking2.timeline.databinding.FragmentMyListsBinding
import com.droibit.looking2.timeline.ui.content.TimelineSource
import com.droibit.looking2.timeline.ui.content.mylist.MyListsFragmentDirections.Companion.toMyListTimeline
import com.droibit.looking2.timeline.ui.widget.ListDividerItemDecoration
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

class MyListsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var myListAdapter: MyListAdapter

    private val viewModel: MyListsViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentMyListsBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyListsBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.list.apply {
            this.addItemDecoration(ListDividerItemDecoration(requireContext()))
            this.adapter = myListAdapter
        }

        observeGetMyListsResult()
    }

    private fun observeGetMyListsResult() {
        viewModel.myLists.observe(viewLifecycleOwner) {
            showMyLists(it)
        }

        viewModel.error.observe(viewLifecycleOwner) {
            it.consume()?.let(::showGetMyListsError)
        }
    }

    private fun showMyLists(myLists: List<UserList>) {
        myListAdapter.setMyLists(myLists)
    }

    private fun showGetMyListsError(error: GetMyListsErrorMessage) {
        when (error) {
            is GetMyListsErrorMessage.Toast -> showToast(error)
        }.exhaustive
        requireActivity().finish()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun onUserListClick(myList: UserList) {
        Timber.d("onUserListClick(${myList.name})")
        findNavController().navigateSafely(
            toMyListTimeline(
                TimelineSource.MyLists(myList.id)
            )
        )
    }
}