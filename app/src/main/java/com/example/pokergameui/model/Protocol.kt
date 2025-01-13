package com.example.pokergameui.model

import android.util.Log
import com.daveanthonythomas.moshipack.MoshiPack
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class Header(val packetLen: Short, val protocolVer: Byte, val packetType: Short)
data class Packet<T>(val header: Header = Header(0, 0, 0), val payload: T? = null)
data class LoginRequest(val user: String, val pass: String)
data class TableListRequest()
data class BaseResponse(val res : Int)

class Protocol {
    companion object {
        const val LOGIN = 100
        const val TABLE_LIST = 500
        const val SIGNUP = 200

        inline fun <reified T> encode(
            protocolVersion: Int,
            packetType: Int,
            payload: T
        ): ByteArray? {
            val tag = "Protocol.encode"
            return try {
                val pVer = protocolVersion.toByte()
                val pType = packetType.toShort()
                val packed = MoshiPack.pack(payload).readByteArray()
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
