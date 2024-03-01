package com.supertal.weatherapp.utils

import android.util.Log
import timber.log.Timber

class ReleaseTree:  Timber.Tree(){

    private val MAX_LOG_LENGTH = 4000

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        // Don't log VERBOSE, DEBUG and INFO
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return false
        }

        // Log only ERROR, WARN and WTF
        return true
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
       if(isLoggable(tag, priority)){

           //Report caught exceptions to Crashlytics
           if(priority == Log.ERROR && t != null){
               //record any exception
           }

           // Message is short enough, doesn't need to be broken into chunks
           if (message.length < MAX_LOG_LENGTH) {
               if (priority == Log.ASSERT) {
                   Log.wtf(tag, message)
               } else {
                   Log.println(priority, tag, message)
               }
               return
           }

           val length = message.length
           for(j in 0..length.minus(1)){
               var i = j
               var newline = message.indexOf('\n', i)
               newline = if(newline != -1) newline else length
               do{
                   val end = newline.coerceAtMost(i + MAX_LOG_LENGTH)
                   val part = message.substring(i, end)
                   if (priority == Log.ASSERT) {
                       Log.wtf(tag, part);
                   } else {
                       Log.println(priority,  tag, part)
                   }
                   i = end

               }while (i < newline)
           }
       }
    }
}