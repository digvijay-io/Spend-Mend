package com.example.spendmend.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spendmend.R
import com.example.spendmend.ui.theme.BrandGreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn.getClient
import com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(
    navController: NavController
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val auth = FirebaseAuth.getInstance()

    val googleSignInClient = remember {

        getClient(

            context,

            Builder(DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(
                    context.getString(R.string.default_web_client_id)
                )
                .build()

        )

    }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val task =
                    getSignedInAccountFromIntent(result.data)

                val account = task.result

                if (account != null) {

                    val credential =
                        GoogleAuthProvider.getCredential(
                            account.idToken,
                            null
                        )

                    auth.signInWithCredential(credential)
                        .addOnCompleteListener {

                            if (it.isSuccessful) {

                                Toast.makeText(
                                    context,
                                    "Google Login Successful",
                                    Toast.LENGTH_SHORT
                                ).show()

                                navController.navigate("main") {

                                    popUpTo("login") {
                                        inclusive = true
                                    }

                                }

                            } else {

                                Toast.makeText(
                                    context,
                                    "Google Login Failed",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        }

                }

            }

        }

    Surface(

        modifier = Modifier.fillMaxSize(),

        color = MaterialTheme.colorScheme.background

    ) {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Spacer(
                modifier = Modifier.height(70.dp)
            )

            Text(

                text = "Welcome Back",

                style = MaterialTheme.typography.headlineLarge,

                fontWeight = FontWeight.Bold

            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(

                text = "Login to manage your finances",

                style = MaterialTheme.typography.bodyMedium,

                color = MaterialTheme.colorScheme.onSurfaceVariant

            )

            Spacer(
                modifier = Modifier.height(36.dp)
            )

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(30.dp),

                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )

            ) {

                Column(

                    modifier = Modifier.padding(24.dp)

                ) {
                    Text(
                        text = "Email Address",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        placeholder = {
                            Text("name@company.com")
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFF4F5F7),
                            unfocusedContainerColor = Color(0xFFF4F5F7)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Password",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Forgot Password?",
                            color = BrandGreen,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {

                                // TODO Forgot Password

                            }
                        )

                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        singleLine = true,
                        visualTransformation =
                            if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        leadingIcon = {

                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                        },
                        trailingIcon = {

                            IconButton(
                                onClick = {

                                    passwordVisible =
                                        !passwordVisible

                                }
                            ) {

                                Icon(
                                    imageVector =
                                        if (passwordVisible)
                                            Icons.Outlined.Visibility
                                        else
                                            Icons.Outlined.VisibilityOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                            }

                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFF4F5F7),
                            unfocusedContainerColor = Color(0xFFF4F5F7)
                        )
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {

                            if (email.isBlank() || password.isBlank()) {

                                Toast.makeText(
                                    context,
                                    "Please fill all fields",
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@Button

                            }

                            isLoading = true

                            auth.signInWithEmailAndPassword(
                                email.trim(),
                                password.trim()
                            ).addOnCompleteListener {

                                isLoading = false

                                if (it.isSuccessful) {

                                    Toast.makeText(
                                        context,
                                        "Login Successful",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    navController.navigate("main") {

                                        popUpTo("login") {
                                            inclusive = true
                                        }

                                    }

                                } else {

                                    Toast.makeText(
                                        context,
                                        it.exception?.message
                                            ?: "Login Failed",
                                        Toast.LENGTH_LONG
                                    ).show()

                                }

                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGreen
                        )
                    ) {

                        if (isLoading) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )

                        } else {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    "Login",
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Icon(
                                    Icons.Rounded.ArrowForward,
                                    contentDescription = null
                                )

                            }

                        }

                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        HorizontalDivider(
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = " OR ",
                            modifier = Modifier.padding(horizontal = 12.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider(
                            modifier = Modifier.weight(1f)
                        )

                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedButton(
                        onClick = {

                            val signInIntent =
                                googleSignInClient.signInIntent

                            launcher.launch(signInIntent)

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                BrandGreen
                            )
                        )
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Image(
                                painter = painterResource(R.drawable.google_logo),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(10.dp)
                            )

                            Text(
                                text = "Google Login",
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold
                            )

                        }

                    }

                    /*
                    ------------------------------------------------------
                    OTP LOGIN
                    Uncomment whenever required
                    ------------------------------------------------------

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(

                        onClick = {

                            navController.navigate("otp")

                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),

                        shape = CircleShape

                    ) {

                        Icon(
                            painter = painterResource(R.drawable.phone),
                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        Text(
                            "Continue with Phone"
                        )

                    }

                    */

                }

            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Don't have an account?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Text(

                    text = "Sign Up",

                    color = BrandGreen,

                    fontWeight = FontWeight.Bold,

                    modifier = Modifier.clickable(

                        interactionSource = remember {
                            MutableInteractionSource()
                        },

                        indication = null

                    ) {

                        navController.navigate("signup")

                    }

                )

            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

        }

    }

}