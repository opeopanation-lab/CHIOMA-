package com.example.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.services.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

class ChiomaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = ChiomaRepository(db)
    private val prefs = application.getSharedPreferences("chioma_agent_prefs", Context.MODE_PRIVATE)

    // Observable DB Flows
    val messages: StateFlow<List<ChatMessage>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<UserNote>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customCommands: StateFlow<List<CustomCommand>> = repository.allCustomCommands
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // App Navigation State
    var currentScreen by mutableStateOf(prefs.getString("current_screen", "WelcomeScreen") ?: "WelcomeScreen")
        private set

    // Configuration Settings
    var serverHost by mutableStateOf(prefs.getString("server_host", "localhost") ?: "localhost")
        private set

    var serverPort by mutableStateOf(prefs.getInt("server_port", 8080))
        private set

    var displayName by mutableStateOf(prefs.getString("display_name", "Agent Pilot") ?: "Agent Pilot")
        private set

    var responseStyle by mutableStateOf(prefs.getString("response_style", "Igbo-mix") ?: "Igbo-mix")
        private set

    var showVerboseOutput by mutableStateOf(prefs.getBoolean("show_verbose_output", false))
        private set

    var soundEffects by mutableStateOf(prefs.getBoolean("sound_effects", true))
        private set

    var autoClearHistory by mutableStateOf(prefs.getBoolean("auto_clear_history", false))
        private set

    var showTerminalOutputRaw by mutableStateOf(prefs.getBoolean("show_terminal_raw", false))
        private set

    var commandConfirmation by mutableStateOf(prefs.getBoolean("command_confirmation", false))
        private set

    var demoMode by mutableStateOf(prefs.getBoolean("demo_mode", false)) // Default false to prioritize real connection
        private set

    // Connection Status
    var connectionStatus by mutableStateOf("Disconnected") // Connected, Disconnected, Testing...
        private set

    var connectionMessage by mutableStateOf("")
        private set

    var isTyping by mutableStateOf(false)
        private set

    init {
        // Pre-populate some custom command templates if they don't exist
        viewModelScope.launch {
            repository.allCustomCommands.collect { cmdList ->
                if (cmdList.isEmpty()) {
                    repository.insertCustomCommand(CustomCommand("show storage", "df -h"))
                    repository.insertCustomCommand(CustomCommand("quick ping", "ping -c 2 google.com"))
                    repository.insertCustomCommand(CustomCommand("say hello", "echo \"Nnoo, Agent Pilot! Chioma is here.\""))
                }
            }
        }
        
        // Try initial connection ping on launch if not in demo mode
        if (!demoMode) {
            checkConnection()
        }
    }

    fun setScreen(screen: String) {
        currentScreen = screen
        prefs.edit().putString("current_screen", screen).apply()
    }

    fun updateServerConfig(host: String, port: Int) {
        serverHost = host
        serverPort = port
        prefs.edit().putString("server_host", host).putInt("server_port", port).apply()
        checkConnection()
    }

    fun updateProfile(name: String, style: String) {
        displayName = name
        responseStyle = style
        prefs.edit().putString("display_name", name).putString("response_style", style).apply()
    }

    fun toggleShowVerbose(value: Boolean) {
        showVerboseOutput = value
        prefs.edit().putBoolean("show_verbose_output", value).apply()
    }

    fun toggleSoundEffects(value: Boolean) {
        soundEffects = value
        prefs.edit().putBoolean("sound_effects", value).apply()
    }

    fun toggleAutoClearHistory(value: Boolean) {
        autoClearHistory = value
        prefs.edit().putBoolean("auto_clear_history", value).apply()
    }

    fun toggleTerminalRaw(value: Boolean) {
        showTerminalOutputRaw = value
        prefs.edit().putBoolean("show_terminal_raw", value).apply()
    }

    fun toggleCommandConfirmation(value: Boolean) {
        commandConfirmation = value
        prefs.edit().putBoolean("command_confirmation", value).apply()
    }

    fun toggleDemoMode(value: Boolean) {
        demoMode = value
        prefs.edit().putBoolean("demo_mode", value).apply()
        if (!value) {
            checkConnection()
        } else {
            connectionStatus = "Simulated"
            connectionMessage = "Running in Simulated Mode (Zero connection limits)."
        }
    }

    fun checkConnection() {
        viewModelScope.launch {
            connectionStatus = "Testing..."
            connectionMessage = "Pinging server..."
            val url = getServerUrl()
            val result = McpClient.ping(url)
            if (result.isSuccess) {
                connectionStatus = "Connected"
                connectionMessage = "🎉 Success: ${result.message}"
            } else {
                connectionStatus = "Disconnected"
                connectionMessage = "Could not reach Termux MCP. Is NodeJS listening on port $serverPort?"
            }
        }
    }

    fun getServerUrl(): String = "http://$serverHost:$serverPort"

    // Sending Natural Language Commands
    fun sendUserMessage(inputText: String) {
        if (inputText.isBlank()) return
        val text = inputText.trim()

        viewModelScope.launch {
            // 1. Save user input message
            val userMsg = ChatMessage(sender = "user", content = text)
            repository.insertMessage(userMsg)

            isTyping = true

            // 2. Clear if automatic cleanup requested
            if (autoClearHistory && messages.value.size > 1000) {
                repository.clearAllMessages()
            }

            // 3. Attempt parsing
            val parsedResult = ChiomaParser.parse(text, customCommands.value)

            // Let's model response time
            kotlinx.coroutines.delay(600)

            if (parsedResult == null) {
                // If it's help command
                if (text.lowercase() == "help" || text.lowercase() == "commands") {
                    val helpResponse = formatHelpResponse()
                    val botMsg = ChatMessage(
                        sender = "chioma",
                        content = helpResponse,
                        commandParsed = "help",
                        commandOutput = "Help console activated.",
                        isSuccess = true
                    )
                    repository.insertMessage(botMsg)
                } else {
                    val styleNote = getStyleAlternativeGreeting()
                    val botMsg = ChatMessage(
                        sender = "chioma",
                        content = "$styleNote\n\nI can execute terminal tasks on Termux. Try stating 'check battery', 'torch on', 'vibrate', or double check custom triggers in the Profile! 😊"
                    )
                    repository.insertMessage(botMsg)
                }
                isTyping = false
                return@launch
            }

            // 4. Executing or simulating the command
            val finalCommandStr = parsedResult.command
            
            // Check if it's saving local notes
            if (finalCommandStr.startsWith("echo \"") && finalCommandStr.endsWith("~/chioma_notes.txt && echo \"Note saved!\"")) {
                val extractedNote = text.removePrefix("note ").removePrefix("remember ").trim()
                repository.insertNote(UserNote(content = extractedNote))
            } else if (finalCommandStr.startsWith("rm ~/chioma_notes.txt")) {
                repository.clearAllNotes()
            }

            if (demoMode) {
                // Return interactive high-quality simulation
                val simulationResult = generateSimulationResult(finalCommandStr, text)
                val botMsg = ChatMessage(
                    sender = "chioma",
                    content = getIgboCompletionPhrase(parsedResult.explanation) + (if (showVerboseOutput) "\n\n📊 [Command Type: ${if (parsedResult.isCustom) "Power/Custom" else "Built-in"}]" else ""),
                    commandParsed = finalCommandStr,
                    commandOutput = simulationResult.output,
                    isSuccess = true
                )
                repository.insertMessage(botMsg)
            } else {
                // Send direct HTTP request to MCP bridge
                val url = getServerUrl()
                val result = McpClient.executeCommand(url, finalCommandStr)
                val confirmationNote = if (result.isSuccess) {
                    getIgboCompletionPhrase(parsedResult.explanation)
                } else {
                    "Hmm, that didn't work. Is Termux running node server.js?\n\nDetails:\n${result.output}"
                }
                val botMsg = ChatMessage(
                    sender = "chioma",
                    content = confirmationNote,
                    commandParsed = finalCommandStr,
                    commandOutput = result.output,
                    isSuccess = result.isSuccess
                )
                repository.insertMessage(botMsg)
            }

            isTyping = false
        }
    }

    // Custom Triggers Manager
    fun addCustomCommand(pattern: String, command: String) {
        viewModelScope.launch {
            repository.insertCustomCommand(CustomCommand(pattern.trim(), command.trim()))
        }
    }

    fun removeCustomCommand(pattern: String) {
        viewModelScope.launch {
            repository.deleteCustomCommand(pattern)
        }
    }

    // Data Management
    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearAllMessages()
            // Reset with initial clean welcome message
            repository.insertMessage(ChatMessage(
                sender = "chioma",
                content = "Nnọọ! I am Chioma. Chat history cleared. Unlimited Terminal Intelligence is ready! 😊"
            ))
        }
    }

    fun clearNotes() {
        viewModelScope.launch {
            repository.clearAllNotes()
        }
    }

    fun exportChatHistoryJson(): String {
        val list = messages.value
        val arrayList = ArrayList<String>()
        list.forEach {
            val s = "{ \"sender\": \"${it.sender}\", \"content\": \"${it.content.replace("\"", "\\\"")}\", \"timestamp\": ${it.timestamp}, \"command\": \"${it.commandParsed ?: ""}\" }"
            arrayList.add(s)
        }
        return "[\n  " + arrayList.joinToString(",\n  ") + "\n]"
    }

    fun exportNotesTxt(): String {
        val list = notes.value
        return list.joinToString("\n\n---\n") {
            "Date: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date(it.timestamp))}\nContent: ${it.content}"
        }
    }

    // Visual helper mapping styling choices
    private fun getStyleAlternativeGreeting(): String {
        return when (responseStyle) {
            "Professional" -> "Acknowledged. I did not detect an exact device control script mapping in that phrase."
            "Friendly" -> "Oh, I see! I couldn't parse a device control command from that, but I'm here and ready to roll! 😊"
            else -> "Nnọọ! Aha. I didn't catch an exact Termux command in that statement. If we aren't executing, what else can I help you check on?"
        }
    }

    private fun getIgboCompletionPhrase(explanation: String): String {
        val IgboPhrases = listOf(
            "Okwu agụchaala! Done! ⚡",
            "Ije ọma! (Well done) Successfully dispatched! ⚡",
            "Mee ya ugbu a! Complete. 😊",
            "Done! What's next? ⚡",
            "Imela! Command completed freely with no limitations. 😊"
        )
        val selectedIndex = Random.nextInt(IgboPhrases.size)
        val termHeading = when (responseStyle) {
            "Professional" -> "Command executed successfully."
            "Friendly" -> "${IgboPhrases[selectedIndex]} I got that all configured for you!"
            else -> "${IgboPhrases[selectedIndex]} $explanation"
        }
        return termHeading
    }

    private fun formatHelpResponse(): String {
        return """
📚 **CHIOMA MCP COMMAND MATRIX** (100% Free Forever):

🔋 **Battery:** `battery status`, `battery health`
🔦 **Hardware:** `torch on`/`torch off`, `vibrate`
🔊 **Sound:** `volume up`, `volume down`, `mute`, `set volume to <0-15>`
💡 **Brightness:** `brightness <0-255>`, `dim screen`, `full brightness`
📂 **Files:** `list files`, `mkdir <folder>`, `delete file <file_path>`, `zip`/`unzip`
📦 **Packages:** `install <pkg>`, `uninstall <pkg>`, `update system`
📍 **Sensor/Gps:** `where am i`, `list sensors`, `storage`
📡 **Network:** `wifi on`/`wifi off`, `bluetooth on`/`bluetooth off`, `ping <host>`, `local ip`, `public ip`
📝 **Notes module:** `note <text>`, `my notes`, `clear notes`
📁 **Interactive Runtime:** `run python <file.py>`, `node <app.js>`, `bash <script.sh>`
        """.trimIndent()
    }

    // High quality terminal simulation values to fit "No Dead-end UI affordances"
    private fun generateSimulationResult(cmd: String, rawInput: String): ExecutionResult {
        return when {
            cmd.contains("battery") -> ExecutionResult(
                isSuccess = true,
                output = "{\n  \"health\": \"GOOD\",\n  \"percentage\": ${Random.nextInt(65, 95)},\n  \"plugged\": \"UNPLUGGED\",\n  \"status\": \"DISCHARGING\",\n  \"temperature\": 28.5\n}",
                exitCode = 0
            )
            cmd.contains("torch") -> ExecutionResult(
                isSuccess = true,
                output = "Flashlight subsystem updated: ${if (cmd.endsWith("on")) "LED light enabled (100% power)" else "LED light disabled"}",
                exitCode = 0
            )
            cmd.contains("volume") -> ExecutionResult(
                isSuccess = true,
                output = "Stream volume level set successfully: ${cmd.substringAfterLast(" ")}",
                exitCode = 0
            )
            cmd.contains("ls") -> ExecutionResult(
                isSuccess = true,
                output = "drwxr-xr-x 4 u0_a230 u0_a230 4096 Jun 06 12:44 .\ndrwxr-xr-x 8 u0_a230 u0_a230 4096 Jun 06 09:12 ..\n-rw-r--r-- 1 u0_a230 u0_a230 1520 Jun 06 10:15 server.js\n-rw-r--r-- 1 u0_a230 u0_a230   42 Jun 06 12:44 chioma_notes.txt\ndrwxr-xr-x 2 u0_a230 u0_a230 4096 Jun 06 12:35 storage",
                exitCode = 0
            )
            cmd.contains("location") -> ExecutionResult(
                isSuccess = true,
                output = "{\n  \"latitude\": 6.5244,\n  \"longitude\": 3.3792,\n  \"accuracy\": 15.2,\n  \"provider\": \"gps\",\n  \"timestamp\": ${System.currentTimeMillis()}\n}",
                exitCode = 0
            )
            cmd.contains("vibrate") -> ExecutionResult(
                isSuccess = true,
                output = "Device vibration pattern triggered for 500 ms.",
                exitCode = 0
            )
            cmd.contains("note") -> ExecutionResult(
                isSuccess = true,
                output = "Note appended to ~/chioma_notes.txt successfully.",
                exitCode = 0
            )
            cmd.contains("cat ~/chioma_notes.txt") -> {
                val list = notes.value
                val text = if (list.isEmpty()) "No local notes found." else list.joinToString("\n") { "• ${it.content}" }
                ExecutionResult(isSuccess = true, output = text, exitCode = 0)
            }
            cmd.contains("wifi") || cmd.contains("bluetooth") -> ExecutionResult(
                isSuccess = true,
                output = "Hardware adapter toggled: ${cmd.split(" ").last()}",
                exitCode = 0
            )
            cmd.contains("pkg") -> ExecutionResult(
                isSuccess = true,
                output = "Reading package lists... Done\nBuilding dependency tree... Done\nLatest stable releases satisfied in Termux database.",
                exitCode = 0
            )
            cmd.contains("brightness") -> ExecutionResult(
                isSuccess = true,
                output = "Display system backlight brightness mapped to level ${cmd.split(" ").last()}.",
                exitCode = 0
            )
            cmd.contains("ip") || cmd.contains("addr") -> ExecutionResult(
                isSuccess = true,
                output = "inet 192.168.1.5/24 brd 192.168.1.255 scope global wlan0",
                exitCode = 0
            )
            cmd.contains("ifconfig") -> ExecutionResult(
                isSuccess = true,
                output = "102.89.5.42",
                exitCode = 0
            )
            cmd.contains("ping") -> ExecutionResult(
                isSuccess = true,
                output = "PING google.com (142.250.190.46) 56(84) bytes of data.\n64 bytes from lhr48s27-in-f14.1e100.net: icmp_seq=1 ttl=117 time=14.2 ms\n64 bytes from lhr48s27-in-f14.1e100.net: icmp_seq=2 ttl=117 time=10.5 ms\n--- google.com ping statistics ---\n2 packets transmitted, 2 received, 0% packet loss, time 1002ms\nrtt min/avg/max/mdev = 10.511/12.355/14.200/1.844 ms",
                exitCode = 0
            )
            cmd.contains("date") || cmd.contains("time") -> ExecutionResult(
                isSuccess = true,
                output = if (cmd.contains("%I")) "08:44 AM" else "Saturday, June 06, 2026",
                exitCode = 0
            )
            cmd.contains("wttr") -> ExecutionResult(
                isSuccess = true,
                output = "☁️ +29 °C",
                exitCode = 0
            )
            else -> ExecutionResult(
                isSuccess = true,
                output = "Command output:\nExecution of direct statement completed.",
                exitCode = 0
            )
        }
    }
}
