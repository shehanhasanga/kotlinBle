package com.shehan.navapp.models.listener

interface MessagesListener {
    fun onNewMessageReceived(message: String)
}