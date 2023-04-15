#!/bin/bash

set -e

# Copy the SmartTest.run file to /usr/local/bin/
sudo cp SmartTest.run /usr/local/bin/

# Create a symbolic link from /usr/local/bin/SmartTest to /usr/local/bin/SmartTest.run
sudo ln -s /usr/local/bin/SmartTest.run /usr/local/bin/SmartTest

# Set permissions on SmartTest.run to make it executable
sudo chmod +rwx /usr/local/bin/SmartTest.run

# Print a message to the user indicating that the installation is complete
echo "SmartTest has been installed successfully!"

