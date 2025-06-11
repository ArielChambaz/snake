#!/bin/bash

echo "🛠 Compiling Java files..."
javac -cp .:lib/json-simple-1.1.1.jar Game.java GameFrame.java GamePanel.java GameMenu.java

if [ $? -eq 0 ]; then
    echo "🚀 Launching Snake Game..."
    java -cp .:lib/json-simple-1.1.1.jar Game
else
    echo "❌ Compilation failed."
fi
