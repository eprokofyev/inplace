package com.inplace.auth.data

import com.inplace.auth.data.model.User_VK

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var userVK: User_VK? = null
        private set

    val isLoggedIn: Boolean
        get() = userVK != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        userVK = null
    }

    fun logout() {
        userVK = null
        dataSource.logout()
    }

    fun login(username: String, password: String): Result<User_VK> {
        // handle login
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(userVK: User_VK) {
        this.userVK = userVK
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}