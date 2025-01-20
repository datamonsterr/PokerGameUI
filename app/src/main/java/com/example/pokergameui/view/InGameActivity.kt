package com.example.pokergameui.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokergameui.R

class InGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the number of players from intent
        val numberOfPlayers = intent.getIntExtra("NUMBER_OF_PLAYERS", 8) // Default to 8 if not provided

        setContent {
            InGameScreen(this, numberOfPlayers)
        }
    }
}

@Composable
fun InGameScreen(activity: Activity, numberOfPlayers: Int) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image of poker table
        Image(
            painter = painterResource(id = R.drawable.img_background), // Replace with your poker table drawable
            contentDescription = "Poker Table",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        CloseImageExample(activity = activity)

        // Dynamically positioned players
        PlayerPositions(numberOfPlayers)

        // Card holders in the center
        CardHolders()
    }
}

@Composable
fun CloseImageExample(activity: Activity) {
    Box(
        contentAlignment = Alignment.TopStart, // Align close button to top-right
        modifier = Modifier.fillMaxSize()
    ) {
        // Close button with image
        Image(
            painter = painterResource(id = R.drawable.ic_close), // Replace with your image resource
            contentDescription = "Close",
            modifier = Modifier
                .size(70.dp) // Set the size of the close button
                .clickable {
                    activity.finish() // End the activity on click
                }
                .padding(16.dp) // Add padding to the image
        )
    }
}

@Composable
fun PlayerPositions(numberOfPlayers: Int) {
    val playerData = (1..numberOfPlayers).map { playerIndex ->
        PlayerData(
            playerName = "P$playerIndex",
            betAmount = (10 * playerIndex).toString(),
            profileImageRes = R.drawable.p1 + (playerIndex % 6) // Cycle through 6 images
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        playerData.forEachIndexed { index, player ->
            val position = getPlayerPosition(index, numberOfPlayers)
            PlayerBox(
                playerName = player.playerName,
                betAmount = player.betAmount,
                profileImageRes = player.profileImageRes,
                modifier = Modifier.align(position)
            )
        }
    }
}

@Composable
fun PlayerBox(playerName: String, betAmount: String, profileImageRes: Int, modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Profile image
        Surface(
            shape = CircleShape,
            modifier = Modifier.size(50.dp),
            color = Color.Gray.copy(alpha = 0.8f)
        ) {
            Image(
                painter = painterResource(id = profileImageRes), // Replace with actual drawable
                contentDescription = "$playerName Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Player name
        Text(
            text = playerName,
            fontSize = 14.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Bet amount
        Text(
            text = betAmount,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Yellow,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun CardHolders() {
    Box(
        modifier = Modifier
            .fillMaxSize(), // Fill the entire screen
        contentAlignment = Alignment.Center // Center the card holders within the Box
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .size(650.dp, 130.dp)
                .background(color = Color.Transparent)
                .border(5.dp, Color.Black, RoundedCornerShape(100.dp)) // Border with rounded corners
                .wrapContentWidth() // Ensure only the content width is taken
        ) {
            repeat(5) {
                Surface(
                    modifier = Modifier
                        .padding(top = 25.dp)
                        .size(50.dp, 70.dp), // Adjust size as needed
                    shape = RoundedCornerShape(8.dp), // Optional: Rounded corners
                    border = BorderStroke(2.dp, Color.Gray), // Gray border
                    color = Color.Transparent // Transparent background
                ) {
                    // Empty Surface, no content inside
                }
            }
        }
    }
}
@Composable
fun OverlappingImages(imageRes1: Int, imageRes2: Int) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        // First Image (background image)
        Image(
            painter = painterResource(id = imageRes1),
            contentDescription = "Image 1",
            modifier = Modifier
                .width(60.dp)
                .height(60.dp)
                .border(width = 0.5.dp, color = Color.Black)
                .background(Color.White)
        )

        // Second Image (overlapping image)
        Image(
            painter = painterResource(id = imageRes2),
            contentDescription = "Image 2",
            modifier = Modifier
                .width(60.dp)
                .height(60.dp)
                .padding(start = 30.dp)
                .border(width = 0.5.dp, color = Color.Black)
                .background(Color.White)
        )
    }
}

data class PlayerData(val playerName: String, val betAmount: String, val profileImageRes: Int)

fun getPlayerPosition(index: Int, numberOfPlayers: Int): Alignment {
    return when (index) {
        0 -> Alignment.TopCenter
        1 -> Alignment.TopStart
        2 -> Alignment.TopEnd
        3 -> Alignment.CenterStart
        4 -> Alignment.CenterEnd
        5 -> Alignment.BottomStart
        6 -> Alignment.BottomEnd
        7 -> Alignment.BottomCenter
        else -> Alignment.Center // Fallback position
    }
}


