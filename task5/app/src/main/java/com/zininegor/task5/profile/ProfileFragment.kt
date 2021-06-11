package com.zininegor.task5.profile

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.zininegor.task5.R
import com.zininegor.task5.databinding.ProfileFragmentBinding
import java.util.*


class ProfileFragment : Fragment() {

    private var filePath: Uri? = null
    private val storageRef = Firebase.storage.reference
    private val user = Firebase.auth.currentUser
    private val PICK_IMAGE_REQUEST = 71
    private var imageView: ImageView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val userEmail = user?.email

        val binding: ProfileFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.profile_fragment, container, false
        )

        binding.lifecycleOwner = this
        imageView = binding.imageView

        if (user != null) {
            binding.textView3.setText(user.displayName)
            binding.textviewEmail.text = userEmail
            setImage(Uri.parse(user.photoUrl.toString()), binding.imageView)
        }

        binding.gravatar.setOnClickListener()
        {
            val url = "https://eu.ui-avatars.com/api/?name=${user?.displayName}&size=240"
            val profileUpdates = userProfileChangeRequest {
                displayName = binding.textView3.text.toString()
                photoUri =
                    Uri.parse("https://eu.ui-avatars.com/api/?name=${user?.displayName}&size=240")
            }
            user!!.updateProfile(profileUpdates)
            setImage(Uri.parse(url), binding.imageView)
        }
        fun uploadImage() {
            if (filePath != null) {
                val progressDialog = ProgressDialog(context)
                progressDialog.setTitle("Uploading...")
                progressDialog.show()

                val ref: StorageReference = storageRef.child("images/" + user!!.email)
                ref.putFile(filePath!!)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(context, "Image Uploaded!!", Toast.LENGTH_SHORT).show()
                        storageRef.child("images/${userEmail}").downloadUrl.addOnSuccessListener {
                            val profileUpdates = userProfileChangeRequest {
                                displayName = binding.textView3.text.toString()
                                photoUri = it
                            }
                            user.updateProfile(profileUpdates)
                            setImage(it, binding.imageView)
                        }
                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(context, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnProgressListener { taskSnapshot ->
                        val progress =
                            (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                        progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
                    }
            }
        }

        binding.button.setOnClickListener()
        {
            uploadImage()

            val profileUpdates = userProfileChangeRequest {
                displayName = binding.textView3.text.toString()
            }
            user!!.updateProfile(profileUpdates)

        }
        binding.imageView.setOnClickListener()
        {
            selectImage()
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {

            filePath = data.data
            filePath?.let {
                imageView?.let { it1 -> setImage(it, it1) }
            }
        }
    }

    private fun selectImage() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Image from here..."),
            PICK_IMAGE_REQUEST
        )
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