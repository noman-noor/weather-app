package com.supertal.weatherapp.utils

import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.supertal.weatherapp.R
import com.supertal.weatherapp.core.dataModels.DaySession

object CommonBindings {

    @JvmStatic
    @BindingAdapter("set_image")
    fun bindImageFromUrl(imageView: AppCompatImageView, imageUrl: String?) {
        imageUrl?.apply {
            Picasso.get()?.load("https:$imageUrl")?.into(imageView)
        }


    }

    @JvmStatic
    @BindingAdapter("set_theme_color")
    fun bindThemeColor(layout: TextView, session: DaySession?) {
        when (session) {
            DaySession.MORNING, DaySession.DAY -> {
                layout.setTextColor(ContextCompat.getColor(layout.context, R.color.black))
            }

            DaySession.EVENING,DaySession.NIGHT -> {
                layout.setTextColor(ContextCompat.getColor(layout.context, R.color.white))

            }

            null -> {
                layout.setTextColor(ContextCompat.getColor(layout.context, R.color.black))
            }
        }
    }

    @JvmStatic
    @BindingAdapter("set_theme_bg")
    fun bindThemeBg(layout: LinearLayout, session: DaySession?) {
        when (session) {
            DaySession.MORNING -> {
                layout.background =
                    ContextCompat.getDrawable(layout.context, R.drawable.morning_gradient)
            }

            DaySession.DAY -> {
                layout.background =
                    ContextCompat.getDrawable(layout.context, R.drawable.day_gradient)
            }

            DaySession.EVENING,DaySession.NIGHT -> {
                layout.background =
                    ContextCompat.getDrawable(layout.context, R.drawable.night_gradient)

            }

            null -> {
                layout.background =
                    ContextCompat.getDrawable(layout.context, R.drawable.morning_gradient)
            }
        }
    }
}