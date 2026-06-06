package com.example.services

object ChiomaServerAssets {

    val SERVER_SCRIPT_JS = """
// server.js - Simple, unlimited, no tracking
const express = require('express');
const { exec } = require('child_process');
const app = express();

app.use(express.json());
app.use((req, res, next) => {
  // No API keys, no rate limiting, no tracking
  res.header('X-Chioma', 'Free-Forever');
  next();
});

app.get('/ping', (req, res) => {
  res.json({ 
    status: 'ok', 
    agent: 'Chioma',
    message: 'Unlimited. Free. Ready.',
    timestamp: new Date().toISOString()
  });
});

app.post('/run', (req, res) => {
  const { command } = req.body;
  console.log("🚀 Executing command: " + command);
  
  exec(command, { 
    timeout: 30000,
    shell: '/system/bin/sh',
    cwd: process.env.HOME
  }, (error, stdout, stderr) => {
    const output = stdout || stderr || (error ? error.message : '');
    res.json({
      output: output.trim() || 'Command completed',
      exitCode: error ? 1 : 0,
      chiomaNote: "Nnoo! Command executed freely 😊"
    });
  });
});

app.listen(8080, () => {
  console.log('🌟 CHIOMA MCP SERVER');
  console.log('∞ Unlimited commands');
  console.log('🔓 100% Free');
  console.log('📍 http://localhost:8080');
});
""".trimIndent()

    val SETUP_INSTRUCTIONS_MARKDOWN = """
# Setup Instructions:
1. Download Open-source Termux application (Recommended: F-Droid build).
2. Inside Termux, run following initial layout commands:
   pkg update && pkg upgrade -y
   pkg install nodejs termux-api -y
3. Put the server script inside Termux as `server.js`:
   nano server.js (Paste what is copied)
4. Launch the server script inside Termux commandline:
   node server.js
5. Click 'CONNECT TO CHIOMA' in-app at port 8080!
""".trimIndent()
}
