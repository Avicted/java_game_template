@echo off
REM Set the project directory and other variables
SET PROJECT_DIR=%CD%
SET BIN_DIR=%PROJECT_DIR%\bin
SET RES_DIR=%PROJECT_DIR%\res
SET SRC_DIR=%PROJECT_DIR%\src
SET JAR_NAME=game.jar

REM Set Java options (if needed)
SET JAVA_OPTS=-Xmx1G -Xms512M

REM Create the bin directory if it doesn't exist
IF NOT EXIST "%BIN_DIR%" (
    mkdir "%BIN_DIR%"
)

REM Compile the project (if needed)
echo Compiling the project...
javac -d "%BIN_DIR%" -sourcepath "%SRC_DIR%" -cp "%RES_DIR%" "%SRC_DIR%\*.java"

REM Run the application
echo Running the application...
java %JAVA_OPTS% -cp "%BIN_DIR%;%RES_DIR%" com.victoranderssen.game.Game
