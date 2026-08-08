package com.example.spendmend.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.Person
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
import androidx.navigation.NavController
import com.example.spendmend.R
import com.example.spendmend.ui.theme.BrandGreen
import com.google.android.gms.auth.api.signin.GoogleSignIn.getClient
import com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun SignupScreen(
    navController: NavController
) {

    var fullName by remember { mutableStateOf("") }

    var email by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }

    var acceptedTerms by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

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
            ActivityResultContracts.StartActivityForResult()
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
                                    "Google Sign Up Successful",
                                    Toast.LENGTH_SHORT
                                ).show()

                                navController.navigate("main") {

                                    popUpTo("signup") {
                                        inclusive = true
                                    }

                                }

                            } else {

                                Toast.makeText(
                                    context,
                                    "Google Sign Up Failed",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        }

                }

            }

        }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF6F7FB)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 24.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(40.dp)
            )

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Start your journey to financial freedom",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(32.dp),

                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )

            ) {

                Column(

                    modifier = Modifier.padding(
                        horizontal = 22.dp,
                        vertical = 18.dp
                    )

                ) {

                    Text(
                        text = "Full Name",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    OutlinedTextField(

                        value = fullName,

                        onValueChange = {

                            fullName = it

                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),

                        leadingIcon = {

                            Icon(
                                Icons.Outlined.Person,
                                null
                            )

                        },

                        placeholder = {

                            Text("John Doe")

                        },

                        singleLine = true,

                        shape = RoundedCornerShape(18.dp),

                        colors = OutlinedTextFieldDefaults.colors(

                            focusedBorderColor = Color.Transparent,

                            unfocusedBorderColor = Color.Transparent,

                            focusedContainerColor = Color(0xFFF3F4F6),

                            unfocusedContainerColor = Color(0xFFF3F4F6)

                        )

                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Email Address",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    OutlinedTextField(

                        value = email,

                        onValueChange = {

                            email = it

                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),

                        leadingIcon = {

                            Icon(
                                Icons.Outlined.Email,
                                null
                            )

                        },

                        placeholder = {

                            Text("name@example.com")

                        },

                        singleLine = true,

                        shape = RoundedCornerShape(18.dp),

                        colors = OutlinedTextFieldDefaults.colors(

                            focusedBorderColor = Color.Transparent,

                            unfocusedBorderColor = Color.Transparent,

                            focusedContainerColor = Color(0xFFF3F4F6),

                            unfocusedContainerColor = Color(0xFFF3F4F6)

                        )

                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Password",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

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
                                Icons.Outlined.Lock,
                                null
                            )

                        },

                        trailingIcon = {

                            IconButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        if (passwordVisible)
                                            Icons.Outlined.Visibility
                                        else
                                            Icons.Outlined.VisibilityOff,
                                    contentDescription =
                                        if (passwordVisible)
                                            "Hide password"
                                        else
                                            "Show password"
                                )

                            }

                        },

                        shape = RoundedCornerShape(18.dp),

                        colors = OutlinedTextFieldDefaults.colors(

                            focusedBorderColor = Color.Transparent,

                            unfocusedBorderColor = Color.Transparent,

                            focusedContainerColor = Color(0xFFF3F4F6),

                            unfocusedContainerColor = Color(0xFFF3F4F6)

                        )

                    )

                    if (password.isNotEmpty() && password.length < 8) {

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Password must be at least 8 characters",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {

                        Checkbox(
                            checked = acceptedTerms,
                            onCheckedChange = {
                                acceptedTerms = it
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = BrandGreen
                            )
                        )

                        Spacer(
                            modifier = Modifier.width(4.dp)
                        )

                        Text(
                            text = "I agree to the Terms & Conditions and Privacy Policy",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 12.dp)
                        )

                    }

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Button(

                        onClick = {

                            when {

                                fullName.isBlank() -> {

                                    Toast.makeText(
                                        context,
                                        "Enter your name",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    return@Button
                                }

                                email.isBlank() -> {

                                    Toast.makeText(
                                        context,
                                        "Enter your email",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    return@Button
                                }

                                password.length < 8 -> {
                                    Toast.makeText(
                                        context,
                                        "Password must be at least 8 characters",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                !acceptedTerms -> {

                                    Toast.makeText(
                                        context,
                                        "Accept Terms & Conditions",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    return@Button
                                }

                            }

                            isLoading = true

                            auth.createUserWithEmailAndPassword(
                                email.trim(),
                                password.trim()
                            ).addOnCompleteListener {

                                isLoading = false

                                if (it.isSuccessful) {

                                    Toast.makeText(
                                        context,
                                        "Account Created Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    navController.navigate("main") {

                                        popUpTo("signup") {
                                            inclusive = true
                                        }

                                    }

                                } else {

                                    Toast.makeText(
                                        context,
                                        it.exception?.localizedMessage
                                            ?: "Signup Failed",
                                        Toast.LENGTH_LONG
                                    ).show()

                                }

                            }

                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                        shape = CircleShape,

                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGreen
                        )

                    ) {

                        if (isLoading) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )

                        } else {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = "Create Account",
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.width(6.dp)
                                )

                                Icon(
                                    Icons.Rounded.ArrowForward,
                                    contentDescription = null
                                )

                            }

                        }

                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

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

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    OutlinedButton(

                        onClick = {

                            launcher.launch(
                                googleSignInClient.signInIntent
                            )

                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                        shape = CircleShape,

                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White
                        ),

                        border = BorderStroke(
                            1.dp,
                            BrandGreen.copy(alpha = .6f)
                        )

                    ) {

                        Image(
                            painter = painterResource(R.drawable.google_logo),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        Text(
                            text = "Continue with Google",
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )

                    }

                    /*
                    --------------------------------------------
                    OTP SIGNUP (Enable later if required)

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            navController.navigate("otp")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = CircleShape
                    ) {

                        Icon(
                            painter = painterResource(R.drawable.phone),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Continue with Phone")

                    }

                    --------------------------------------------
                    */

                }

            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "Already have an account?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    Modifier.width(4.dp)
                )

                Text(
                    text = "Log In",
                    color = BrandGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate("login")
                    }
                )

            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Secure authentication powered by Firebase",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        }

    }

}