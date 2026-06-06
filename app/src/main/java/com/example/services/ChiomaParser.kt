package com.example.services

import java.util.regex.Pattern

data class ParsedCommand(
    val command: String,
    val isCustom: Boolean = false,
    val explanation: String = ""
)

object ChiomaParser {

    fun parse(input: String, customCommands: List<com.example.data.CustomCommand> = emptyList()): ParsedCommand? {
        val raw = input.trim()
        val text = raw.lowercase()

        // 1. Check custom commands first
        for (cc in customCommands) {
            val patternStr = cc.pattern.trim()
            try {
                // If it starts with (?i) or is standard regex
                val regex = Regex(patternStr, RegexOption.IGNORE_CASE)
                if (regex.containsMatchIn(raw)) {
                    val matchResult = regex.find(raw)
                    var expandedCommand = cc.command
                    matchResult?.groupValues?.forEachIndexed { index, groupValue ->
                        expandedCommand = expandedCommand.replace("$$index", groupValue)
                    }
                    return ParsedCommand(
                        command = expandedCommand,
                        isCustom = true,
                        explanation = "Executed custom rule: '$patternStr'"
                    )
                }
            } catch (e: Exception) {
                // Defensive catch for invalid user regexes
                if (text.contains(patternStr.lowercase())) {
                    return ParsedCommand(cc.command, isCustom = true, explanation = "Executed custom shortcut")
                }
            }
        }

        // 2. Direct matches or regexes for built-in commands
        // Battery status
        if (matchesPattern(text, "battery|check battery|battery status|how's my battery")) {
            return ParsedCommand("termux-battery-status", false, "Checks the battery level and status.")
        }
        if (matchesPattern(text, "battery info|battery health")) {
            return ParsedCommand("termux-battery-status", false, "Checks information on battery health and performance.")
        }

        // Flashlight
        if (matchesPattern(text, "torch on|flashlight on|light on|turn on light")) {
            return ParsedCommand("termux-torch on", false, "Turns on the camera flashlight.")
        }
        if (matchesPattern(text, "torch off|flashlight off|light off|turn off light")) {
            return ParsedCommand("termux-torch off", false, "Turns off the flashlight.")
        }

        // Volume controls
        if (matchesPattern(text, "volume up|increase volume|louder")) {
            return ParsedCommand("termux-volume music $(($(termux-volume get | grep music | awk '{print $3}') + 1)) 2>/dev/null || termux-volume music 10", false, "Increases music volume level.")
        }
        if (matchesPattern(text, "volume down|decrease volume|quieter")) {
            return ParsedCommand("termux-volume music $(($(termux-volume get | grep music | awk '{print $3}') - 1)) 2>/dev/null || termux-volume music 2", false, "Decreases music volume level.")
        }
        if (matchesPattern(text, "volume mute|mute|silent")) {
            return ParsedCommand("termux-volume music 0", false, "Mutes media volume.")
        }
        
        val setVolumeMatch = matchRegex(text, "set volume to (\\d+)")
        if (setVolumeMatch != null) {
            val vol = setVolumeMatch.groupValues[1]
            return ParsedCommand("termux-volume music $vol", false, "Sets media volume to $vol.")
        }

        // List files
        val listFilesMatch = matchRegex(text, "list files|show files|what's in (.*)|ls (.*)")
        if (listFilesMatch != null) {
            val target = listFilesMatch.groupValues.let { if (it.size > 2) it[2] else it[1] }.trim()
            val parsedTarget = if (target.isEmpty() || target == "show files" || target == "list files") "." else target
            return ParsedCommand("ls -la $parsedTarget", false, "Lists structure for path: '$parsedTarget'")
        }

        // System update/upgrade
        if (matchesPattern(text, "update|update packages|upgrade")) {
            return ParsedCommand("pkg update && pkg upgrade -y", false, "Updates package indexes and upgrades systems.")
        }

        // Camera capture
        if (matchesPattern(text, "take picture|capture photo|take photo")) {
            return ParsedCommand("termux-camera-photo -c 0 ~/storage/pictures/chioma_$(date +%s).jpg", false, "Takes a photo with the rear-facing camera.")
        }

        // Location Info
        if (matchesPattern(text, "my location|where am i|find me")) {
            return ParsedCommand("termux-location", false, "Retrieves current GPS or network location coordinates.")
        }

        // WiFi Toggle
        if (matchesPattern(text, "wifi on|enable wifi")) {
            return ParsedCommand("termux-wifi-enable true", false, "Enables your Wi-Fi interface.")
        }
        if (matchesPattern(text, "wifi off|disable wifi")) {
            return ParsedCommand("termux-wifi-enable false", false, "Disables your Wi-Fi interface.")
        }

        // Bluetooth Toggle
        if (matchesPattern(text, "bluetooth on|enable bluetooth")) {
            return ParsedCommand("termux-bluetooth enable", false, "Enables Bluetooth adapter.")
        }
        if (matchesPattern(text, "bluetooth off|disable bluetooth")) {
            return ParsedCommand("termux-bluetooth disable", false, "Disables Bluetooth adapter.")
        }

        // Haptics
        if (matchesPattern(text, "vibrate|buzz")) {
            return ParsedCommand("termux-vibrate -d 500", false, "Vibrates your device for 500 ms.")
        }

        // Clipboard controls
        val copyToClipboardMatch = matchRegex(text, "clipboard copy|copy (.*) to clipboard")
        if (copyToClipboardMatch != null) {
            val textToCopy = copyToClipboardMatch.groupValues[1].replace("\"", "\\\"")
            return ParsedCommand("echo \"$textToCopy\" | termux-clipboard-set", false, "Sets clipboard text.")
        }
        if (matchesPattern(text, "clipboard paste|get clipboard")) {
            return ParsedCommand("termux-clipboard-get", false, "Awaits text copied on paste clipboard.")
        }

        // Contacts Lookup
        if (matchesPattern(text, "contacts|show contacts|list contacts")) {
            return ParsedCommand("termux-contact-list", false, "Lists the contact phone numbers from address book.")
        }

        // Telephony calling
        val callMatch = matchRegex(text, "call (.*)|dial (.*)")
        if (callMatch != null) {
            var number = callMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            // clean formatting
            number = number.replace(" ", "").replace("-", "")
            return ParsedCommand("termux-telephony-call $number", false, "Places an outgoing cellular call to $number.")
        }

        // SMS Lookup Flow / SMS Send Command
        val smsMatch = matchRegex(text, "send.*to|text.*saying|message.*to")
        if (smsMatch != null) {
            return ParsedCommand("termux-contact-list && termux-sms-send", false, "Triggers contact directory filtering and dispatches SMS.")
        }
        if (matchesPattern(text, "sms|send text")) {
            return ParsedCommand("termux-contact-list", false, "Fetches contact registry profiles to open SMS routing.")
        }

        // Sensors
        if (matchesPattern(text, "sensors|what sensors")) {
            return ParsedCommand("termux-sensor -l", false, "Lists dynamic device hardware sensors.")
        }

        // Disk details
        if (matchesPattern(text, "storage|disk space|memory")) {
            return ParsedCommand("df -h", false, "Displays total remaining storage volumes.")
        }

        // Running processes
        if (matchesPattern(text, "processes|running apps|ps")) {
            return ParsedCommand("ps aux", false, "Fetches processes currently active in terminal execution context.")
        }

        // Kill process
        val killMatch = matchRegex(text, "kill (\\d+)|stop process (\\d+)")
        if (killMatch != null) {
            val pid = killMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }
            return ParsedCommand("kill $pid", false, "Kills unix background process matching PID $pid.")
        }

