@echo off
cd /d  "%~dp0"
cd ../..
::build jar 
gradlew jar
pause
