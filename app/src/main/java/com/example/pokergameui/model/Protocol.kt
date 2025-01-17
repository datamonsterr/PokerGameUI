package com.example.pokergameui.model

import android.util.Log
import android.util.Size
import com.daveanthonythomas.moshipack.MoshiPack
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class Header(val packetLen: Short, val protocolVer: Byte, val packetType: Short)
data class Packet<T>(val header: Header = Header(0, 0, 0), val payload: T? = null)
data class LoginRequest(val user: String, val pass: String)
data class CreateTableRequest(
    val tableName: String,
    val maxPlayer: Byte,
    val minBet: Int
)

data class BaseResponse(val res : Short)

data class LoginResponse(
    val res : Short,
    val userId : Int,
    val username : String,
    val balance : Int,
    val fullname: String,
    val email: String,
    val phone: String,
    val dob: String,
    val country: String,
    val gender: String
)

data class PokerTable(
    val id: Int,
    val tableName: String,
    val maxPlayer: Int,
    val minBet: Int,
    val currentPlayer: Int
)
data class TableListResponse(val size: Int, val tables: List<PokerTable>)

data class UserScore(
    val rank: Int,
    val id: Int,
    val balance: Int
)

data class ScoreboardResponse(
    val users: List<UserScore>
)

data class JoinTableRequest(val tableId: Int)

class Protocol {
    companion object {
        const val LOGIN = 100
        const val LOGIN_OK = 101
        const val LOGIN_NOT_OK = 102

        const val SIGNUP = 200
        const val SIGNUP_OK = 201
        const val SIGNUP_NOT_OK = 202

        const val CREATE_TABLE = 300
        const val CREATE_TABLE_OK = 301
        const val CREATE_TABLE_NOT_OK = 302

        const val JOIN_TABLE = 400
        const val JOIN_TABLE_OK = 401
        const val JOIN_TABLE_NOT_OK = 402

        const val TABLE_LIST = 500
        const val TABLE_LIST_OK = 501
        const val TABLE_LIST_NOT_OK = 502

        const val UPDATE_GAMESTATE = 600
        const val UPDATE_GAMESTATE_OK = 601
        const val UPDATE_GAMESTATE_NOT_OK = 602

        const val LEAVE_TABLE = 700
        const val LEAVE_TABLE_OK = 701
        const val LEAVE_TABLE_NOT_OK = 702

        const val SCOREBOARD = 800
        const val SCOREBOARD_OK = 801
        const val SCOREBOARD_NOT_OK = 802

        const val FRIEND_LIST = 900
        const val FRIEND_LIST_OK = 901
        const val FRIEND_LIST_NOT_OK = 902

        inline fun <reified T> encode(
            protocolVersion: Int,
            packetType: Int,
            payload: T?
        ): ByteArray? {
            val tag = "Protocol.encode"
            return try {
                val pVer = protocolVersion.toByte()
                val pType = packetType.toShort()
                val packed = payload?.let { MoshiPack.pack(it).readByteArray() } ?: byteArrayOf()
                val packetLen: Short = (5 + packed.size).toShort()
                val buffer = ByteBuffer.allocate(packetLen.toInt()).order(ByteOrder.BIG_ENDIAN)
                buffer.putShort(packetLen)
                buffer.put(pVer)
                buffer.putShort(pType)
                buffer.put(packed)
                Log.d(tag, "Encoded packet: ${buffer.array().contentToString()}")
                buffer.array()
            } catch (e: Exception) {
                Log.e(tag, "Encode error: ${e.localizedMessage}")
                null
            }
        }

        inline fun <reified T> decode(data: ByteArray): Packet<T> {
            val tag = "Protocol.decode"
            return try {
                Log.d(tag, "Decoding packet: ${data.contentToString()}")
                val buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN)
                val packetLen = buffer.getShort()
                val protocolVer = buffer.get()
                val packetType = buffer.getShort()
                // get the payload by skipping the header = 5 bytes
                val payload = MoshiPack.unpack<T>(data.copyOfRange(5, data.size))
                Log.d(tag, "Decoded packet: $packetLen, $protocolVer, $packetType, $payload")
                Packet(Header(packetLen, protocolVer, packetType), payload)
            } catch (e: Exception) {
                Log.e(tag, "Decode error: ${e.localizedMessage}")
                Packet()
            }
        }
    }
}