        // Reboot & Shutdown setup
        if (matchesPattern(text, "reboot|restart phone")) {
            return ParsedCommand("termux-reboot", false, "Asks operating kernel to prompt reboot cycle.")
        }
        if (matchesPattern(text, "shutdown|power off")) {
            return ParsedCommand("termux-shutdown", false, "Powers off the Android operating system.")
        }

        // Display Brightness controls
        val setBrightnessMatch = matchRegex(text, "brightness (\\d+)|set brightness to (\\d+)")
        if (setBrightnessMatch != null) {
            val brightnessVal = setBrightnessMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }
            return ParsedCommand("termux-brightness $brightnessVal", false, "Changes display brightness level component configuration (0-255).")
        }
        if (matchesPattern(text, "dim screen|lower brightness")) {
            return ParsedCommand("termux-brightness 50", false, "Dims screen backlight output power to 50.")
        }
        if (matchesPattern(text, "full brightness|max brightness")) {
            return ParsedCommand("termux-brightness 255", false, "Elevates display brightness limits to maximum setting 255.")
        }

        // Launcher
        val openAppMatch = matchRegex(text, "open (.*)|launch (.*)")
        if (openAppMatch != null) {
            val appToOpen = openAppMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            val finalCmd = if (appToOpen.contains("http://") || appToOpen.contains("https://") || appToOpen.contains("www.")) {
                "termux-open-url $appToOpen"
            } else {
                "am start -n $appToOpen 2>/dev/null || termux-open-url https://www.google.com/search?q=$appToOpen"
            }
            return ParsedCommand(finalCmd, false, "Launches direct activity handle or routes search URL: '$appToOpen'")
        }

        // Media capture utilities
        if (matchesPattern(text, "screenshot|capture screen")) {
            return ParsedCommand("screencap -p ~/storage/pictures/screenshot_$(date +%s).png", false, "Instructs system shell screen to compile screenshot frame output.")
        }
        if (matchesPattern(text, "record screen|start recording")) {
            return ParsedCommand("screenrecord ~/storage/movies/recording_$(date +%s).mp4", false, "Recorders real-time screen display frames to Movies.")
        }

        // Host Time & Date Lookups
        if (matchesPattern(text, "what time|current time|tell time")) {
            return ParsedCommand("date '+%I:%M %p'", false, "Returns localized current time index.")
        }
        if (matchesPattern(text, "what date|today's date")) {
            return ParsedCommand("date '+%A, %B %d, %Y'", false, "Retrieves current calendar date metadata format.")
        }

        // Weather queries
        if (matchesPattern(text, "weather|temperature")) {
            return ParsedCommand("curl -s 'wttr.in?format=%C+%t'", false, "Downloads current climate report weather status via wttr.in.")
        }

        // Network diagnostic tools
        val pingMatch = matchRegex(text, "ping (.*)|check connection to (.*)")
        if (pingMatch != null) {
            val target = pingMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            return ParsedCommand("ping -c 4 $target", false, "Executes 4-ping packet analysis connection test on '$target'.")
        }

        // Internet Downloaders
        val downloadMatch = matchRegex(text, "download (.*)|wget (.*)")
        if (downloadMatch != null) {
            val url = downloadMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            return ParsedCommand("wget $url", false, "Triggers remote download server request for transfer '$url'.")
        }

        // File/Directory structures builder
        val mkdirMatch = matchRegex(text, "make directory (.*)|mkdir (.*)")
        if (mkdirMatch != null) {
            val dir = mkdirMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            return ParsedCommand("mkdir -p \"$dir\"", false, "Creates localized multi-level directory path: '$dir'.")
        }
        val rmMatch = matchRegex(text, "delete file (.*)|remove (.*)")
        if (rmMatch != null) {
            val targetFile = rmMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            return ParsedCommand("rm -rf \"$targetFile\"", false, "Removes target file structure recursively: '$targetFile'.")
        }
        val findMatch = matchRegex(text, "search for (.*)|find (.*)")
        if (findMatch != null) {
            val term = findMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            return ParsedCommand("find . -name \"*$term*\" 2>/dev/null", false, "Searches tree for instances resembling keyword matching: '$term'")
        }

        // Archiving
        val zipMatch = matchRegex(text, "zip (.*)|compress (.*)")
        if (zipMatch != null) {
            val target = zipMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            return ParsedCommand("zip -r \"$target.zip\" \"$target\"", false, "Packs archive directory path container into visual zip format.")
        }
        val unzipMatch = matchRegex(text, "unzip (.*)|extract (.*)")
        if (unzipMatch != null) {
            val target = unzipMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            return ParsedCommand("unzip \"$target\"", false, "Extracts compressed zip archives content files.")
        }

        // Runtime interpreters
        val pythonMatch = matchRegex(text, "python (.*)|run python (.*)")
        if (pythonMatch != null) {
            val file = pythonMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            return ParsedCommand("python3 \"$file\"", false, "Executes core local Python interpreter script.")
        }
        val nodeMatch = matchRegex(text, "node (.*)|run node (.*)")
        if (nodeMatch != null) {
            val file = nodeMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            return ParsedCommand("node \"$file\"", false, "Executes package node runtime scripts.")
        }
        val bashMatch = matchRegex(text, "bash (.*)|run script (.*)")
        if (bashMatch != null) {
            val file = bashMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            return ParsedCommand("bash \"$file\"", false, "Launches executable local shell script container routines.")
        }
        val chmodMatch = matchRegex(text, "make executable (.*)|chmod (.*)")
        if (chmodMatch != null) {
            val file = chmodMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            return ParsedCommand("chmod +x \"$file\"", false, "Sets terminal flag permissions to make target executable: '$file'")
        }

        // Diagnostic logs and parameters
        if (matchesPattern(text, "environment|env vars|environment variables")) {
            return ParsedCommand("env", false, "Exposes runtime environment config lists.")
        }
        if (matchesPattern(text, "whoami|current user")) {
            return ParsedCommand("whoami", false, "Fetches current active shell user terminal name.")
        }
        if (matchesPattern(text, "hostname|device name")) {
            return ParsedCommand("hostname", false, "Returns hostname title identifiers.")
        }

        // Network diagnostic addresses
        if (matchesPattern(text, "ip address|local ip|my ip")) {
            return ParsedCommand("ip addr show | grep 'inet '", false, "Exposes interface IP network routing maps.")
        }
        if (matchesPattern(text, "public ip|external ip")) {
            return ParsedCommand("curl -s ifconfig.me", false, "Pings ifconfig.me server to return outgoing public IP address.")
        }
        val dnsMatch = matchRegex(text, "dns (.*)|lookup (.*)")
        if (dnsMatch != null) {
            val domain = dnsMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            return ParsedCommand("nslookup \"$domain\"", false, "Queries DNS nameservers for hostname resolution maps: '$domain'.")
        }

        // URLs browser launch
        val openUrlMatch = matchRegex(text, "open website (.*)|browse (.*)")
        if (openUrlMatch != null) {
            val url = openUrlMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            return ParsedCommand("termux-open-url \"$url\"", false, "Enables android-device to open browser routing target: '$url'")
        }

        // File system share bridge
        val shareMatch = matchRegex(text, "share file (.*)|send file (.*)")
        if (shareMatch != null) {
            val f = shareMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            return ParsedCommand("termux-share \"$f\"", false, "Triggers Android share sheet interface on path files: '$f'")
        }

        // Embedded Termux notes file command parser (write and clear)
        val noteCommandMatch = matchRegex(text, "note (.*)|remember (.*)")
        if (noteCommandMatch != null) {
            val noteText = noteCommandMatch.groupValues.let { if (it[1].isNotEmpty()) it[1] else it[2] }.trim()
            return ParsedCommand("echo \"$noteText\" >> ~/chioma_notes.txt && echo \"Note saved!\"", false, "Saves user records into standard notes file path.")
        }
        if (matchesPattern(text, "my notes|show notes|read notes")) {
            return ParsedCommand("cat ~/chioma_notes.txt 2>/dev/null || echo 'No notes yet'", false, "Reads current saved notes file path lines.")
        }
        if (matchesPattern(text, "clear notes|delete all notes")) {
            return ParsedCommand("rm ~/chioma_notes.txt && echo 'Notes cleared'", false, "Removes notes file record.")
        }

        // System Help dialog trigger
        if (matchesPattern(text, "help|what can you do|commands")) {
            return ParsedCommand("help", false, "Triggers localized list of all action configurations.")
        }

        // 3. Fallback or generic shell instructions trigger (If user styles like terminal console)
        if (text.startsWith("$ ") || text.startsWith("pkg ") || text.startsWith("termux-") || text.split(" ").first() in listOf("ls", "mkdir", "rm", "cd", "pwd", "ping", "curl", "wget", "ps", "top", "chmod", "cat", "echo", "grep", "mv", "cp", "tar", "git", "python", "node", "npm")) {
            val cleanCmd = raw.removePrefix("$ ").trim()
            return ParsedCommand(cleanCmd, false, "Direct shell pass-through command execution.")
        }

        // If no matching command found at all, let's return null representation
        return null
    }

    private fun matchesPattern(text: String, pattern: String): Boolean {
        return try {
            val p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
            p.matcher(text).find()
        } catch (e: Exception) {
            text.contains(pattern.lowercase())
        }
    }

    private fun matchRegex(text: String, regexPatternStr: String): MatchResult? {
        return try {
            val regex = Regex(regexPatternStr, RegexOption.IGNORE_CASE)
            regex.find(text)
        } catch (e: Exception) {
            null
        }
    }
}
