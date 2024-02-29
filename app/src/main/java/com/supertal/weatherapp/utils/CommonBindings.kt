package com.supertal.weatherapp.utils

import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

object CommonBindings {

    @JvmStatic
    @BindingAdapter("set_image")
    fun bindImageFromUrl(imageView: AppCompatImageView, imageUrl: String?) {
        imageUrl?.apply {
            Picasso.get()?.load("https:$imageUrl")?.into(imageView)
        }


    }
}