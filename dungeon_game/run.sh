#!/bin/bash

# Set the name of the project directory (adjust as necessary)
PROJECT_DIR="$(pwd)"
BIN_DIR="$PROJECT_DIR/bin"
RES_DIR="$PROJECT_DIR/res"
SRC_DIR="$PROJECT_DIR/src"
JAR_NAME="dungeon_game.jar"

# Set Java options (if needed)
JAVA_OPTS="-Xmx1G -Xms512M"

# Create the bin directory if it doesn't exist
if [ ! -d "$BIN_DIR" ]; then
    mkdir "$BIN_DIR"
fi

# Compile the project (if needed, skip this if already compiled)
echo "Compiling the project..."
javac -d "$BIN_DIR" -sourcepath "$SRC_DIR" -cp "$RES_DIR" $(find "$SRC_DIR" -name "*.java")

# Run the application
echo "Running the application..."
java $JAVA_OPTS -cp "$BIN_DIR:$RES_DIR" com.victoranderssen.dungeon_game.Game
