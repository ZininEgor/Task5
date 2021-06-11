package com.zininegor.task5.gameclient

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.zininegor.task5.R
import com.zininegor.task5.databinding.GameClientFragmentBinding
import com.zininegor.task5.game.MyMap
import com.zininegor.task5.game.TitleWorkoutAdapter
import com.zininegor.task5.game.TitleWorkoutListener
import com.zininegor.task5.others.Client
import com.zininegor.task5.others.EndGame
import com.zininegor.task5.others.GameData
import com.zininegor.task5.others.Host
import com.zininegor.task5.waitroomclient.WaitRoomClientFragmentArgs
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class GameClientFragment : Fragment() {

    val database = Firebase.database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: GameClientFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.game_client_fragment, container, false
        )
        val roomId = WaitRoomClientFragmentArgs.fromBundle(requireArguments()).roomId
        val viewModel = ViewModelProvider(this).get(GameClientViewModel::class.java)
        binding.lifecycleOwner = this
        viewModel.roomId = roomId

        val manager = GridLayoutManager(activity, 3)
        binding.recyclerView2.layoutManager = manager

        viewModel.navigateToFinish.observe(viewLifecycleOwner,
            {
                if (it) {
                    this.findNavController().navigate(
                        GameClientFragmentDirections.actionGameClientFragmentToFinishFragment(
                            roomId, viewModel.win, false, viewModel.draw,
                            binding.HostNick.text as String
                        )
                    )
                }
            })
        val adapter = TitleWorkoutAdapter(TitleWorkoutListener { nightId ->
            viewModel.onClicked(nightId)
        })

        val hostListener = object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (activity == null) {
                    return
                }
                var stepOwner = dataSnapshot.child(roomId).child("GameStep").child("stepOwner")
                    .getValue<String>()
                val host =
                    dataSnapshot.child(roomId).child("Host").getValue<Host>()
                val client =
                    dataSnapshot.child(roomId).child("Client").getValue<Client>()
                val gameData = dataSnapshot.child(roomId).child("GameData")
                    .getValue<GameData>()
                val endGame =
                    dataSnapshot.child(roomId).child("GameEnd").getValue<EndGame>()
                if (endGame != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(TimeUnit.SECONDS.toMillis(2))
                        withContext(Dispatchers.Main) {
                            if (endGame.navigate) {
                                viewModel.finish(endGame)
                            }
                        }
                    }

                }

                if (stepOwner != null) {
                    if (stepOwner == "host") {
                        binding.hostNick2.text = "X"
                        binding.hostNick2.setTextColor(resources.getColor(R.color.danger))
                    } else {
                        binding.hostNick2.text = "O"
                        binding.hostNick2.setTextColor(resources.getColor(R.color.yellow))
                    }
                    viewModel.stepOwner.value = stepOwner
                }
                if (host != null) {
                    binding.HostNick.text = host.nickname
                    setImage(Uri.parse(host.urlAvatar), binding.imageView)
                }
                if (gameData != null) {
                    val list: MutableList<MyMap> = mutableListOf()
                    for (i in gameData.matrix!!) {
                        var myMap = MyMap(map = i)
                        list.add(myMap)
                    }
                    adapter.notifyDataSetChanged()
                    Log.i("Adapter", list.last().map?.keys!!.first())
                    viewModel.list.value = list
                }
                if (client != null) {
                    binding.ClientNick.text = client.nickname
                    setImage(Uri.parse(client.urlAvatar), binding.imageView3)
                    if (client.status == "Ready") {
                    } else {

                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(hostListener)

        viewModel.list.observe(viewLifecycleOwner, {

            binding.recyclerView2.adapter = adapter
            it?.let {
                adapter.submitList(it)
                Log.i("Adapter", it.count().toString())

            }
            binding.recyclerView2.smoothScrollToPosition(0)
        })
        adapter.notifyDataSetChanged()

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