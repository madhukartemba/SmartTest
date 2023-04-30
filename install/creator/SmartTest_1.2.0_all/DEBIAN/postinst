#!/bin/bash

set -e

# Define the installation directory
INSTALL_DIR="/usr/local/bin"

# Check if a previous version of SmartTest exists and remove it
if [ -e "$INSTALL_DIR/SmartTest.run" ]; then
  echo "Removing previous version of SmartTest..."
  sudo rm "$INSTALL_DIR/SmartTest.run"
  sudo rm "$INSTALL_DIR/SmartTest"
fi

# Copy the SmartTest.run file to the installation directory
sudo cp SmartTest.run "$INSTALL_DIR/"

# Create a symbolic link from SmartTest to SmartTest.run
sudo ln -s "$INSTALL_DIR/SmartTest.run" "$INSTALL_DIR/SmartTest"

# Set permissions on SmartTest.run to make it executable
sudo chmod +x "$INSTALL_DIR/SmartTest.run"

# Print a message to the user indicating that the installation is complete
echo "SmartTest has been installed successfully!"
