package com.example.feature.ui.mypage.viewmodel

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.ViewModel
import com.example.core.ui.theme.HelloWorldGrayScale800
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CounselingDetailViewModel @Inject constructor(

) : ViewModel() {

    private val _summaryText = MutableStateFlow<AnnotatedString>(androidx.compose.ui.text.AnnotatedString(""))
    val summaryText: StateFlow<AnnotatedString> = _summaryText

    init {
        _summaryText.value = styledText()
    }

    private fun styledText(): AnnotatedString {
        return buildAnnotatedString {
            append("John Smith는 한국에서 근무 중인 호주 출신 근로자로, 지난 3개월 동안 ")

            withStyle(style = SpanStyle(color = HelloWorldGrayScale800)) {
                append("임금체불과 직장 내 괴롭힘")
            }

            append(
                "을 겪고 있다고 보고했습니다. 그는 고용주에게 여러 차례 임금 지급을 요청했지만, " +
                        "고용주는 이를 무시하고 있으며, 직장 내 동료들의 괴롭힘으로 인해 정신적 스트레스가 심한 상태입니다. " +
                        "John은 이러한 문제를 해결하기 위해 필요한 조치와 지원 방법에 대해 궁금해 했습니다."
            )
        }
    }
}