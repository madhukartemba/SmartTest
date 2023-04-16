#!/bin/bash

set -e

# Remove the SmartTest symbolic link
sudo rm /usr/local/bin/SmartTest

# Remove the SmartTest.run file
sudo rm /usr/local/bin/SmartTest.run

# Print a message to the user indicating that the uninstallation is complete
echo "SmartTest has been uninstalled successfully!"

