#!/bin/bash

# Exit on error
set -e

# Copy mock google-services file
echo "Using mock google-services.json"
cp .buildscript/mock-google-services.json app/google-services.json