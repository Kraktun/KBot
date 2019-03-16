@echo off
cd /d  "%~dp0"
::build jar 
gradlew jar
pause
