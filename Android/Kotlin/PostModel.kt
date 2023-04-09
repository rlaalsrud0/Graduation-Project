package com.example.ollie

import com.google.gson.annotations.SerializedName

class PostModel {
        @field:SerializedName("u_name")
        private val name: String = ""

    @field:SerializedName("u_phone")
        private val phone: String = ""

    @field:SerializedName("u_passwd")
        private val passwd: String = ""

    fun getName(): String? {
        return name
    }

    fun getPhone(): String? {
        return phone
    }

    fun getPasswd() : String? {
        return passwd
    }
}