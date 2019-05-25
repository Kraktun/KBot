@echo off
setlocal enabledelayedexpansion
cd /d  "%~dp0"
set filename=KBot.jar
::Start only last version of the program
::According to https://stackoverflow.com/a/3606659
for /f "tokens=*" %%s in ('dir /b KBot*.jar ^| sort') do ( 
	set filename=%%s
) 
cmd /c start java -jar %filename% 
