package com.example.arlhambra.ui.tools

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ToolsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "- Single tap on glasses to get to this menu\n" +
                "- Try to find important monuments at alhambra!"

    }
    val text: LiveData<String> = _text
}