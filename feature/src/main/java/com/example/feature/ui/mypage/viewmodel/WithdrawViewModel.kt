package com.example.feature.ui.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.mypage.MyPageRepository
import com.example.core.ui.component.DialogData
import com.example.domain.WithdrawUseCase
import com.example.model.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    private val withdrawUseCase: WithdrawUseCase
) : ViewModel() {

    private val _dialogData = MutableStateFlow<DialogData?>(null)
    val dialogData: StateFlow<DialogData?> = _dialogData

    fun updateDialogData(data: DialogData? = null) {
        _dialogData.value = data
    }

    fun withdraw(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            withdrawUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> {} // TODO
                    is Result.Error -> {} // TODO
                    is Result.Success<Boolean> -> {
                        onResult(result.data)
                    }
                }
            }
        }
    }
}