@echo off
setlocal enabledelayedexpansion
cd /d  "%~dp0"
set filename=KBot.jar
if not exist logs mkdir logs
if not exist downloads mkdir downloads
::Start only last version of the program
::According to https://stackoverflow.com/a/3606659
for /f "tokens=*" %%s in ('dir /b KBot*.jar ^| sort') do ( 
	set filename=%%s
) 
cmd /c start java -jar %filename% 
