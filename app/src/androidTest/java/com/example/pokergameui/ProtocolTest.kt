package com.example.pokergameui

import com.example.pokergameui.model.LoginRequest
import com.example.pokergameui.model.Protocol
import org.junit.Test

class ProtocolTest {
    @Test
    fun testEncodePacket() {
        val encodedPacket = Protocol.encode(1, 100, LoginRequest("admin", "admin"))
        assert(encodedPacket != null)
        if (encodedPacket != null) {
            val decodedPacket = Protocol.decode<LoginRequest>(encodedPacket)
            assert(decodedPacket.header.packetType == 100.toShort())
            assert(decodedPacket.payload?.user == "admin")
            assert(decodedPacket.payload?.pass == "admin")
        }
    }
}