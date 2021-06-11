package com.zininegor.task5.game

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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.zininegor.task5.R
import com.zininegor.task5.databinding.GameHostFragmentBinding
import com.zininegor.task5.others.*
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class GameHostFragment : Fragment() {

    var nav = true
    val database = Firebase.database.reference
    private val user = Firebase.auth.currentUser!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: GameHostFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.game_host_fragment, container, false
        )

        val viewModel = ViewModelProvider(this).get(GameHostViewModel::class.java)

        binding.lifecycleOwner = this

        val manager = GridLayoutManager(activity, 3)
        binding.recyclerView2.layoutManager = manager


        val adapter = TitleWorkoutAdapter(TitleWorkoutListener { nightId ->
            viewModel.onClicked(nightId)
        })

        fun checkEndGame() {
            val check = viewModel.list.value?.let { GameLogic(it) }
            var winner = ""
            var loser = ""
            var draw = false
            if (check!!.isNotEmpty()) {
                if (check == "X") {
                    winner = "X"
                    loser = "O"
                }
                if (check == "O") {
                    winner = "O"
                    loser = "X"
                }
                if (check == "dead heat") {
                    draw = true
                }


                database.child(user.email!!.replace(".", "")).child("GameEnd")
                    .setValue(
                        EndGame(
                            winner = winner,
                            loser = loser,
                            draw = draw,
                            navigate = nav
                        )
                    )

                nav = false
            }
        }

        val hostListener = object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (activity == null) {
                    return
                }
                var stepOwner = dataSnapshot.child(user.email!!.replace(".", "")).child("GameStep")
                    .child("stepOwner").getValue<String>()
                val host = user.email?.replace(".", "")
                    ?.let { dataSnapshot.child(it).child("Host").getValue<Host>() }
                val endGame =
                    dataSnapshot.child(user.email?.replace(".", "")!!).child("GameEnd")
                        .getValue<EndGame>()
                val client = user.email?.replace(".", "")?.let {
                    dataSnapshot.child(it).child("Client").getValue<Client>()
                }
                val gameData = user.email?.replace(".", "")?.let {
                    dataSnapshot.child(it).child("GameData")
                        .getValue<GameData>()
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
                    checkEndGame()
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
        viewModel.navigateToFinish.observe(viewLifecycleOwner,
            {
                if (it) {
                    this.findNavController().navigate(
                        GameHostFragmentDirections.actionGameHostFragmentToFinishFragment(
                            user.email?.replace(
                                ".",
                                ""
                            ).toString(),
                            viewModel.win,
                            true,
                            viewModel.draw,
                            binding.ClientNick.text as String
                        )
                    )
                }
            })
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