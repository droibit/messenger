package com.droibit.looking2.timeline.ui.content.mylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import com.droibit.looking2.core.model.tweet.UserList
import com.droibit.looking2.core.util.Event
import com.droibit.looking2.core.util.ext.exhaustive
import com.droibit.looking2.core.util.ext.showNetworkErrorToast
import com.droibit.looking2.core.util.ext.showRateLimitingErrorToast
import com.droibit.looking2.core.util.ext.showShortToast
import com.droibit.looking2.timeline.databinding.FragmentMyListsBinding
import com.droibit.looking2.timeline.ui.content.mylist.GetMyListsResult.FailureType as GetMyListsFailureType
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

class MyListsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var myListAdapter: MyListAdapter

    private val viewModel: MyListsViewModel by viewModels { viewModelFactory }

    private lateinit var binding: FragmentMyListsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyListsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.list.apply {
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            this.adapter = myListAdapter
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeGetMyListsResult()

        lifecycle.addObserver(viewModel)
    }

    private fun observeGetMyListsResult() {
        viewModel.getMyListsResult.observe(viewLifecycleOwner) {
            when (it) {
                is GetMyListsResult.Success -> showMyLists(it.myLists)
                is GetMyListsResult.Failure -> showGetMyListsFailureResult(it.type)
            }
            binding.loadingInProgress = it is GetMyListsResult.InProgress
        }.exhaustive
    }

    private fun showMyLists(myLists: List<UserList>) {
        myListAdapter.setMyLists(myLists)
    }

    private fun showGetMyListsFailureResult(failureType: Event<GetMyListsFailureType>) {
        failureType.consume()?.let {
            when (it) {
                is GetMyListsFailureType.Network -> showNetworkErrorToast()
                is GetMyListsFailureType.UnExpected -> showShortToast(it.messageResId)
                is GetMyListsFailureType.Limited -> showRateLimitingErrorToast()
            }
            requireActivity().finish()
        }
    }

    fun onUserListClick(myList: UserList) {
        Timber.d("onUserListClick(${myList.name})")
    }
}