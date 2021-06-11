package com.zininegor.task5.fininsh

import android.annotation.SuppressLint
import android.content.ContentValues
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.zininegor.task5.R
import com.zininegor.task5.databinding.FinishFragmentBinding
import com.zininegor.task5.others.Case
import kotlinx.coroutines.*


class FinishFragment : Fragment() {
    val database = Firebase.database.reference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FinishFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.finish_fragment, container, false
        )
        val user = Firebase.auth.currentUser!!


        val viewModel = ViewModelProvider(this).get(FinishViewModel::class.java)
        val args = FinishFragmentArgs.fromBundle(requireArguments())
        val roomId = args.roomId
        val win = args.winner
        val draw = args.draw
        val nickOpponent = args.nick
        val host = args.host
        var status = ""
        binding.lifecycleOwner = this
        if (win) {
            binding.textView12.text = "You won"
            status = "Win"
        } else {
            binding.textView12.text = "You lose"
            status = "Lose"
        }
        if (draw) {
            binding.textView12.text = "Draw"
            status = "Draw"
        }


        val hostListener = object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (activity == null) {
                    return
                }
                var stats = dataSnapshot.child("stats").child(user.email!!.replace(".",""))
                    .getValue<MutableList<Case>>()

                if(stats != null)
                {
                    viewModel.list = stats
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(hostListener)
        binding.button6.setOnClickListener()
        {
            viewModel.addStats(status=status, nicknameOpponent = nickOpponent)
            if (host) {
                database.child(roomId).child("GameData").removeValue()
                this.findNavController().navigate(
                    FinishFragmentDirections.actionFinishFragmentToWaitRoomFragment()
                )

            } else {
                database.child(roomId).child("GameData").removeValue()
                this.findNavController().navigate(
                    FinishFragmentDirections.actionFinishFragmentToWaitRoomClientFragment(roomId)
                )
            }
        }
        return binding.root
    }
}
