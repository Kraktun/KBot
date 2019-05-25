@echo off
cd /d  "%~dp0"
git update-index --skip-worktree src/main/kotlin/com/miche/krak/kBot/BotConfig.kt
