package com.example.arlhambra.ui.share

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.arlhambra.R
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class ShareFragment : Fragment() {

    private lateinit var shareViewModel: ShareViewModel
    val GALLERY = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        shareViewModel =
            ViewModelProviders.of(this).get(ShareViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_share, container, false)
        val textView: TextView = root.findViewById(R.id.text_share)
        shareViewModel.text.observe(this, Observer {
            textView.text = it
        })
        val shareButton : Button = root.findViewById(R.id.shareBut)
        shareButton?.setOnClickListener {
            choosePhotoFromGallery()
        }
        return root
    }
    private fun choosePhotoFromGallery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val packageManager = activity!!.packageManager
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, GALLERY)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode ==  GALLERY){

            val image = data?.data
            val sharingIntent = Intent(Intent.ACTION_SEND);
            sharingIntent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET))
            sharingIntent.setType("image/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, image);
            startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));

        }

    }

}