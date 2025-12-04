package com.example.feature.ui.mypage.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.mypage.MyPageRepository
import com.example.core.ui.component.DialogData
import com.example.core.ui.component.ToastData
import com.example.domain.SetProfileUseCase
import com.example.model.common.Language
import com.example.model.common.Result
import com.example.model.mypage.UserInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ProfileEditViewModel.Factory::class)
class ProfileEditViewModel @AssistedInject constructor(
    private val setProfileUseCase: SetProfileUseCase,
    @Assisted val initUser: UserInfo
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileEditUiState>(ProfileEditUiState.Idle)
    val uiState: StateFlow<ProfileEditUiState> = _uiState.asStateFlow()

    private val _editingNickName = MutableStateFlow("")
    val editingNickName: StateFlow<String> = _editingNickName.asStateFlow()

    private val _editingUserImg = MutableStateFlow<Uri?>(null)
    val editingUserImg: StateFlow<Uri?> = _editingUserImg.asStateFlow()

    private val _editingLanguage = MutableStateFlow<Language?>(null)
    val editingLanguage: StateFlow<Language?> = _editingLanguage.asStateFlow()

    private val _dialogData = MutableStateFlow<DialogData?>(null)
    val dialogData: StateFlow<DialogData?> = _dialogData.asStateFlow()

    private val _toastData = MutableStateFlow<ToastData?>(null)
    val toastData = _toastData.asStateFlow()

    fun updateDialogData(data: DialogData? = null) {
        _dialogData.value = data
    }

    fun updateNickname(nickName: String) {
        _editingNickName.value = nickName
        if (nickName.length > 15) {
            _toastData.value = ToastData(
                text = "닉네임은 15자 이하로 입력해주세요.",
                duration = 1000,
                onDismiss = { _toastData.value = null }
            )
        }
    }

    fun updateUserImg(imgUri: Uri) {
        _editingUserImg.value = imgUri
    }

    fun updateLanguage(language: Language) {
        _editingLanguage.value = language
    }

    fun setProfile(context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _uiState.value = ProfileEditUiState.Loading

            val imageBytes = _editingUserImg.value?.let { uri ->
                try {
                    val contentResolver = context.contentResolver
                    val inputStream = contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()
                    bytes
                } catch (e: Exception) {
                    null
                }
            }

            setProfileUseCase(
                nickName = _editingNickName.value.ifBlank { initUser.name },
                userImg = imageBytes,
                language = if (initUser.language?.code == _editingLanguage.value?.code) null else _editingLanguage.value
            ).collect {
                when (it) {
                    is Result.Loading -> _uiState.value = ProfileEditUiState.Loading
                    is Result.Success<Boolean> -> {
                        _uiState.value = ProfileEditUiState.Success
                        onResult(it.data)
                    }
                    is Result.Error -> {
                        _uiState.value = ProfileEditUiState.Error
                        onResult(false)
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            initUser: UserInfo
        ): ProfileEditViewModel
    }
}

sealed interface ProfileEditUiState {
    data object Idle: ProfileEditUiState
    data object Loading: ProfileEditUiState
    data object Success: ProfileEditUiState
    data object Error: ProfileEditUiState
}