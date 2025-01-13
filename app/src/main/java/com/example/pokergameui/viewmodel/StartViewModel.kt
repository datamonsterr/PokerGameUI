package com.example.pokergameui.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokergameui.model.BaseResponse
import com.example.pokergameui.model.LoginRequest
import com.example.pokergameui.model.Protocol
import com.example.pokergameui.model.TCPConnectionManager
import com.example.pokergameui.view.LobbyActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StartViewModel : ViewModel() {
    private val tag = "StartViewModel"
    private val host = "192.168.1.20"
    private val port = 8080

    fun initConnection() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(tag, "initConnection called")
                val isConnected = TCPConnectionManager.connect(host, port)
                Log.d(tag, "initConnection get result $isConnected")
                if (isConnected) {
                    Log.d(tag, "Connection successful")
                }
            } catch (e: Exception) {
                Log.e(tag, "Error: ${e.localizedMessage}")
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(tag, "disconnect called")
                val isDisconnected = TCPConnectionManager.disconnect()
                Log.d(tag, "disconnect get result $isDisconnected")
                if (isDisconnected) {
                    Log.d(tag, "Disconnected successfully")
                }
            } catch (e: Exception) {
                Log.e(tag, "Error: ${e.localizedMessage}")
            }
        }
    }

    fun signIn(context: Context, username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("handle-signin", "signing in")
            val msg = Protocol.encode(1, Protocol.LOGIN, LoginRequest(username, password))
                ?: return@launch
            if (!TCPConnectionManager.send(msg)) {
                Log.e("handle-signin", "send failed")
                return@launch
            }

            val response = TCPConnectionManager.receive() ?: return@launch
            val status = Protocol.decode<BaseResponse>(response)
            if (status.header.packetLen == 0.toShort()) {
                Log.e("handle-signin", "receive failed")
                return@launch
            }

            if (status.payload?.res == Protocol.LOGIN + 1) {
                Log.d("handle-signin", "login success")
                val intent = Intent(context, LobbyActivity::class.java)
                context.startActivity(intent)
            } else {
                Log.e("handle-signin", "login failed")
            }
        }
    }
}