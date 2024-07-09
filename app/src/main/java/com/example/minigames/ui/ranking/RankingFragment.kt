package com.example.minigames.ui.ranking

import com.example.minigames.adapter.UsersAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minigames.R
import com.example.minigames.server.model.User

class RankingFragment : Fragment() {

    private val rankingViewModel: RankingViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var usersAdapter: UsersAdapter
    private val users = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_ranking, container, false)
        recyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        usersAdapter = UsersAdapter(users)
        recyclerView.adapter = usersAdapter

        // ViewModel에서 데이터를 가져와 RecyclerView에 반영
        rankingViewModel.users.observe(viewLifecycleOwner, Observer { userList->
            users.clear()
            users.addAll(userList)
            usersAdapter.notifyDataSetChanged()
        })

        // 서버에서 사용자 정보 로드
        rankingViewModel.loadUsersFromServer()

        return root
    }
    override fun onResume() {
        super.onResume()
        // 서버에서 사용자 정보 로드
        rankingViewModel.loadUsersFromServer()
    }
}