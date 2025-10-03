# Troubleshooting Guide

## Gradle Dependency Error Fix

If you're getting the error: `'org.gradle.api.artifacts.Dependency org.gradle.api.artifacts.dsl.DependencyHandler.module(java.lang.Object)'`

### Solution Steps:

1. **Create local.properties file:**
   - Copy `local.properties.template` to `local.properties`
   - Update the SDK path to match your Android SDK installation

2. **Clean and Rebuild:**
   ```bash
   # In Android Studio terminal or command line:
   ./gradlew clean
   ./gradlew build
   ```

3. **Invalidate Caches:**
   - In Android Studio: File → Invalidate Caches and Restart
   - Choose "Invalidate and Restart"

4. **Update Android Studio:**
   - Make sure you're using Android Studio Hedgehog (2023.1.1) or later
   - Update to the latest version if possible

5. **Check Gradle Version:**
   - The project uses Gradle 8.4 (configured in gradle-wrapper.properties)
   - Android Studio should automatically download this version

### Alternative Fixes:

#### Option 1: Downgrade Compose BOM
If the issue persists, edit `app/build.gradle.kts` and change:
```kotlin
val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
```
to:
```kotlin
val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
```

#### Option 2: Use Different Gradle Version
Edit `gradle/wrapper/gradle-wrapper.properties` and change:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.4-bin.zip
```
to:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
```

### Common Issues:

1. **SDK Path Issues:**
   - Make sure `local.properties` has the correct Android SDK path
   - On macOS: `/Users/[username]/Library/Android/sdk`
   - On Windows: `C:\Users\[username]\AppData\Local\Android\Sdk`

2. **Java Version:**
   - Make sure you're using Java 17 or Java 11
   - Check in Android Studio: File → Project Structure → SDK Location

3. **Network Issues:**
   - If downloads fail, check your internet connection
   - Some corporate networks block Gradle downloads

### Still Having Issues?

1. **Create a new project** in Android Studio with similar settings
2. **Copy the working gradle files** from the new project
3. **Check Android Studio logs** in Help → Show Log in Finder/Explorer

### Verification:
After applying fixes, you should be able to:
- ✅ Sync project with Gradle files
- ✅ Build the project successfully
- ✅ Run the app on emulator/device
- ✅ See the Welcome screen with "Summarize Smarter" title
