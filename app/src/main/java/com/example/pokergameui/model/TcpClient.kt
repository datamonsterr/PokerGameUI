package com.example.pokergameui.model

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException

class TCPClient(private val host: String, private val port: Int) {
    private var socket: Socket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null

    // Connect to the server
    suspend fun connect(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("TCPClient", "Attempting to connect to $host:$port")
                socket = Socket()
                socket?.connect(InetSocketAddress(host, port), 5000)
                outputStream = socket?.getOutputStream()
                inputStream = socket?.getInputStream()
                Log.d("TCPClient", "Connected to $host:$port")
                true
            } catch (e: Exception) {
                Log.e("TCPClient", "Error connecting to $host:$port - ${e.localizedMessage}")
                false
            }
        }
    }

    // Send data to the server
    suspend fun send(data: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (outputStream == null) {
                    Log.e("TCPClient", "OutputStream is null, cannot send data")
                    false
                }

                if (socket == null || socket?.isClosed == true) {
                    Log.e("TCPClient", "Socket is closed or null, cannot send data")
                    false
                }
                Log.d("TCPClient", "Sending data of size: ${data.size} bytes")
                outputStream?.write(data, 0, data.size)
                outputStream?.flush()
                Log.d("TCPClient", "Data sent successfully")
                true
            } catch (e: IOException) {
                Log.e("TCPClient", "IO error while sending data: ${e.localizedMessage}")
                e.printStackTrace()
                false
            } catch (e: SocketException) {
                Log.e("TCPClient", "Socket error while sending data: ${e.localizedMessage}")
                e.printStackTrace()
                false
            } catch (e: Exception) {
                Log.e("TCPClient", "Unexpected error while sending data: ${e.localizedMessage}")
                e.printStackTrace()
                false
            }
        }
    }

    // Receive data from the server
    suspend fun receive(): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val buffer = ByteArray(4096)
                val bytesRead = inputStream?.read(buffer)
                if (bytesRead == -1) {
                    Log.e("TCPClient", "End of stream reached")
                    return@withContext null
                }
                Log.d("TCPClient", "Received data of size: $bytesRead bytes")
                buffer.copyOf(bytesRead!!)
            } catch (e: Exception) {
                Log.e("TCPClient", "Receive error: ${e.localizedMessage}")
                null
            }
        }
    }

    // Close the connection
    suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            try {
                outputStream?.close()
                inputStream?.close()
                socket?.close()
                Log.d("TCPClient", "Disconnected from server")
            } catch (e: Exception) {
                Log.e("TCPClient", "Disconnect error: ${e.localizedMessage}")
            }
        }
    }
}

object TCPConnectionManager {
    private var tcpClient: TCPClient? = null

    // Connect to the server
    suspend fun connect(host: String, port: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (tcpClient == null) {
                    Log.d("TCPConnectionManager", "Connecting to $host:$port")
                    tcpClient = TCPClient(host, port)
                }

                val connectionResult = tcpClient?.connect() ?: false
                if (connectionResult) {
                    Log.d("TCPConnectionManager", "Successfully connected to $host:$port")
                } else {
                    Log.e("TCPConnectionManager", "Failed to connect to $host:$port")
                }
                connectionResult
            } catch (e: Exception) {
                Log.e("TCPConnectionManager", "Error connecting to server: ${e.localizedMessage}")
                false
            }
        }
    }

    // Send data to the server
    suspend fun send(data: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (tcpClient == null) {
                    Log.e("TCPConnectionManager", "No active connection. Cannot send data.")
                    return@withContext false
                }

                val sendResult = tcpClient?.send(data) ?: false
                if (sendResult) {
                    Log.d("TCPConnectionManager", "Data sent successfully")
                } else {
                    Log.e("TCPConnectionManager", "Failed to send data")
                }
                sendResult
            } catch (e: Exception) {
                Log.e("TCPConnectionManager", "Error sending data: ${e.localizedMessage}")
                false
            }
        }
    }

    // Receive data from the server
    suspend fun receive(): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                if (tcpClient == null) {
                    Log.e("TCPConnectionManager", "No active connection. Cannot receive data.")
                    return@withContext null
                }

                val data = tcpClient?.receive()
                if (data != null) {
                    Log.d("TCPConnectionManager", "Received data: ${data.size} bytes")
                } else {
                    Log.e("TCPConnectionManager", "No data received or stream closed.")
                }
                data
            } catch (e: Exception) {
                Log.e("TCPConnectionManager", "Error receiving data: ${e.localizedMessage}")
                null
            }
        }
    }

    // Disconnect from the server
    suspend fun disconnect(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                tcpClient?.disconnect()
                tcpClient = null
                Log.d("TCPConnectionManager", "Disconnected from server")
                true
            } catch (e: Exception) {
                Log.e("TCPConnectionManager", "Error disconnecting: ${e.localizedMessage}")
                false
            }
        }
    }
}
