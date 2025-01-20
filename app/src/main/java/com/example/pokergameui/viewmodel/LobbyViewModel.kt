package com.example.pokergameui.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokergameui.model.BaseResponse
import com.example.pokergameui.model.CreateTableRequest
import com.example.pokergameui.model.JoinTableRequest
import com.example.pokergameui.model.PokerTable
import com.example.pokergameui.model.Protocol
import com.example.pokergameui.model.ScoreboardResponse
import com.example.pokergameui.model.TCPConnectionManager
import com.example.pokergameui.model.TableListResponse
import com.example.pokergameui.model.UserScore
import com.example.pokergameui.view.InGameActivity
import com.example.pokergameui.view.JoinTableScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class User(
    val userId: Int? = null,
    val username: String? = null,
    val balance: Int? = null,
    val fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val dob: String? = null,
    val country: String? = null,
)

class LobbyViewModel : ViewModel() {
    private val tag = "LobbyViewModel"

    var tables: MutableState<List<PokerTable>> = mutableStateOf(emptyList())
        private set
    var user: User? = null
    var scoreboard: List<UserScore>? = null
    var isLoadingTable by mutableStateOf(false)
        private set

    fun createTable(context: Context, tableName: String, maxPlayers: Int, minBet: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = MyViewModels.lobbyViewModel.user
            if (user == null) {
                withContext(Dispatchers.Main) {
                    try {
                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e(tag, "Failed to show toast: ${e.localizedMessage}")
                    }
                }
                return@launch
            }

            if (maxPlayers < 2 || maxPlayers > 10) {
                withContext(Dispatchers.Main) {
                    try {
                        Toast.makeText(context, "Invalid number of players", Toast.LENGTH_SHORT)
                            .show()
                    } catch (e: Exception) {
                        Log.e(tag, "Failed to show toast: ${e.localizedMessage}")
                    }
                }
                return@launch
            }

            if (minBet < 10 || minBet > user.balance!!) {
                withContext(Dispatchers.Main) {
                    try {
                        Toast.makeText(context, "Invalid minimum bet", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e(tag, "Failed to show toast: ${e.localizedMessage}")
                    }
                }
                return@launch
            }
            val msg = Protocol.encode(
                1,
                Protocol.CREATE_TABLE,
                CreateTableRequest(tableName, maxPlayers.toByte(), minBet.toInt())
            )
            if (msg == null) {
                Log.d(tag, "Failed to encode message")
                return@launch
            }

            val status = TCPConnectionManager.send(msg)
            if (!status) {
                Log.d(tag, "Failed to send message")
                return@launch
            }

            val resp = TCPConnectionManager.receive()
            if (resp == null) {
                Log.d(tag, "Failed to receive message")
                return@launch
            }

            val respMsg = Protocol.decode<BaseResponse>(resp)
            if (respMsg.payload == null) {
                Log.d(tag, "Failed to decode message")
                return@launch
            }
            val res: Int = respMsg.payload.res.toInt()

            if (res == Protocol.CREATE_TABLE_OK) {
                Log.d(tag, "Table created successfully")
                withContext(Dispatchers.Main) {
                    try {
                        Toast.makeText(context, "Table created successfully", Toast.LENGTH_SHORT)
                            .show()

                        val intent = Intent(context, InGameActivity::class.java)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Log.e(tag, "Failed to show toast: ${e.localizedMessage}")
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    try {
                        Toast.makeText(context, "Failed to create table", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e(tag, "Failed to show toast: ${e.localizedMessage}")
                    }
                }
            }
        }
    }

    fun getTableList() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoadingTable = true

            val msg = Protocol.encode<Any>(1, Protocol.TABLE_LIST, null)
            if (msg == null) {
                Log.d(tag, "Failed to encode message")
                isLoadingTable = false
                return@launch
            }

            val status = TCPConnectionManager.send(msg)
            if (!status) {
                Log.d(tag, "Failed to send message")
                isLoadingTable = false
                return@launch
            }

            val resp = TCPConnectionManager.receive()
            if (resp == null) {
                Log.d(tag, "Failed to receive message")
                isLoadingTable = false
                return@launch
            }

            val respMsg = Protocol.decode<TableListResponse>(resp)

            val size = respMsg.payload?.size ?: 0
            val tables = respMsg.payload?.tables

            if (tables == null) {
                Log.d(tag, "Failed to get tables")
                isLoadingTable = false
                return@launch
            }

            if (size == 0 || tables.isEmpty()) {
                Log.d(tag, "No tables found")
                isLoadingTable = false
                return@launch
            }

            if (size != tables.size) {
                Log.d(tag, "Invalid table list")
                isLoadingTable = false
                return@launch
            }


            withContext(Dispatchers.Main) {
                this@LobbyViewModel.tables.value = tables
                isLoadingTable = false
            }
            TCPConnectionManager.send(msg)
        }
    }

    fun joinTable(context: Context, id : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val msg = Protocol.encode(1, Protocol.JOIN_TABLE, JoinTableRequest(id))

            if (msg == null) {
                Log.d(tag, "Failed to encode message")
                return@launch
            }

            val status = TCPConnectionManager.send(msg)

            if (!status) {
                Log.d(tag, "Failed to send message")
                return@launch
            }

            val resp = TCPConnectionManager.receive()

            if (resp == null) {
                Log.d(tag, "Failed to receive message")
                return@launch
            }

            val respMsg = Protocol.decode<BaseResponse>(resp)
            if (respMsg.payload == null) {
                Log.d(tag, "Failed to decode message")
                return@launch
            }

            val res: Int = (respMsg.payload.res).toInt()

            if (res == Protocol.JOIN_TABLE_OK) {
                Log.d(tag, "Joined table successfully")
                withContext(Dispatchers.Main) {
                    try {
                        Toast.makeText(context, "Joined table successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, InGameActivity::class.java)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Log.e(tag, "Failed to show toast: ${e.localizedMessage}")
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    try {
                        Toast.makeText(context, "Failed to join table", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e(tag, "Failed to show toast: ${e.localizedMessage}")
                    }
                }
            }
        }
    }

    fun showScoreboard() {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }

    fun showFriendList() {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }

    fun leaveTable() {
        viewModelScope.launch(Dispatchers.IO) {
            if (user == null || tables.value.isEmpty()) {
                Log.d(tag, "User or tables not found")
                return@launch
            }

            val msg = Protocol.encode<Any>(1, Protocol.LEAVE_TABLE, null);

        }
    }

    fun getScoreboard() {
        viewModelScope.launch(Dispatchers.IO) {
            val msg = Protocol.encode<Any>(1, Protocol.SCOREBOARD, null)

            if (msg == null) {
                Log.d(tag, "Failed to encode message")
                return@launch
            }

            val status = TCPConnectionManager.send(msg)

            if (!status) {
                Log.d(tag, "Failed to send message")
                return@launch
            }

            val resp = TCPConnectionManager.receive()

            if (resp == null) {
                Log.d(tag, "Failed to receive message")
                return@launch
            }

            val respMsg = Protocol.decode<ScoreboardResponse>(resp)
            val scb = respMsg.payload as List<UserScore>

            MyViewModels.lobbyViewModel.scoreboard = scb
        }
    }
}