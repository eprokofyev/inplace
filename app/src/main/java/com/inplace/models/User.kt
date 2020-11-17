package com.inplace.models

data class User(val name: String,
                var avatar :String,
                val vk: UserVK?,
                val telegram: UserTelegram?,
                val id: Int,
)

data class UserVK(val name: String,
                  var avatar :String,
                  val mobile: String,
                  val token: String,
                  val id: String,
                  val email: String,
                  val localID: Int,
)

data class UserTelegram(val name: String,
                        var avatar :String,
                        val mobile: String,
                        val token: String,
                        val id: String,
                        val localID: Int,
)