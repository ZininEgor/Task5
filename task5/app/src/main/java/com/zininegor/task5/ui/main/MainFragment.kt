package com.zininegor.task5.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zininegor.task5.R
import com.zininegor.task5.databinding.MainFragmentBinding

class MainFragment : Fragment() {


    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: MainFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.main_fragment, container, false
        )
        binding.lifecycleOwner = this
        auth = Firebase.auth


        val user = Firebase.auth.currentUser
        if (user != null) {
            this.findNavController().navigate(
                MainFragmentDirections
                    .actionMainFragmentToMenuFragment()
            )
        }
        fun signIn(email: String, password: String) {
            activity?.let {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(it) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            this.findNavController().navigate(
                                MainFragmentDirections
                                    .actionMainFragmentToMenuFragment()
                            )
                            Toast.makeText(
                                context, "Authentication work.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
        binding.signin.setOnClickListener()
        {
            if (binding.login.text.toString().isNotEmpty() || binding.password.text.toString()
                    .isNotEmpty()
            ) {
                signIn(binding.login.text.toString(), binding.password.text.toString())
            }
        }
        binding.register.setOnClickListener()
        {
            this.findNavController().navigate(
                MainFragmentDirections
                    .actionMainFragmentToRegistrationFragment()
            )
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
    }


}