package com.zininegor.task5.waitroomclient

import android.content.ContentValues
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.zininegor.task5.R
import com.zininegor.task5.databinding.WaitRoomClientFragmentBinding
import com.zininegor.task5.others.Client
import com.zininegor.task5.others.GameData
import com.zininegor.task5.others.Host
import kotlinx.coroutines.*

class WaitRoomClientFragment : Fragment() {

    val database = Firebase.database.reference
    private val user = Firebase.auth.currentUser!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: WaitRoomClientFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.wait_room_client_fragment, container, false
        )

        val viewModel = ViewModelProvider(this).get(WaitRoomClientViewModel::class.java)

        val roomId = WaitRoomClientFragmentArgs.fromBundle(requireArguments()).roomId

        binding.lifecycleOwner = this


        viewModel.doneNavigation.observe(viewLifecycleOwner, {
            if (viewModel.doneNavigation.value == true) {
                viewModel.doneNavigation.value = false
                this.findNavController().navigate(
                    WaitRoomClientFragmentDirections.actionWaitRoomClientFragmentToMenuFragment()
                )
            }
        })

        viewModel.doneNavigationToGame.observe(viewLifecycleOwner, {
            if (viewModel.doneNavigationToGame.value == true) {
                binding.start.visibility = View.VISIBLE
            }
        })

        fun writeClient(roomId: String, email: String, nickname: String, status: String) {
            val client =
                Host(nickname, email, status = status, user.photoUrl.toString())
            database.child(roomId).child("Client").setValue(client)
        }
        writeClient(roomId.replace(".", ""), user.email!!, user.displayName!!, "Ready")

        fun setImage(uri: Uri, imageView: ImageView) {
            Glide.with(this)
                .load(uri)
                .into(object : ViewTarget<ImageView, Drawable>(imageView) {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        imageView.setImageDrawable(resource)
                    }
                })
        }
        binding.textView10.text = roomId
        val hostListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (activity == null) {
                    return
                }
                val host = dataSnapshot.child(roomId.replace(".", "")).child("Host")
                    .getValue<Host>()
                val client = dataSnapshot.child(roomId.replace(".", "")).child("Client")
                    .getValue<Client>()
                val gameData = dataSnapshot.child(roomId.replace(".", "")).child("GameData")
                    .getValue<GameData>()

                if (gameData != null) {
                    viewModel.doneNavigationToGame.value = true
                }
                if (host != null) {
                    binding.textView7.text = host.nickname
                    setImage(Uri.parse(host.urlAvatar), binding.imageView)
                } else {
                    viewModel.doneNavigation.value = true

                }
                if (client != null) {
                    binding.nicknameOpponent.text = client.nickname
                    setImage(Uri.parse(client.urlAvatar), binding.imageView2)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        binding.exit.setOnClickListener()
        {
            fun writeClient(roomId: String, email: String, nickname: String, status: String) {
                val client =
                    Host(
                        nickname,
                        email,
                        status = status,
                        user.photoUrl.toString()
                    )
                database.child(roomId).child("Client").setValue(client)
            }
            writeClient(
                WaitRoomClientFragmentArgs.fromBundle(requireArguments()).roomId.replace(
                    ".",
                    ""
                ), user.email!!, user.displayName!!, "notReady"
            )
            this.findNavController()
                .navigate(
                    WaitRoomClientFragmentDirections
                        .actionWaitRoomClientFragmentToMenuFragment()
                )
        }
        database.addValueEventListener(hostListener)
        binding.start.setOnClickListener()
        {
            this.findNavController().navigate(
                WaitRoomClientFragmentDirections.actionWaitRoomClientFragmentToGameClientFragment(
                    roomId.replace(".", "")
                )
            )
        }

        return binding.root
    }

}