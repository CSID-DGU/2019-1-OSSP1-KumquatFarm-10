package com.kakao.sdk.newtone.sample.feature

import ai.api.AIConfiguration
import ai.api.AIDataService
import ai.api.android.AIService
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kakao.sdk.newtone.sample.R
import com.kakao.sdk.newtone.sample.adapter.ChatAdapter
import com.kakao.sdk.newtone.sample.entity.ChatMessage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainContract.View {

    lateinit var adapter: ChatAdapter
    lateinit var ref: DatabaseReference
    lateinit var aiService: AIService
    lateinit var aiDataAIService: AIDataService
    var user: String? = null

    lateinit var mPresenter : MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPresenter()

        rvChat.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        rvChat.layoutManager = layoutManager
        ref.keepSynced(true)

        btnSend.setOnClickListener {
            val message = edChat.text.toString()
            if (message != "") {
                mPresenter.sendMessage(message)
            } else {
                aiService.startListening()
                Toast.makeText(applicationContext, "Enter message first", Toast.LENGTH_SHORT).show()
            }
            edChat.setText("")
        }

        val options = FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(ref.child("chat"), ChatMessage::class.java)
                .build()

        adapter = ChatAdapter(options)

        rvChat.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)

                val msgCount = adapter.itemCount
                val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()

                if (lastVisiblePosition == -1 || positionStart >= msgCount - 1 && lastVisiblePosition == positionStart - 1) {
                    rvChat.scrollToPosition(positionStart)
                }
            }
        })

    }




    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    fun initPresenter(){
        val aiConfiguration = ai.api.android.AIConfiguration("b5a055ab32ed4e3b8f4a1a5bb54701fb",
                AIConfiguration.SupportedLanguages.Korean,
                ai.api.android.AIConfiguration.RecognitionEngine.System)

        aiService = AIService.getService(this, aiConfiguration)
        aiDataAIService = AIDataService(aiConfiguration)
        ref = FirebaseDatabase.getInstance().reference

        mPresenter = MainPresenter(aiDataAIService, ref)

    }
}
