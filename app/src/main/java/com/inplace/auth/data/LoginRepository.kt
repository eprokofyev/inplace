package com.inplace.auth.data

// import com.inplace.api.CommandResult
// import com.inplace.api.vk.ApiVK
// import com.inplace.api.vk.VkUser
//
// /**
// * Class that requests authentication and user information from the remote data source and
// * maintains an in-memory cache of login status and user credentials information.
// */
//
// class LoginRepository(val dataSource: ApiVK) {
//
// // in-memory cache of the loggedInUser object
// var vkUser: VkUser? = null
// private set
//
// val isLoggedIn: Boolean
// get() = vkUser != null
//
// init {
// // If user credentials will be cached in local storage, it is recommended it be encrypted
// // @see https://developer.android.com/training/articles/keystore
// vkUser = null
// }
//
// fun logout() {
// // logout
// }
//
// fun login(username: String, password: String): CommandResult {
// return ApiVK.login(username, password)
// }
//
// private fun setLoggedInUser(vkUser: VkUser) {
// this.vkUser = vkUser
// // If user credentials will be cached in local storage, it is recommended it be encrypted
// // @see https://developer.android.com/training/articles/keystore
// }
// }