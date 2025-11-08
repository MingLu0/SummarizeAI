# Deployment Guide for Nutshell App

This guide covers how to build, sign, and deploy the Nutshell Android app using GitHub Actions and Firebase App Distribution.

## Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Initial Setup](#initial-setup)
- [Deploying to Firebase](#deploying-to-firebase)
- [Local Development Builds](#local-development-builds)
- [Troubleshooting](#troubleshooting)
- [Future: Play Store Publishing](#future-play-store-publishing)

## Overview

The Nutshell app uses a fully automated CI/CD pipeline:

```
Push to main → GitHub Actions → Build & Test → Sign APK → Firebase App Distribution → Testers Notified
```

**Key Components:**
- **GitHub Actions**: Automates build, test, and deployment
- **Firebase App Distribution**: Distributes APK to beta testers
- **Gradle Signing Config**: Signs APK with release keystore
- **Automated Testing**: Runs unit tests before building

## Prerequisites

Before you can deploy, you need:

1. ✅ **Release Keystore Generated** (see [Generate Keystore](#generate-keystore) below)
2. ✅ **Firebase Project Set Up** (see [FIREBASE_SETUP.md](./FIREBASE_SETUP.md))
3. ✅ **GitHub Secrets Configured** (see [FIREBASE_SETUP.md](./FIREBASE_SETUP.md#step-7-configure-github-secrets))

## Initial Setup

### Generate Keystore

If you haven't already, generate a production keystore:

```bash
keytool -genkey -v -keystore nutshell-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias nutshell
```

**You'll be prompted for:**
- Keystore password (choose a strong password)
- Key password (can be same as keystore password)
- Your name/organization details

**Save these securely:**
- The `nutshell-release.jks` file
- Keystore password
- Key alias (`nutshell`)
- Key password

> ⚠️ **Critical**: If you lose the keystore, you cannot update your app in the Play Store. Back it up securely!

### Encode Keystore for GitHub

To use the keystore in GitHub Actions, encode it to base64:

**macOS:**
```bash
base64 -i nutshell-release.jks | pbcopy
```

**Linux:**
```bash
base64 nutshell-release.jks
```

The base64 string is now in your clipboard (macOS) or printed to terminal (Linux). Save this for the `KEYSTORE_FILE` GitHub secret.

## Deploying to Firebase

### Automatic Deployment (Recommended)

Every push to the `main` branch automatically triggers deployment:

```bash
git add .
git commit -m "feat: add new feature"
git push origin main
```

**What happens:**
1. GitHub Actions workflow starts
2. Code is checked out
3. JDK 17 is set up
4. Keystore is decoded from secrets
5. Unit tests run
6. Release APK is built and signed
7. APK is uploaded to Firebase App Distribution
8. Testers in the "testers" group receive notification

**Monitoring the workflow:**
1. Go to your GitHub repository
2. Click the **Actions** tab
3. Click on the latest workflow run
4. Watch the progress in real-time

### Manual Deployment

You can also trigger deployment manually:

1. Go to **Actions** tab in GitHub
2. Click **Firebase App Distribution** workflow
3. Click **Run workflow**
4. Select branch (usually `main`)
5. Click **Run workflow**

### Release Tags

For versioned releases, create a tag:

```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

This triggers the workflow and creates a GitHub release.

## Local Development Builds

### Build Debug APK (No Keystore Needed)

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

### Build Release APK Locally

If you have the keystore configured locally:

1. **Option A**: Set environment variables:

```bash
export KEYSTORE_FILE="$(base64 -i nutshell-release.jks)"
export KEYSTORE_PASSWORD="your_keystore_password"
export KEY_ALIAS="nutshell"
export KEY_PASSWORD="your_key_password"

./gradlew assembleRelease
```

2. **Option B**: Add to `gradle.properties` (in project root):

```properties
KEYSTORE_FILE=/path/to/nutshell-release.jks
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=nutshell
KEY_PASSWORD=your_key_password
```

Then build:

```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

> ⚠️ **Important**: Never commit `gradle.properties` with credentials to git!

### Run Tests Locally

```bash
# Unit tests only
./gradlew test

# All tests with lint
./gradlew check

# Verbose output
./gradlew test --info
```

## Deployment Workflow Explained

### GitHub Actions Workflow

The workflow file is located at `.github/workflows/firebase-distribution.yml`.

**Triggers:**
- `push` to `main` branch
- `workflow_dispatch` (manual trigger)
- `release` published

**Steps:**
1. **Checkout**: Clones your repository
2. **Setup Java**: Installs JDK 17 with Gradle caching
3. **Grant permissions**: Makes gradlew executable
4. **Decode keystore**: Uses GitHub secrets to recreate keystore
5. **Run tests**: Executes unit tests (`./gradlew test`)
6. **Build APK**: Creates signed release APK (`./gradlew assembleRelease`)
7. **Upload to Firebase**: Distributes APK to testers
8. **Save artifact**: Stores APK for 30 days in GitHub

**Required GitHub Secrets:**
- `KEYSTORE_FILE` - Base64-encoded keystore
- `KEYSTORE_PASSWORD` - Keystore password
- `KEY_ALIAS` - Key alias (usually `nutshell`)
- `KEY_PASSWORD` - Key password
- `FIREBASE_APP_ID` - Firebase App ID
- `FIREBASE_SERVICE_ACCOUNT_JSON` - Service account credentials

### Build Variants

The app has two build variants:

| Variant | Package ID | Signing | Use Case |
|---------|-----------|---------|----------|
| Debug | `com.nutshell.debug` | Debug keystore | Local development |
| Release | `com.nutshell` | Release keystore | Production/Firebase |

## Versioning

### Current Versioning

Defined in `app/build.gradle.kts`:

```kotlin
versionCode = 1
versionName = "1.0.0"
```

### Updating Version

Before releasing a new version:

1. Update `versionCode` (increment by 1)
2. Update `versionName` (semantic versioning)

```kotlin
versionCode = 2
versionName = "1.1.0"
```

3. Commit and push:

```bash
git add app/build.gradle.kts
git commit -m "chore: bump version to 1.1.0"
git push origin main
```

### Future: Auto-increment Version

You can automate version incrementing in the workflow by modifying `build.gradle.kts`:

```kotlin
versionCode = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1
```

## Troubleshooting

### Build Fails: "Tests failed"

**Solution**: Fix the failing tests first
```bash
./gradlew test --info
```

Check the test output for specific failures.

### Build Fails: "Keystore not found"

**Causes:**
- GitHub secret `KEYSTORE_FILE` is not set
- Keystore is not properly base64-encoded
- Secret name is misspelled

**Solution**:
1. Verify secret exists in GitHub Settings → Secrets
2. Re-encode and update the secret

### Firebase Upload Fails: "App ID not found"

**Causes:**
- `FIREBASE_APP_ID` secret is incorrect
- App not registered in Firebase

**Solution**:
1. Get correct App ID from Firebase Console → Project Settings
2. Update GitHub secret

### APK Not Signed Properly

**Symptoms**: Can't install APK or "App not signed" error

**Solution**:
1. Verify all signing secrets are correct
2. Check that `KEY_ALIAS` matches your keystore alias
3. Rebuild APK

### Testers Not Receiving Builds

**Causes:**
- Testers not added to "testers" group in Firebase
- Testers haven't accepted Firebase invitation

**Solution**:
1. Go to Firebase Console → App Distribution → Testers & Groups
2. Verify testers are in the "testers" group
3. Testers must accept invitation email

## Best Practices

### Before Pushing to Main

1. ✅ Run tests locally: `./gradlew test`
2. ✅ Build debug APK and test: `./gradlew assembleDebug`
3. ✅ Verify changes work as expected
4. ✅ Write meaningful commit messages

### Security

1. 🔒 Never commit keystore files
2. 🔒 Never commit credentials in gradle.properties
3. 🔒 Rotate service account keys periodically
4. 🔒 Use strong passwords for keystores
5. 🔒 Backup keystore in secure location (password manager, encrypted drive)

### Release Checklist

Before releasing a new version:

- [ ] Update `versionCode` and `versionName`
- [ ] Test all major features
- [ ] Run full test suite: `./gradlew check`
- [ ] Update changelog/release notes
- [ ] Create git tag
- [ ] Monitor Firebase distribution
- [ ] Verify testers receive build

## Future: Play Store Publishing

Once ready to publish to Google Play Store:

1. **Create Play Console Account** ($25 one-time fee)
2. **Create App Listing** in Play Console
3. **Add Play Store Service Account** with publishing permissions
4. **Update Workflow** to build AAB (Android App Bundle):
   ```bash
   ./gradlew bundleRelease
   ```
5. **Add Play Publishing Action** to workflow
6. **Configure Release Track** (internal → alpha → beta → production)

The current workflow is designed to easily extend for Play Store publishing - just add the Play Publishing step after Firebase distribution.

## Monitoring & Analytics

### Build Status

Monitor builds at: `https://github.com/YOUR_USERNAME/YOUR_REPO/actions`

### Firebase Distribution

View releases at: Firebase Console → App Distribution → Releases

### Download Metrics

Track downloads and tester engagement in Firebase Console.

## Support

For issues or questions:

1. Check [Troubleshooting](#troubleshooting) section
2. Review GitHub Actions logs
3. Check Firebase Console for distribution errors
4. Verify all secrets are configured correctly

## Additional Resources

- [Firebase App Distribution Setup](./FIREBASE_SETUP.md)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Firebase App Distribution Docs](https://firebase.google.com/docs/app-distribution)
- [Android Signing Docs](https://developer.android.com/studio/publish/app-signing)
