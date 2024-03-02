package com.supertal.weatherapp.utils

import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.supertal.weatherapp.core.dataModels.TextChangeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun TextInputEditText.getQueryTextChangeStateFlow(): StateFlow<TextChangeModel> {

    val query = MutableStateFlow(TextChangeModel("", 0, 0, 0))
    doOnTextChanged { text, start, count, after ->
        if (count != after) query.value = TextChangeModel(text.toString(), start, count, after)
    }

    return query

}
