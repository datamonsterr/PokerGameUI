package com.example.pokergameui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokergameui.model.Protocol
import com.example.pokergameui.view.PokerTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LobbyViewModel : ViewModel() {
    fun createTable(context: Context, tableName: String, maxPlayers: Int, minBet: Int) {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }

    suspend fun getTableList(): List<PokerTable>? {
        return withContext(Dispatchers.IO) {
            // Fetch or process data in IO dispatcher
            val tableList = mutableListOf<PokerTable>()
            val msg = Protocol.encode<>(1, Protocol.TABLE_LIST, null)
            tableList // Return the result
        }
    }

    fun joinTable(context: Context, tableName: String) {
        viewModelScope.launch(Dispatchers.IO) {
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
}