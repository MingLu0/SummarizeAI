#!/bin/bash

# Navigation Flow Test Runner
# This script runs the navigation tests to verify the back button and bottom tab navigation

echo "🧪 Running Navigation Flow Tests..."
echo "=================================="

# Clean and build the project
echo "📦 Building project..."
./gradlew clean assembleDebug

if [ $? -ne 0 ]; then
    echo "❌ Build failed. Please fix build errors before running tests."
    exit 1
fi

echo "✅ Build successful!"

# Install the app
echo "📱 Installing app..."
./gradlew installDebug

if [ $? -ne 0 ]; then
    echo "❌ Installation failed."
    exit 1
fi

echo "✅ App installed successfully!"

# Run the navigation tests
echo "🚀 Running navigation tests..."
echo ""

# Run individual test classes
echo "1️⃣ Running NavigationFlowTest..."
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.summarizeai.ui.NavigationFlowTest

echo ""
echo "2️⃣ Running NavigationIntegrationTest..."
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.summarizeai.ui.NavigationIntegrationTest

echo ""
echo "3️⃣ Running OutputScreenTest..."
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.summarizeai.ui.OutputScreenTest

echo ""
echo "🎉 Navigation tests completed!"
echo ""
echo "📊 Test Results Summary:"
echo "- NavigationFlowTest: Tests individual navigation components"
echo "- NavigationIntegrationTest: Tests full navigation flow"
echo "- OutputScreenTest: Tests Output screen functionality"
echo ""
echo "💡 If tests fail, check the test reports in:"
echo "   app/build/reports/androidTests/connected/"

