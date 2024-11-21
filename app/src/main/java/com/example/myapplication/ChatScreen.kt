package com.example.myapplication


import kotlinx.coroutines.launch
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.piesocket.channels.PieSocket
import com.piesocket.channels.misc.PieSocketEvent
import com.piesocket.channels.misc.PieSocketEventListener
import com.piesocket.channels.misc.PieSocketOptions
import org.json.JSONObject
import java.util.*

data class Message(val id: String, val content: String, val isSentByMe: Boolean)

@SuppressLint("RememberReturnType")
@Composable
fun ChatScreen() {
    var textState by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }
    val coroutineScope = rememberCoroutineScope()

    // Initialize PieSocket with the provided options
    val pieSocket = remember {
        val pieSocketOptions = PieSocketOptions()
        pieSocketOptions.clusterId = "free.blr2"
        pieSocketOptions.apiKey = "2ZilnOus6sDs7od7bbVYQgx8LlgAfabf7yGLJlUt"
        PieSocket(pieSocketOptions)
    }

    // Join the "chat-room" channel
    val channel = remember(pieSocket) {
        pieSocket.join("chat-room")
    }

    // Listen for incoming messages and system connection events
    DisposableEffect(channel) {
        channel.listen("system:connected", object : PieSocketEventListener() {
            override fun handleEvent(event: PieSocketEvent) {
                // Handle system connected event if needed
            }
        })

        channel.listen("message", object : PieSocketEventListener() {
            override fun handleEvent(event: PieSocketEvent) {
                coroutineScope.launch {
                    val eventData = event.data.toString()
                    val jsonObject = JSONObject(eventData)
                    val receivedMessageId = jsonObject.getString("id")
                    val receivedMessageContent = jsonObject.getString("content")
                    if (!messages.any { it.id == receivedMessageId }) {
                        messages.add(0, Message(receivedMessageId, receivedMessageContent, false))
                    }
                }
                Log.i("TextReceived", event.data.toString())
            }
        })

        onDispose {
            channel.disconnect()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                MessageCard(message)
            }
        }
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
            )
            IconButton(onClick = {
                if (textState.isNotBlank()) {
                    // Publish the message to the PieSocket channel with a unique ID
                    val messageId = UUID.randomUUID().toString()
                    val messageContent = textState
                    val messageJson = JSONObject().apply {
                        put("id", messageId)
                        put("content", messageContent)
                    }.toString()

                    val clientEvent = PieSocketEvent("message")
                    clientEvent.setData(messageJson)
                    channel.publish(clientEvent)
                    // Add the message to the local list and clear the input field
                    val newMessage = Message(messageId, messageContent, true)
                    messages.add(0, newMessage)
                    textState = ""
                }
            }) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun MessageCard(message: Message) {
    val textColor = if (message.isSentByMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(all = 12.dp), // Increased padding for a better look
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message.content,
                color = textColor,
                modifier = Modifier.weight(1f) // Text takes up the remaining space
            )
        }
    }
}