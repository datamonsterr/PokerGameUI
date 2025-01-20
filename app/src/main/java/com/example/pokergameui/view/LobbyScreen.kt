package com.example.pokergameui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pokergameui.model.PokerTable
import com.example.pokergameui.model.UserScore
import com.example.pokergameui.viewmodel.MyViewModels
import com.example.pokergameui.viewmodel.User

@Composable
fun LobbyScreen(
    navController: NavController,
    onCreateTableClicked: () -> Unit,
    onJoinTableClicked: () -> Unit,
    onClickGameRule: () -> Unit,
    user: User
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        AvatarSection(user)
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(
                onClick = { navController.navigate("scoreboard") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
            ) {
                Text("Scoreboard", color = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { navController.navigate("friend_list") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
            ) {
                Text("Friends", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        PlaySection(
            rememberNavController(),
            onCreateTableClicked = onCreateTableClicked,
            onJoinTableClicked = onJoinTableClicked,
            onClickGameRule = onClickGameRule
        )
    }
}

@Composable
fun CreateTableScreen(onCreateTable: (String, Int, Int) -> Unit) {
    var tableName by remember { mutableStateOf("") }
    var numberOfPlayers by remember { mutableStateOf("") }
    var minBet by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = tableName,
            onValueChange = { tableName = it },
            label = { Text("Table Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = numberOfPlayers,
            onValueChange = { numberOfPlayers = it },
            label = { Text("Number of Players") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = minBet,
            onValueChange = { minBet = it },
            label = { Text("Min Bet") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val players = numberOfPlayers.toIntOrNull() ?: 0
                val bet = minBet.toIntOrNull() ?: 0
                onCreateTable(tableName, players, bet)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
        ) {
            Text("Create Table", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun JoinTableScreen(navController: NavController) {
    val tableList = MyViewModels.lobbyViewModel.tables
    val isLoadingTable = MyViewModels.lobbyViewModel.isLoadingTable

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Button(
            onClick = { MyViewModels.lobbyViewModel.getTableList() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
        ) {
            Text("Refresh", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoadingTable) {
            CircularProgressIndicator()
        } else {
            PokerTableList(tableList.value)
        }
    }
}


@Composable
fun PokerTableList(tables: List<PokerTable>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items(tables.size) { index ->
            PokerTableItem(tables[index])
        }
    }
}

@Composable
fun PokerTableItem(table: PokerTable) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                MyViewModels.lobbyViewModel.joinTable(context, table.id)
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Table: ${table.tableName}", fontSize = 16.sp, color = Color.Black)
            Text(
                "Players: ${table.currentPlayer}/${table.maxPlayer}",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Text("Minimum Bet: ${table.minBet} Chips", fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}


@Composable
fun FriendListScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Friend List",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        repeat(3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Friend ${it + 1}")
                Button(onClick = { /* Invite Friend Logic */ }) {
                    Text("Invite")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Button(
            onClick = { navController.navigate("play") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
        ) {
            Text("Go to Play Options", color = Color.White)
        }
    }
}

@Composable
fun PlaySection(
    navController: NavController,
    onCreateTableClicked: () -> Unit,
    onJoinTableClicked: () -> Unit,
    onClickGameRule: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onCreateTableClicked,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
        ) {
            Text("Create Table", color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onJoinTableClicked,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
        ) {
            Text("Join Table", color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onClickGameRule,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
        ) {
            Text("Game Rules", color = Color.White)
        }
    }
}

@Composable
fun ScoreboardScreen(navController: NavController, scoreboard : List<UserScore>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Scoreboard",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        for (score in scoreboard) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${score.id}")
                Text(text = "${score.balance}")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun AvatarSection(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(40.dp),
                color = Color.Gray
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("Avatar", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "${user.username}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Balance: ${user.balance?.toString()}",
                    fontSize = 16.sp,
                    color = Color(0xFF00796B)
                )
            }
        }
    }
}
