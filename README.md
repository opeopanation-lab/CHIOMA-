# CHIOMA AI AGENT - Unlimited Free Terminal Intelligence

## Why Chioma?
Because AI assistants shouldn't have limits. Chioma is:

∞ **UNLIMITED** - No daily caps, no usage tracking, no limits ever!
🔓 **100% FREE** - Never pay, never upgrade, no pro tiers.
🚀 **FULL FEATURES** - Every command is fully available to everyone.
❤️ **FOR EVERYONE** - Open-source, local-first paradigm with zero paywalls.

## Features (All Free)
- Control your Android device with warm, professional natural language
- 50+ built-in command patterns (battery health, flashlight, vibrate, media, sensor diagnostics...)
- Custom natural language to shell trigger mappings (add your own regex patterns in-app!)
- Chat history backups and localized TXT note exports
- High-fidelity visual simulated mode to test commands without needing a direct bridge
- 100% Offline-friendly architecture

## Installation & Setup

To sync the Android app with your physical device terminals, configure the local Termux bridge:

1. **Install Termux** from F-Droid (Recommended).
2. Inside Termux, update pack lists and install **NodeJS** + **termux-api**:
   ```bash
   pkg update && pkg upgrade -y
   pkg install nodejs termux-api -y
   ```
3. Copy the Node server script from the connection setup wizard in the app, paste it into a file called `server.js` inside Termux:
   ```bash
   nano server.js
   # Paste scripture
   ```
4. Start the Node.js MCP server:
   ```bash
   node server.js
   ```
5. Click **CONNECT TO CHIOMA** in the setup screen and get rolling!

## The Promise
Chioma will never:
- Add command limits
- Introduce premium tiers
- Collect/track your data
- Show third-party ads
- Require active subscription payments

Ever.

## License
MIT - Free forever, open source.
