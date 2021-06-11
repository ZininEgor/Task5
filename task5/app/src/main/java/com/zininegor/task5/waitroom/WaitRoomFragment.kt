package com.zininegor.task5.waitroom

import android.annotation.SuppressLint
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
import com.zininegor.task5.databinding.WaitRoomFragmentBinding
import com.zininegor.task5.others.Client
import com.zininegor.task5.others.Host

class WaitRoomFragment : Fragment() {

    private val user = Firebase.auth.currentUser



    val database = Firebase.database.reference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: WaitRoomFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.wait_room_fragment, container, false
        )

        val viewModel = ViewModelProvider(this).get(WaitRoomViewModel::class.java)

        binding.lifecycleOwner = this
        setImage(Uri.parse(user!!.photoUrl.toString()), binding.imageView)
        binding.textView10.text = user.email


        val hostListener = object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (activity == null) {
                    return
                }
                val host = user.email?.replace(".", "")
                    ?.let { dataSnapshot.child(it).child("Host").getValue<Host>() }
                val client = user.email?.replace(".", "")
                    ?.let { dataSnapshot.child(it).child("Client").getValue<Client>() }
                if (host != null) {
                    binding.textView7.text = host.nickname
                }
                if (client != null) {
                    binding.nicknameOpponent.text = client.nickname
                    setImage(Uri.parse(client.urlAvatar), binding.imageView2)
                    if (client.status == "Ready") {
                        binding.readyStatusOpponent.text = "Ready"
                        binding.start.visibility = View.VISIBLE
                        binding.readyStatusOpponent.setTextColor(resources.getColor(R.color.success_dark))
                    } else {
                        binding.readyStatusOpponent.text = "not Ready"
                        binding.start.visibility = View.GONE
                        binding.readyStatusOpponent.setTextColor(resources.getColor(R.color.danger))
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(hostListener)
        binding.close.setOnClickListener()
        {

            user.email?.replace(".", "")?.let { it1 -> database.child(it1).removeValue() }
            this.findNavController().navigate(
                WaitRoomFragmentDirections.actionWaitRoomFragmentToMenuFragment()
            )
        }
        binding.start.setOnClickListener()
        {
            user.email?.replace(".", "")?.let { it1 -> viewModel.CreateGame(it1) }
            this.findNavController().navigate(
                WaitRoomFragmentDirections
                    .actionWaitRoomFragmentToGameHostFragment()
            )
        }
        return binding.root
    }

    fun setImage(uri: Uri, imageView: ImageView) {
        Glide.with(requireContext())
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

}