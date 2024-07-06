package com.example.minigames.ui.gallery

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
import com.example.minigames.adapter.Score
import com.example.minigames.adapter.ScoresAdapter

class RankingFragment : Fragment() {

    private val galleryViewModel: RankingViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var scoresAdapter: ScoresAdapter
    private val scores = mutableListOf<Score>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_ranking, container, false)
        recyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        scoresAdapter = ScoresAdapter(scores)
        recyclerView.adapter = scoresAdapter

        // ViewModel에서 데이터를 가져와 RecyclerView에 반영
        galleryViewModel.scores.observe(viewLifecycleOwner, Observer { scoreList ->
            scores.clear()
            scores.addAll(scoreList)
            scoresAdapter.notifyDataSetChanged()
        })

        // JSON 데이터 로드
        context?.let { galleryViewModel.loadScoresFromJson(it) }

        return root
    }
}