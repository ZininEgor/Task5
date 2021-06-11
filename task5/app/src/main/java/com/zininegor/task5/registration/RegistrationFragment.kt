package com.zininegor.task5.registration

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.zininegor.task5.R
import com.zininegor.task5.databinding.RegistrationFragmentBinding


class RegistrationFragment : Fragment() {


    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val user = Firebase.auth.currentUser
        auth = Firebase.auth
        val binding: RegistrationFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.registration_fragment, container, false
        )
        binding.lifecycleOwner = this

        fun createAccount(email: String, password: String) {
            activity?.let {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(it) { task ->
                        if (task.isSuccessful) {
                            val profileUpdates = userProfileChangeRequest {
                                displayName = "nick"
                                photoUri =
                                    Uri.parse("https://eu.ui-avatars.com/api/?name=${"nick"}&size=240")
                            }
                            user?.updateProfile(profileUpdates)
                            Toast.makeText(
                                context, "Authentication success for $email",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("Registration", "createUserWithEmail:success")
                            this.findNavController().navigate(
                                RegistrationFragmentDirections
                                    .actionRegistrationFragmentToMainFragment()
                            )
                        } else {

                            Log.w("Registration", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                context, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
        binding.textViewLogIn.setOnClickListener()
        {
            this.findNavController().navigate(
                RegistrationFragmentDirections
                    .actionRegistrationFragmentToMainFragment()
            )
        }
        binding.register.setOnClickListener()
        {
            createAccount(binding.login.text.toString(), binding.password1.text.toString())
        }
        return binding.root
    }

}