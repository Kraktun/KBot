@echo off
cd /d  "%~dp0"
set current=%~dp0
cd ..
echo BUILDING ZIP
call gradlew distZip
move build\distributions\*.zip %current%
echo BUILDING JAR
call gradlew shadowJar
move build\libs\*-all.jar %current%
pause
