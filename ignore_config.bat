@echo off
:: Ignore local updates to config file, but pull upstream changes
cd /d  "%~dp0"
:: git update-index --no-skip-worktree src/main/kotlin/com/miche/krak/kBot/BotConfig.kt
git update-index --skip-worktree src/main/kotlin/com/kraktun/kbot/BotConfig.kt
