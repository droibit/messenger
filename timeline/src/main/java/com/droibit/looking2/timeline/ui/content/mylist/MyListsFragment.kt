package com.droibit.looking2.timeline.ui.content.mylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droibit.looking2.core.model.tweet.UserList
import com.droibit.looking2.timeline.databinding.FragmentMyListsBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class MyListsFragment : DaggerFragment() {

    @Inject
    lateinit var myListAdapter: MyListAdapter

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

        binding.list.adapter = myListAdapter
    }

    fun onUserListClick(myList: UserList) {

    }
}