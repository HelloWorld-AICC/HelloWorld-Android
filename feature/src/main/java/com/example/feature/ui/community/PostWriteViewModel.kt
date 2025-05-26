package com.example.feature.ui.community

import androidx.lifecycle.ViewModel
import com.example.core.ui.component.DialogData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PostWriteViewModel @Inject constructor(

) : ViewModel() {

    private val _selectedTab = MutableStateFlow<String>("problem")
    val selectedTab: StateFlow<String> = _selectedTab

    private val _title = MutableStateFlow<String>("")
    val title: StateFlow<String> = _title

    private val _content = MutableStateFlow<String>("")
    val content: StateFlow<String> = _content

    private val _dialogData = MutableStateFlow<DialogData?>(null)
    val dialogData: StateFlow<DialogData?> = _dialogData

    fun changeTab(tab: String) {
        _selectedTab.value = tab
    }

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun updateContent(newContent: String) {
        _content.value = newContent
    }

    fun updateDialogData(data: DialogData? = null) {
        _dialogData.value = data
    }

}