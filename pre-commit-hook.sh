#!/bin/bash

# Pre-commit hook for SummarizeAI App
# This script ensures the app builds successfully before allowing commits

echo "🔍 Running pre-commit build verification..."

# Change to project directory (go up one level from .git/hooks)
cd "$(dirname "$0")/../.."

# Run debug build
echo "📱 Building debug APK..."
if ./gradlew assembleDebug --quiet; then
    echo "✅ Debug build successful"
else
    echo "❌ Debug build failed!"
    echo "🚫 Commit rejected. Please fix build errors before committing."
    exit 1
fi

# Run tests
echo "🧪 Running tests..."
if ./gradlew test --quiet; then
    echo "✅ All tests passed"
else
    echo "❌ Tests failed!"
    echo "🚫 Commit rejected. Please fix failing tests before committing."
    exit 1
fi

echo "🎉 Build verification complete. Proceeding with commit..."
exit 0
