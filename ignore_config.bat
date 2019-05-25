@echo off
cd /d  "%~dp0"
git update-index --assume-unchanged src/main/kotlin/com/miche/krak/kBot/BotConfig.kt
