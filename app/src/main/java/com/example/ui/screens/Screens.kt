package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest
import com.example.data.ChatMessage
import com.example.data.CustomCommand
import com.example.services.ChiomaServerAssets
import com.example.ui.ChiomaViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

// ==========================================
// FROSTED GLASS COMPONENTS
// ==========================================
@Composable
fun FrostedGlassBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F1A))
            .drawBehind {
                // Top-left purple ambient glow sphere
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x3D7C3AED), Color(0x00000000)),
                        center = Offset(size.width * 0.15f, size.height * 0.2f),
                        radius = size.minDimension * 0.95f
                    )
                )
                // Bottom-right cyan ambient glow sphere
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x2B00E5FF), Color(0x00000000)),
                        center = Offset(size.width * 0.85f, size.height * 0.75f),
                        radius = size.minDimension * 0.95f
                    )
                )
                
                // Tech ambient grid meshes
                val strokeWidth = 0.5.dp.toPx()
                val gridSpacing = 80.dp.toPx()
                for (x in 0 until (size.width / gridSpacing).toInt() + 1) {
                    drawLine(
                        color = Color(0x0800E5FF),
                        start = Offset(x * gridSpacing, 0f),
                        end = Offset(x * gridSpacing, size.height),
                        strokeWidth = strokeWidth
                    )
                }
                for (y in 0 until (size.height / gridSpacing).toInt() + 1) {
                    drawLine(
                        color = Color(0x0800E5FF),
                        start = Offset(0f, y * gridSpacing),
                        end = Offset(size.width, y * gridSpacing),
                        strokeWidth = strokeWidth
                    )
                }
            }
    ) {
        content()
    }
}

