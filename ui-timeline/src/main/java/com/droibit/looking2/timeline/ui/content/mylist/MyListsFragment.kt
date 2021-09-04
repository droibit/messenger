package com.droibit.looking2.timeline.ui.content.mylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.cash.exhaustive.Exhaustive
import com.droibit.looking2.core.model.tweet.UserList
import com.droibit.looking2.timeline.databinding.FragmentMyListsBinding
import com.droibit.looking2.timeline.ui.content.TimelineSource
import com.droibit.looking2.timeline.ui.content.mylist.MyListsFragmentDirections.Companion.toMyListTimeline
import com.droibit.looking2.timeline.ui.widget.ListDividerItemDecoration
import com.droibit.looking2.ui.common.ext.addCallback
import com.droibit.looking2.ui.common.ext.navigateSafely
import com.droibit.looking2.ui.common.ext.showToast
import com.droibit.looking2.ui.common.widget.PopBackSwipeDismissCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import timber.log.Timber

@AndroidEntryPoint
class MyListsFragment : Fragment(), MyListAdapter.OnItemClickListener {

    @Inject
    lateinit var myListAdapter: MyListAdapter

    @Inject
    lateinit var swipeDismissCallback: PopBackSwipeDismissCallback

    private val viewModel: MyListsViewModel by viewModels()

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
        binding.swipeDismissLayout.addCallback(viewLifecycleOwner, swipeDismissCallback)

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
        myListAdapter.submitList(myLists)
    }

    private fun showGetMyListsError(error: GetMyListsErrorMessage) {
        @Exhaustive
        when (error) {
            is GetMyListsErrorMessage.Toast -> showToast(error)
        }
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onUserListClick(myList: UserList) {
        Timber.d("onUserListClick(${myList.name})")
        findNavController().navigateSafely(
            toMyListTimeline(
                TimelineSource.MyLists(myList.id)
            )
        )
    }
}
