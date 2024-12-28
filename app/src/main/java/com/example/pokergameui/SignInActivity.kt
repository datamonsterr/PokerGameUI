package com.example.pokergameui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokergameui.ui.theme.InputLabel
import com.example.pokergameui.ui.theme.Blue
import com.example.pokergameui.ui.theme.Dark
import com.example.pokergameui.ui.theme.PokerGameUITheme

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokerGameUITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SignInBody(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SignInBody(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        SignInHeader()
        Spacer(modifier = Modifier.height(24.dp))
        SignInForm(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun SignInHeader(modifier: Modifier = Modifier) {
    Surface(color = Dark, contentColor = Color.White) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Sign in to your Account",
                fontSize = 28.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = "Sign up",
                    color = Blue,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        // Navigate to Sign-Up screen logic

                    }
                )
            }
        }
    }
}

@Composable
fun SignInForm(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(vertical = 16.dp)
    ) {
        InputLabel(
            label = "Email",
            placeholder = "Enter your email",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        InputLabel(
            label = "Password",
            placeholder = "Enter your password",
            hidden = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Blue),
            onClick = { /* Handle sign-in logic */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Login",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Forgot Password?",
            color = Blue,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    // Handle forgot password logic
                }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_8")
@Composable
fun SignInPreview() {
    PokerGameUITheme {
        SignInBody()
    }
}