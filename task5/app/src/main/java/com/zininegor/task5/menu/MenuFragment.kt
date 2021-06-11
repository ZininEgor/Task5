package com.zininegor.task5.menu

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.zininegor.task5.R
import com.zininegor.task5.databinding.MenuFragmentBinding
import com.zininegor.task5.others.Case
import com.zininegor.task5.others.Host

class MenuFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val user = Firebase.auth.currentUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: MenuFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.menu_fragment, container, false
        )
        var database = Firebase.database.reference
        val viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        binding.lifecycleOwner = this

        this.context?.let { FirebaseApp.initializeApp(/*context=*/ it) }
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance())

        viewModel.doneNavigation.observe(viewLifecycleOwner, {
            if (viewModel.doneNavigation.value == true) {
                viewModel.doneNavigation.value = false
                this.findNavController().navigate(
                    MenuFragmentDirections
                        .actionMenuFragmentToWaitRoomClientFragment(binding.editTextTextEmailAddress.text.toString())
                )
            }
        })
        val adapter = StatAdapter()
        binding.recyclerView.adapter = adapter
        viewModel.list.observe(viewLifecycleOwner,
            {
                adapter.submitList(it)
            })

        binding.email.text = user!!.email
        binding.displayName.text = user!!.displayName
        setImage(Uri.parse(user.photoUrl.toString()), binding.imageView)
        binding.button2.setOnClickListener()
        {
            Firebase.auth.signOut()
            this.findNavController().navigate(
                MenuFragmentDirections
                    .actionMenuFragmentToMainFragment()
            )
        }
        if (user.displayName.isNullOrEmpty()) {
            this.findNavController().navigate(
                MenuFragmentDirections
                    .actionMenuFragmentToProfileFragment2()
            )
        }

        val hostListener = object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (activity == null) {
                    return
                }
                var stats = dataSnapshot.child("stats").child(user.email!!.replace(".", ""))
                    .getValue<MutableList<Case>>()

                if (stats != null) {
                    viewModel.list.value = stats
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(hostListener)

        binding.button3.setOnClickListener()
        {
            val hostListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val host = dataSnapshot.child(
                        binding.editTextTextEmailAddress.text.toString().replace(
                            ".",
                            ""
                        )
                    ).child("Host").getValue<Host>()
                    if (host != null) {
                        viewModel.doneNavigation.value = true
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
                }
            }
            database.addValueEventListener(hostListener)
        }

        binding.button4.setOnClickListener()
        {
            this.findNavController().navigate(
                MenuFragmentDirections
                    .actionMenuFragmentToWaitRoomFragment()
            )
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_meny, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        )
                || super.onOptionsItemSelected(item)
    }


    private fun setImage(uri: Uri, imageView: ImageView) {
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