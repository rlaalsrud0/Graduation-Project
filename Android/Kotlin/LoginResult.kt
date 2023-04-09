package com.example.ollie

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class LoginResult {

    private var name: String? = null
    private var phone: String? = null
    private var passwd: String? = null

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