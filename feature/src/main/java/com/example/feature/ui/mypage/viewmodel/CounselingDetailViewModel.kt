package com.example.feature.ui.mypage.viewmodel

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.mypage.MyPageRepository
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.model.mypage.DetailSummaryRequest
import com.example.model.mypage.DetailSummaryResponse
import com.example.model.mypage.Summary
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(assistedFactory = CounselingDetailViewModel.Factory::class)
class CounselingDetailViewModel @AssistedInject constructor(
    private val myPageRepository: MyPageRepository,
    @Assisted val summaryId: Int,
) : ViewModel() {

    private val _summary = MutableStateFlow<DetailSummaryResponse?>(null)
    val summary = _summary.asStateFlow()

    init {
        getSummary()
    }

    fun getSummary() {
        viewModelScope.launch {
            myPageRepository.getDetailSummary(DetailSummaryRequest(summaryId.toLong())
            ).fold(
                onSuccess = {
                    _summary.value = it
                },
                onFailure = {
                    // TODO
                }
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            summaryId: Int
        ): CounselingDetailViewModel
    }
}