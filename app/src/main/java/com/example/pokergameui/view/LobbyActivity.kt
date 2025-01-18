package com.example.pokergameui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pokergameui.viewmodel.LobbyViewModel
import com.example.pokergameui.viewmodel.MyViewModels
import com.example.pokergameui.viewmodel.User

class LobbyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = MyViewModels.lobbyViewModel.user
        if (user == null) {
            startActivity(Intent(this, StartActivity::class.java))
            finish()
            return
        }

        setContent {
            PokerGameApp(user)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokerGameApp(user: User) {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Poker Game", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF00796B)
                )
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "lobby",
            modifier = Modifier.padding(padding)
        ) {
            addLobbyScreen(navController, user)
            addCreateTableScreen(navController)
            addJoinTableScreen(navController)
            addScoreboardScreen(navController)
        }
    }
}

fun NavGraphBuilder.addLobbyScreen(navController: NavController, user: User) {
    composable("lobby") {
        LobbyScreen(
            navController = navController,
            onCreateTableClicked = { navController.navigate("create_table") },
            onJoinTableClicked = { navController.navigate("join_table") },
            onClickGameRule = {
                val youtubeUrl = "https://www.youtube.com/watch?v=JOomXP-r1wY"
                navController.context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(youtubeUrl)
                    )
                )
            }, user = user
        )
    }
}

fun NavGraphBuilder.addCreateTableScreen(navController: NavController) {
    fun handleCreateTable(tableName: String, numberOfPlayers: Int, minBet: Int) {
        val context = navController.context
        MyViewModels.lobbyViewModel.createTable(context, tableName, numberOfPlayers, minBet)
    }

    composable("create_table") {
        CreateTableScreen(
            onCreateTable = { tableName, numberOfPlayers, minBet ->
                Log.d(
                    "CreateTable",
                    "Table: $tableName, Players: $numberOfPlayers, Min Bet: $minBet"
                )

                handleCreateTable(tableName, numberOfPlayers, minBet)

                navController.navigate("lobby")
            }
        )
    }
}

fun NavGraphBuilder.addJoinTableScreen(navController: NavController) {
    composable("join_table") {
        JoinTableScreen(navController)
    }
}

fun NavGraphBuilder.addScoreboardScreen(navController: NavController) {
    composable("scoreboard") {
        val scoreboard = MyViewModels.lobbyViewModel.scoreboard
        if (scoreboard == null) {
            MyViewModels.lobbyViewModel.getScoreboard()
            return@composable
        }
        ScoreboardScreen(navController, scoreboard)
    }
}