// ==========================================
// WELCOME SCREEN
// ==========================================
@Composable
fun WelcomeScreen(viewModel: ChiomaViewModel) {
    val scrollState = rememberScrollState()

    FrostedGlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Frosted Brand Infinity Logo
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(GlassmorphismSurfaceColor)
                    .border(1.dp, GlassmorphismBorderColor, CircleShape)
                    .drawBehind {
                        drawCircle(
                            color = ElectricCyan,
                            style = Stroke(width = 2.dp.toPx()),
                            radius = size.minDimension / 2f - 4.dp.toPx()
                        )
                        drawCircle(
                            color = ChiomaPurple,
                            style = Stroke(width = 1.dp.toPx()),
                            radius = size.minDimension / 2.3f - 4.dp.toPx()
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "C",
                        fontSize = 58.sp,
                        fontWeight = FontWeight.Black,
                        color = ElectricCyan,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "∞",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan,
                        modifier = Modifier.offset(y = (-14).dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App name title with modern styling
            Text(
                text = "CHIOMA AI AGENT",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ElectricCyan,
                textAlign = TextAlign.Center,
                letterSpacing = 1.5.sp
            )

            // Dynamic Tagline
            Text(
                text = "Intelligence Without Limits",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Badges Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WelcomeBadge(label = "∞ Unlimited Commands", color = ElectricCyan)
                Spacer(modifier = Modifier.width(8.dp))
                WelcomeBadge(label = "🔓 100% Free", color = SuccessGreen)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                WelcomeBadge(label = "🚀 Full Features Active", color = ChiomaPurple)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Description
            Text(
                text = "A completely open-source personal terminal intelligence assistant. Chioma connects securely to Termux local servers behind localhost:8080 or demo modes, providing complete device control with zero limits or fee gates. Proudly African-Futurist.",
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(40.dp))

            // Beautiful Gradient action button with glass-theme blend
            Button(
                onClick = { viewModel.setScreen("ConnectScreen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = ChiomaPurple.copy(alpha = 0.85f))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "START USING CHIOMA",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Arrow Forward", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun WelcomeBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .background(GlassmorphismSurfaceColor, RoundedCornerShape(12.dp))
            .border(1.dp, GlassmorphismBorderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}


// ==========================================
// CONNECT SCREEN (TERMUX SETUP)
// ==========================================
@Composable
fun ConnectScreen(viewModel: ChiomaViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    var hostInput by remember { mutableStateOf(viewModel.serverHost) }
    var portInput by remember { mutableStateOf(viewModel.serverPort.toString()) }
    var step1Confirmed by remember { mutableStateOf(false) }

    FrostedGlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // Header Info as a beautiful Glass floating card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassmorphismSurfaceColor)
                    .border(1.dp, GlassmorphismBorderColor, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.setScreen("WelcomeScreen") },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = ElectricCyan)
                }
                Text(
                    text = "TERMUX MCP SETUP",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ElectricCyan,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .background(Color(0x1F00E5FF), RoundedCornerShape(8.dp))
                        .border(1.dp, ElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Wizard", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 1 Column
            SetupCard(stepTitle = "01 Install Termux Terminal") {
                Text(
                    text = "Please install the official, open-source Termux application on your Android system (Highly recommended: F-Droid release).",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            val webIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://f-droid.org/en/packages/com.termux/"))
                            context.startActivity(webIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ChiomaDeepPurple.copy(alpha = 0.8f)),
                        border = BorderStroke(1.dp, Color(0x2BFFFFFF)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Get Termux on F-Droid", fontSize = 12.sp, color = ElectricCyan)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = step1Confirmed,
                            onCheckedChange = { step1Confirmed = it },
                            colors = CheckboxDefaults.colors(checkedColor = ElectricCyan, uncheckedColor = Color(0x33FFFFFF))
                        )
                        Text("Installed", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step 2 Column
            SetupCard(stepTitle = "02 Deploy Local MCP Bridge") {
                Text(
                    text = "First trigger Termux updates, install Node.js systems, then paste the server script into `server.js` and run node server.",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33070712), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0x1400E5FF), RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "pkg update && pkg install nodejs termux-api -y\n# Paste script into server.js\nnode server.js",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = ElectricCyan
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(ChiomaServerAssets.SERVER_SCRIPT_JS))
                            Toast.makeText(context, "Server.js script copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ChiomaPurple.copy(alpha = 0.85f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Copy Icon", modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Server Script", fontSize = 12.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step 3 Column
            SetupCard(stepTitle = "03 Test Connection Parameters") {
                Text(
                    text = "Configure your target IP host address and port listening coordinates (Default: localhost on 8080).",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = hostInput,
                        onValueChange = { hostInput = it },
                        label = { Text("Server Host IP", color = TextSecondary, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = Color(0x22FFFFFF),
                            focusedContainerColor = Color(0x1FFFFFFF),
                            unfocusedContainerColor = Color(0x0DFFFFFF),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(2f),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = portInput,
                        onValueChange = { portInput = it },
                        label = { Text("Port", color = TextSecondary, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = Color(0x22FFFFFF),
                            focusedContainerColor = Color(0x1FFFFFFF),
                            unfocusedContainerColor = Color(0x0DFFFFFF),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val parsedPort = portInput.toIntOrNull() ?: 8080
                        viewModel.updateServerConfig(hostInput, parsedPort)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ChiomaPurple.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("CONNECT TO CHIOMA", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Server Status Indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x1400E5FF), RoundedCornerShape(10.dp))
                        .border(1.dp, ElectricCyan.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        when (viewModel.connectionStatus) {
                                            "Connected" -> SuccessGreen
                                            "Testing..." -> ElectricCyan
                                            "Simulated" -> ElectricCyan
                                            else -> ErrorRed
                                        },
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Status: ${viewModel.connectionStatus.uppercase()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = viewModel.connectionMessage.ifEmpty { "Ready to verify connections." },
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Demo Mode Option Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassmorphismSurfaceColor)
                    .border(1.dp, GlassmorphismBorderColor, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "💡 Run in Offline Simulation?",
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Zero bridges needed! Enable simulation mode to try all 50+ command patterns interactively in AI Studio preview.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = viewModel.demoMode,
                        onCheckedChange = { viewModel.toggleDemoMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ElectricCyan,
                            checkedTrackColor = ChiomaPurple.copy(alpha = 0.7f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Continue Buttons
            Button(
                onClick = { viewModel.setScreen("ChatScreen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(
                        1.dp,
                        Color(0x33FFFFFF),
                        RoundedCornerShape(28.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewModel.demoMode || viewModel.connectionStatus == "Connected") ChiomaPurple.copy(alpha = 0.85f) else Color(0x22FFFFFF)
                )
            ) {
                Text(
                    text = "PROCEED TO INTELLIGENCE TERMINAL →",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SetupCard(stepTitle: String, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassmorphismSurfaceColor)
            .border(1.dp, GlassmorphismBorderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = stepTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = ElectricCyan,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}


// ==========================================
// CHAT SCREEN
// ==========================================
@Composable
fun ChatScreen(viewModel: ChiomaViewModel) {
    val messages by viewModel.messages.collectAsState()
    val scrollState = rememberScrollState()
    var inputQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isListening by remember { mutableStateOf(false) }
    var listeningError by remember { mutableStateOf<String?>(null) }

    val speechHelper = remember {
        com.example.services.SpeechRecognizerHelper(
            context = context,
            onResults = { result ->
                inputQuery = result
                isListening = false
                listeningError = null
                Toast.makeText(context, "Heard: \"$result\"", Toast.LENGTH_SHORT).show()
            },
            onPartialResults = { partial ->
                inputQuery = partial
            },
            onError = { err ->
                isListening = false
                listeningError = err
                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
            },
            onListeningStateChange = { state ->
                isListening = state
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            speechHelper.destroy()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isListening = true
            speechHelper.startListening()
        } else {
            Toast.makeText(context, "Microphone permission is required for Voice input.", Toast.LENGTH_SHORT).show()
        }
    }

    FrostedGlassBackground {
        Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
            // Screen Header Configuration as glassmorphism top header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlassmorphismSurfaceColor)
                    .drawBehind {
                        // thin white border bottom simulation
                        drawLine(
                            color = GlassmorphismBorderColor,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Chioma Avatar
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Brush.radialGradient(listOf(ChiomaPurple, ChiomaDeepPurple)), CircleShape)
                            .border(1.dp, ElectricCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("C", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CHIOMA AI",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElectricCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (viewModel.demoMode) "Offline Sim Mode" else "Terminal Node: active",
                            fontSize = 11.sp,
                            color = SuccessGreen
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Unlimited Indicator Badge with frosted purple backdrop
                    Box(
                        modifier = Modifier
                            .background(Color(0x2B7C3AED), RoundedCornerShape(8.dp))
                            .border(1.dp, ElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "∞ Unlimited",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Navigate to Profile
                    IconButton(
                        onClick = { viewModel.setScreen("ProfileScreen") },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Profile settings check", tint = ElectricCyan)
                    }
                }
            }

            // Quick Hotkeys Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickCommandChip(label = "Check Battery 🔋") { inputQuery = "Check battery status" }
                QuickCommandChip(label = "Flashlight 🔦") { inputQuery = "Torch on" }
                QuickCommandChip(label = "List Files 📂") { inputQuery = "List files in directory" }
                QuickCommandChip(label = "Vibrate 📳") { inputQuery = "Vibrate" }
                QuickCommandChip(label = "Show Help ❓") { inputQuery = "Help" }
            }

            HorizontalDivider(color = Color(0x0DFFFFFF))

            // Body chat messages
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                if (messages.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Empty Console placeholder", tint = TextSecondary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Unlimited Console Initialized",
                            color = ElectricCyan,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Type any natural statement or trigger. Try 'whoami', 'weather', 'sysinfo', or note reminders.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 2.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        reverseLayout = false,
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(messages) { msg ->
                            ChatBubbleItem(msg = msg)
                        }
                        if (viewModel.isTyping) {
                            item {
                                TypingIndicatorBlock()
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0x0DFFFFFF))

            // Bottom Input Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlassmorphismSurfaceColor)
                    .drawBehind {
                        // top border bottom simulation
                        drawLine(
                            color = GlassmorphismBorderColor,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    placeholder = { Text(if (isListening) "Listening carefully..." else "Ask Chioma anything...", color = TextSecondary.copy(alpha = 0.6f), fontSize = 14.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = Color(0x1BFFFFFF),
                        focusedContainerColor = Color(0x1AFFFFFF),
                        unfocusedContainerColor = Color(0x0DFFFFFF),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (isListening) {
                            speechHelper.stopListening()
                        } else {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasPermission) {
                                isListening = true
                                speechHelper.startListening()
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (isListening) Color(0x3300E5FF) else Color(0x0DFFFFFF),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            if (isListening) ElectricCyan else Color(0x1BFFFFFF),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Voice command input",
                        tint = if (isListening) ElectricCyan else Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputQuery.isNotBlank()) {
                            viewModel.sendUserMessage(inputQuery)
                            inputQuery = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(ChiomaPurple.copy(alpha = 0.9f), CircleShape)
                        .border(1.dp, Color(0x33FFFFFF), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send trigger command", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun QuickCommandChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GlassmorphismSurfaceColor)
            .border(1.dp, GlassmorphismBorderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text = label, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ChatBubbleItem(msg: ChatMessage) {
    var isExpanded by remember { mutableStateOf(false) }

    val userBubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 0.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )
    
    val chiomaBubbleShape = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 16.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (msg.sender == "user") Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = if (msg.sender == "user") {
                        Brush.linearGradient(colors = listOf(Color(0xFF7C3AED), Color(0xFF6366F1)))
                    } else {
                        Brush.linearGradient(colors = listOf(GlassmorphismSurfaceColor, GlassmorphismSurfaceColor))
                    },
                    shape = if (msg.sender == "user") userBubbleShape else chiomaBubbleShape
                )
                .border(
                    1.dp,
                    if (msg.sender == "user") Color(0x33FFFFFF) else GlassmorphismBorderColor,
                    shape = if (msg.sender == "user") userBubbleShape else chiomaBubbleShape
                )
                .padding(14.dp)
                .widthIn(max = 280.dp)
        ) {
            Column {
                Text(
                    text = msg.content,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                // If this is a terminal trigger executing unix statements, show execution details
                if (msg.commandParsed != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x33000000), RoundedCornerShape(8.dp))
                            .border(0.5.dp, Color(0x22FFFFFF), RoundedCornerShape(8.dp))
                            .clickable { isExpanded = !isExpanded }
                            .padding(8.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$ ${msg.commandParsed}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = ElectricCyan,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expand/Collapse Terminal stdout logs",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    HorizontalDivider(color = Color(0x16FFFFFF))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = msg.commandOutput ?: "Command executed successfully.",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = SuccessGreen,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Text(
            text = java.text.SimpleDateFormat("I:mm a").format(java.util.Date(msg.timestamp)),
            fontSize = 9.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
        )
    }
}

@Composable
fun TypingIndicatorBlock() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GlassmorphismSurfaceColor)
            .border(1.dp, GlassmorphismBorderColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .width(80.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val alpha1 by infiniteTransition.animateFloat(
            initialValue = 0.2f, targetValue = 1f,
            animationSpec = infiniteRepeatable(animation = tween(600, delayMillis = 0), repeatMode = RepeatMode.Reverse)
        )
        val alpha2 by infiniteTransition.animateFloat(
            initialValue = 0.2f, targetValue = 1f,
            animationSpec = infiniteRepeatable(animation = tween(600, delayMillis = 200), repeatMode = RepeatMode.Reverse)
        )
        val alpha3 by infiniteTransition.animateFloat(
            initialValue = 0.2f, targetValue = 1f,
            animationSpec = infiniteRepeatable(animation = tween(600, delayMillis = 400), repeatMode = RepeatMode.Reverse)
        )

        Box(modifier = Modifier.size(6.dp).background(ElectricCyan.copy(alpha = alpha1), CircleShape))
        Box(modifier = Modifier.size(6.dp).background(ElectricCyan.copy(alpha = alpha2), CircleShape))
        Box(modifier = Modifier.size(6.dp).background(ElectricCyan.copy(alpha = alpha3), CircleShape))
    }
}


// ==========================================
// PROFILE & FEATURES SETTINGS SCREEN
// ==========================================
@Composable
fun ProfileScreen(viewModel: ChiomaViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val customCommands by viewModel.customCommands.collectAsState()
    val notes by viewModel.notes.collectAsState()

    var userNameInput by remember { mutableStateOf(viewModel.displayName) }
    var userStyleInput by remember { mutableStateOf(viewModel.responseStyle) }
    
    // Custom Command Add triggers
    var customPattern by remember { mutableStateOf("") }
    var customCommandText by remember { mutableStateOf("") }
    
    var showAddCommandDialog by remember { mutableStateOf(false) }

    FrostedGlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header floating card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassmorphismSurfaceColor)
                    .border(1.dp, GlassmorphismBorderColor, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.setScreen("ChatScreen") }, modifier = Modifier.size(40.dp)) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Return back", tint = ElectricCyan)
                }
                Text(
                    text = "AGENT PREFERENCES",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ElectricCyan,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .background(Color(0x1F00E5FF), RoundedCornerShape(8.dp))
                        .border(1.dp, ElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("∞ Free", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Card Settings
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassmorphismSurfaceColor)
                    .border(1.dp, GlassmorphismBorderColor, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Brush.radialGradient(listOf(ChiomaPurple, ChiomaDeepPurple)), CircleShape)
                                .border(1.5.dp, ElectricCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (userNameInput.isNotEmpty()) userNameInput.first().toString().uppercase() else "A",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = userNameInput,
                                onValueChange = {
                                    userNameInput = it
                                    viewModel.updateProfile(it, userStyleInput)
                                },
                                label = { Text("Display Name", color = TextSecondary, fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = ElectricCyan,
                                    unfocusedBorderColor = Color(0x1BFFFFFF),
                                    focusedContainerColor = Color(0x1AFFFFFF),
                                    unfocusedContainerColor = Color(0x0DFFFFFF)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    
                    // Style selection Row
                    Text("Chioma's Voice PersonalityStyle", color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Igbo-mix", "Friendly", "Professional").forEach { style ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (userStyleInput == style) ChiomaDeepPurple.copy(alpha = 0.8f) else GlassmorphismSurfaceColor
                                    )
                                    .border(
                                        1.dp,
                                        if (userStyleInput == style) ElectricCyan else GlassmorphismBorderColor,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        userStyleInput = style
                                        viewModel.updateProfile(userNameInput, style)
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = style, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Settings Configurations
            Text("ENGINE CONFIGURATION", color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x0DFFFFFF))
                    .border(1.dp, Color(0x1BFFFFFF), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Simulation mode", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Allows commands executing as simulation models local-only.", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = viewModel.demoMode,
                            onCheckedChange = { viewModel.toggleDemoMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ElectricCyan,
                                checkedTrackColor = ChiomaPurple.copy(alpha = 0.7f)
                            )
                        )
                    }
                    HorizontalDivider(color = Color(0x0DFFFFFF))

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Verbose outputs", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Expose underlying custom parsed shell headers in output chats.", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = viewModel.showVerboseOutput,
                            onCheckedChange = { viewModel.toggleShowVerbose(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ElectricCyan,
                                checkedTrackColor = ChiomaPurple.copy(alpha = 0.7f)
                            )
                        )
                    }
                    HorizontalDivider(color = Color(0x0DFFFFFF))

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Auto-clear Chat History", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Cleans old thread history automatically when exceeding 1000 items.", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = viewModel.autoClearHistory,
                            onCheckedChange = { viewModel.toggleAutoClearHistory(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ElectricCyan,
                                checkedTrackColor = ChiomaPurple.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Custom commands config trigger mapping
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("CUSTOM CLI TRIGGER MAPS", color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Black)
                IconButton(onClick = { showAddCommandDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add maps", tint = ElectricCyan)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            if (customCommands.isEmpty()) {
                Text(
                    "No custom commands registered. Tap '+' to build your own maps.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassmorphismSurfaceColor)
                        .border(1.dp, GlassmorphismBorderColor, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    customCommands.forEach { cc ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = cc.pattern, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(text = "$ ${cc.command}", color = ElectricCyan, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            }
                            IconButton(onClick = { viewModel.removeCustomCommand(cc.pattern) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete custom maps", tint = ErrorRed, modifier = Modifier.size(18.dp))
                            }
                        }
                        HorizontalDivider(color = Color(0x0DFFFFFF))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Data utility exports
            Text("DATA BACKUP & CLEANING UTILITIES", color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassmorphismSurfaceColor)
                    .border(1.dp, GlassmorphismBorderColor, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            val json = viewModel.exportChatHistoryJson()
                            clipboardManager.setText(AnnotatedString(json))
                            Toast.makeText(context, "Exported JSON chats copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ChiomaDeepPurple.copy(alpha = 0.8f)),
                        border = BorderStroke(1.dp, Color(0x2BFFFFFF)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Export Chats (JSON Copied)", fontSize = 12.sp, color = ElectricCyan)
                    }

                    Button(
                        onClick = {
                            val txt = viewModel.exportNotesTxt()
                            clipboardManager.setText(AnnotatedString(txt))
                            Toast.makeText(context, if (txt.isBlank()) "No notes registered yet!" else "Notes TXT copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ChiomaDeepPurple.copy(alpha = 0.8f)),
                        border = BorderStroke(1.dp, Color(0x2BFFFFFF)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Export Notes (TXT Copied)", fontSize = 12.sp, color = ElectricCyan)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.clearChatHistory() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x1FFF0055)),
                            border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.4f)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Clear Chat", fontSize = 11.sp, color = ErrorRed)
                        }

                        Button(
                            onClick = { viewModel.clearNotes() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x1FFF0055)),
                            border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.4f)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Clear Notes", fontSize = 11.sp, color = ErrorRed)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About footer information
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("CHIOMA AI AGENT - v1.0.0", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("∞ Unlimited commands - completely free", color = TextSecondary, fontSize = 10.sp)
                Text("Made with ❤️ for everyone.", color = TextSecondary, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // Add custom mapping dialog
        if (showAddCommandDialog) {
            AlertDialog(
                onDismissRequest = { showAddCommandDialog = false },
                containerColor = Color(0xEE131324),
                modifier = Modifier.border(1.dp, Color(0x1BFFFFFF), RoundedCornerShape(28.dp)),
                title = { Text("Add Custom CLI Trigger", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Enter a natural text phrase pattern, and specify the exact Termux shell statement it triggers.", color = TextSecondary, fontSize = 12.sp)
                        OutlinedTextField(
                            value = customPattern,
                            onValueChange = { customPattern = it },
                            label = { Text("Search Pattern (e.g., 'sys status')", color = TextSecondary, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = Color(0x1BFFFFFF),
                                focusedContainerColor = Color(0x1AFFFFFF),
                                unfocusedContainerColor = Color(0x0DFFFFFF)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = customCommandText,
                            onValueChange = { customCommandText = it },
                            label = { Text("Command statement (e.g., 'uptime')", color = TextSecondary, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = Color(0x1BFFFFFF),
                                focusedContainerColor = Color(0x1AFFFFFF),
                                unfocusedContainerColor = Color(0x0DFFFFFF)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (customPattern.isNotBlank() && customCommandText.isNotBlank()) {
                                viewModel.addCustomCommand(customPattern, customCommandText)
                                customPattern = ""
                                customCommandText = ""
                                showAddCommandDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ChiomaPurple.copy(alpha = 0.9f)),
                        border = BorderStroke(1.dp, Color(0x2BFFFFFF)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Mapping", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showAddCommandDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text("Dismiss", color = TextSecondary)
                    }
                }
            )
        }
    }
}
