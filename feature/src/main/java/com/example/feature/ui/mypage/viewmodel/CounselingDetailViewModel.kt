package com.example.feature.ui.mypage.viewmodel

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@HiltViewModel(assistedFactory = CounselingDetailViewModel.Factory::class)
class CounselingDetailViewModel @AssistedInject constructor(
    @Assisted("roomId") roomId: String,
    @Assisted("title") title: String,
    @Assisted("chatSummary") chatSummary: String,
) : ViewModel() {

    private val _summary = MutableStateFlow(
        CounselingDetailState(
            roomId = roomId,
            title = title,
            chatSummary = chatSummary
        )
    )
    val summary = _summary

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("roomId") roomId: String,
            @Assisted("title") title: String,
            @Assisted("chatSummary") chatSummary: String
        ): CounselingDetailViewModel
    }
}

data class CounselingDetailState(
    val roomId: String,
    val title: String,
    val chatSummary: String
)
