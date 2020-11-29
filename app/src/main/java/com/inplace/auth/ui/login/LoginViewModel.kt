package com.inplace.auth.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inplace.api.CommandResult
import com.inplace.auth.data.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _isDataValid = MutableLiveData<Boolean>()
    val isDataValid: LiveData<Boolean> = _isDataValid

    private val _commandResult = MutableLiveData<CommandResult>()
    val commandResult: LiveData<CommandResult> = _commandResult

    fun login(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val commandResult = loginRepository.login(username, password)
            _commandResult.postValue(commandResult)
        }

//        if (commandResult.result != null ) {
//            _loginResult.value =
//                LoginResult(success = LoggedInUserView(displayName = commandResult.data.displayName))
//        } else {
//            _loginResult.value = LoginResult(error = R.string.login_failed)
//        }
    }

    fun loginDataChanged(username: String, password: String) {
        _isDataValid.value = username.isNotEmpty() && password.isNotEmpty()
    }
}