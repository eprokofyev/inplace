package com.inplace.models

data class Sobesednik(val name: String,
                      var avatar :String,
                      val vk: SobesednikVk?,
                      val telegram: SobesednikTelegram?,
                      val id: String,
)

data class SobesednikVk(val name: String,
                        var avatar :String,
                        val id: String,
                        var activeTime: String,
                        var about: String,
                        val localID: Int,
)

data class SobesednikTelegram(val name: String,
                              var avatar :String,
                              val id: String,
                              val mobile: String,
                              var activeTime: String,
                              var about: String,
                              val localID: Int,
)