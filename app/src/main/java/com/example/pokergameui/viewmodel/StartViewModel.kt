package com.example.pokergameui.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokergameui.model.BaseResponse
import com.example.pokergameui.model.LoginRequest
import com.example.pokergameui.model.LoginResponse
import com.example.pokergameui.model.Protocol
import com.example.pokergameui.model.TCPConnectionManager
import com.example.pokergameui.view.LobbyActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartViewModel : ViewModel() {
    private val tag = "StartViewModel"
    private val host = "192.168.1.17"
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
            try {
                val status = Protocol.decode<LoginResponse>(response)
                if (status.header.packetLen == 0.toShort()) {
                    Log.e("handle-signin", "receive failed")
                    Toast.makeText(context, "Failed to receive data", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                MyViewModels.lobbyViewModel.user = User(
                    status.payload?.userId ?: 0,
                    status.payload?.username ?: "",
                    status.payload?.balance ?: 0,
                    status.payload?.fullname ?: "",
                    status.payload?.email ?: "",
                    status.payload?.phone ?: "",
                    status.payload?.dob ?: "",
                    status.payload?.country ?: ""
                )
                withContext(Dispatchers.Main) {
                    if (status.payload?.res == Protocol.LOGIN + 1) {
                        Log.d("handle-signin", "login success")
                        try {
                            Toast.makeText(context, "Login sucess", Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, LobbyActivity::class.java)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("handle-signin", "error toast: ${e.localizedMessage}")
                        }
                    } else {
                        Log.e("handle-signin", "login failed")
                        try {
                            Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Log.e("handle-signin", "error toast: ${e.localizedMessage}")
                        }
                    }
                }
            } catch (e: Exception) {
                val status = Protocol.decode<BaseResponse>(response)
                if (status.header.packetLen == 0.toShort()) {
                    Log.e("handle-signin", "receive failed")
                    Toast.makeText(context, "Failed to receive data", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    if (status.payload?.res == Protocol.LOGIN + 2) {
                        Log.e("handle-signin", "login failed")
                        try {
                            Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Log.e("handle-signin", "error toast: ${e.localizedMessage}")
                        }
                    }
                }
                return@launch
            }
        }
    }
}