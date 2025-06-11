#!/bin/bash

echo "🛠 Compiling Java files..."
javac Game.java GameFrame.java GamePanel.java

if [ $? -eq 0 ]; then
    echo "🚀 Launching Snake Game..."
    java Game
else
    echo "❌ Compilation failed."
fi
