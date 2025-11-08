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

The Nutshell app uses a fully automated CI/CD pipeline based on version tags:

```
Update versionName → Auto-create Tag → Build & Sign → Firebase App Distribution → Testers Notified
```

**Key Components:**
- **Auto-Tagging**: Automatically creates version tags when `versionName` changes
- **GitHub Actions**: Automates build, signing, and deployment
- **Firebase App Distribution**: Distributes release APK to beta testers
- **Release Signing**: Signs APK with production keystore
- **Play Store Ready**: Infrastructure supports future Play Store publishing (AAB builds)

## Prerequisites

Before you can deploy, you need:

1. ✅ **Release Keystore Generated** (see [KEYSTORE_GENERATION.md](./KEYSTORE_GENERATION.md))
2. ✅ **Firebase Project Set Up** (see [FIREBASE_SETUP.md](./FIREBASE_SETUP.md))
3. ✅ **GitHub Secrets Configured** (see [GITHUB_SECRETS_SETUP.md](./GITHUB_SECRETS_SETUP.md))

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

To trigger a new release, simply update the version in `app/build.gradle.kts`:

1. **Update the version**:

```kotlin
versionCode = 2  // Increment by 1
versionName = "1.0.1"  // Update version number
```

2. **Commit and push to main**:

```bash
git add app/build.gradle.kts
git commit -m "chore: bump version to 1.0.1"
git push origin main
```

**What happens automatically:**

1. **Auto-Tag Workflow** triggers on `app/build.gradle.kts` change
2. Extracts new `versionName` from the file
3. Checks if tag `v1.0.1` already exists
4. Creates and pushes new tag `v1.0.1` if it doesn't exist
5. **Firebase Distribution Workflow** triggers on new tag
6. Builds both APK (for Firebase) and AAB (for future Play Store)
7. Signs release builds with production keystore
8. Uploads APK to Firebase App Distribution
9. Notifies testers in the "testers" group
10. Creates GitHub Release with APK attached

**Monitoring the workflows:**
1. Go to your GitHub repository
2. Click the **Actions** tab
3. You'll see two workflows run:
   - "Auto Tag Version" (creates the tag)
   - "Firebase App Distribution" (builds and distributes)
4. Watch the progress in real-time

### Manual Deployment

You can also trigger deployment manually without changing the version:

1. Go to **Actions** tab in GitHub
2. Click **Firebase App Distribution** workflow
3. Click **Run workflow**
4. Enter version number (e.g., "1.0.1")
5. Click **Run workflow**

This bypasses the auto-tagging system and builds the specified version immediately.

### Manual Tag Creation

If you prefer to create tags manually instead of using auto-tagging:

```bash
# Update version in app/build.gradle.kts first
# Then create and push tag manually
git tag -a v1.0.1 -m "Release version 1.0.1"
git push origin v1.0.1
```

This will trigger the Firebase Distribution workflow directly.

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

### Workflow Architecture

The deployment system consists of two GitHub Actions workflows:

#### 1. Auto Tag Version (`.github/workflows/auto-tag-version.yml`)

**Triggers:**
- Push to `main` branch that modifies `app/build.gradle.kts`

**Steps:**
1. Extracts `versionName` and `versionCode` from `build.gradle.kts`
2. Checks if tag `v{versionName}` already exists
3. If tag doesn't exist, creates annotated tag with version info
4. Pushes tag to repository (triggers Firebase Distribution workflow)
5. If tag exists, skips and logs message

#### 2. Firebase App Distribution (`.github/workflows/firebase-distribution.yml`)

**Triggers:**
- Push of tags matching `v*` pattern (e.g., `v1.0.0`, `v1.0.1`)
- Manual workflow dispatch

**Jobs:**

**Build Job:**
1. **Checkout**: Clones your repository
2. **Extract version**: Gets version from tag or input
3. **Setup Java**: Installs JDK 17 with Gradle caching
4. **Grant permissions**: Makes gradlew executable
5. **Decode keystore**: Uses GitHub secrets to recreate keystore
6. **Build Release APK**: Creates signed APK (`./gradlew assembleRelease`)
7. **Build Release AAB**: Creates AAB bundle (`./gradlew bundleRelease`)
8. **Upload artifacts**: Stores APK, AAB, and mapping file

**Distribute Firebase Job:**
1. **Download APK**: Gets APK from build job
2. **Generate release notes**: Creates formatted release notes with version info
3. **Upload to Firebase**: Distributes APK to "testers" group
4. **Create GitHub Release**: Attaches APK to GitHub release

**Play Store Job (Disabled):**
- Infrastructure in place for future Play Store deployment
- Uncomment and configure when ready to publish to Play Store

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

To release a new version, update both values in `app/build.gradle.kts`:

1. **Increment `versionCode`** (must be higher than previous)
2. **Update `versionName`** (follow semantic versioning)

```kotlin
versionCode = 2      // Was 1, now 2
versionName = "1.1.0"  // Was "1.0.0", now "1.1.0"
```

**Semantic Versioning Guide:**
- **Major (X.0.0)**: Breaking changes, major new features
- **Minor (1.X.0)**: New features, backward compatible
- **Patch (1.0.X)**: Bug fixes, minor improvements

3. **Commit and push**:

```bash
git add app/build.gradle.kts
git commit -m "chore: bump version to 1.1.0"
git push origin main
```

The auto-tagging workflow will create tag `v1.1.0` and trigger deployment automatically.

### Version Management Tips

**For regular releases:**
- Always increment both `versionCode` and `versionName` together
- Use meaningful version numbers
- Follow semantic versioning

**For hotfixes:**
- Increment patch version (e.g., 1.0.0 → 1.0.1)
- Keep the change focused on the fix

**For major releases:**
- Update major version (e.g., 1.9.0 → 2.0.0)
- Plan breaking changes carefully
- Communicate changes to testers

## Troubleshooting

### Tag Already Exists

**Symptom**: Auto-tag workflow logs "Tag vX.X.X already exists"

**Cause**: You pushed a version that was already released

**Solution**: 
```bash
# Update version to a new number
versionCode = 3
versionName = "1.0.2"  # Must be different from existing tags
```

### Workflow Doesn't Trigger

**Symptom**: Pushed version change but no workflow runs

**Cause**: File path or branch doesn't match trigger conditions

**Solution**:
- Verify you pushed to `main` branch (not `master` or feature branch)
- Ensure you modified `app/build.gradle.kts` (exact path matters)
- Check GitHub Actions is enabled for your repository

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

- [ ] Test all major features locally
- [ ] Run full test suite: `./gradlew check`
- [ ] Update `versionCode` (increment by 1)
- [ ] Update `versionName` (semantic versioning)
- [ ] Commit with clear message: `chore: bump version to X.X.X`
- [ ] Push to main branch
- [ ] Monitor GitHub Actions for both workflows
- [ ] Verify tag was created automatically
- [ ] Check Firebase Console for new release
- [ ] Verify testers receive notification
- [ ] Test distributed APK on real device

## Future: Play Store Publishing

The infrastructure is ready for Play Store publishing. When you're ready:

### Prerequisites

1. **Create Play Console Account** ($25 one-time fee)
2. **Create App Listing** in Play Console
3. **Complete Store Listing** (screenshots, description, etc.)
4. **Generate Service Account** for automated publishing

### Enable Play Store Deployment

The workflow already builds AAB files - you just need to enable the Play Store job:

1. **Edit `.github/workflows/firebase-distribution.yml`**:

```yaml
# Change this:
# deploy-play-store:
#   if: false  # Disabled for now

# To this:
deploy-play-store:
  if: true  # Enabled
```

2. **Add GitHub Secret**:
- `PLAY_STORE_SERVICE_ACCOUNT_JSON` - Service account with Play Console API access

3. **Configure release track** in the workflow:
   - `internal` - Internal testing (fast, no review)
   - `alpha` - Closed alpha (fast, minimal review)
   - `beta` - Open beta (full review)
   - `production` - Public release (full review)

4. **Push new version** and the workflow will:
   - Build signed AAB
   - Upload to Firebase (testers)
   - Upload to Play Store (selected track)

### Play Store Service Account Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select your project
3. **IAM & Admin** → **Service Accounts** → **Create Service Account**
4. Name it `play-store-publisher`
5. Grant role: **Service Account User**
6. Generate JSON key
7. Go to [Play Console](https://play.google.com/console)
8. **Setup** → **API access**
9. Link the service account
10. Grant permissions: **Release to production, excluding Pricing & Distribution**

### Recommended Release Strategy

1. **Phase 1**: Firebase App Distribution only (current setup)
   - Test with closed group
   - Gather feedback
   - Iterate quickly

2. **Phase 2**: Internal track on Play Store
   - Verify Play Store integration works
   - Test with internal team

3. **Phase 3**: Alpha/Beta track
   - Expand tester base
   - Get more diverse feedback
   - Ensure stability

4. **Phase 4**: Production release
   - Polish based on beta feedback
   - Prepare marketing materials
   - Launch to public

The current workflow supports all phases - just enable the Play Store job when ready!

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

## Quick Reference

### Release a New Version

```bash
# 1. Update version in app/build.gradle.kts
versionCode = 2
versionName = "1.0.1"

# 2. Commit and push
git add app/build.gradle.kts
git commit -m "chore: bump version to 1.0.1"
git push origin main

# 3. Wait for workflows to complete
# Auto-tag workflow creates v1.0.1 tag
# Firebase workflow builds and distributes

# 4. Verify in Firebase Console
# Check App Distribution → Releases
```

### Local Testing

```bash
# Run tests
./gradlew test

# Build debug APK
./gradlew assembleDebug

# Build release APK (with keystore configured)
./gradlew assembleRelease

# Full check (tests + lint)
./gradlew check
```

### Monitoring

- **GitHub Actions**: `https://github.com/YOUR_USERNAME/YOUR_REPO/actions`
- **Firebase Console**: `https://console.firebase.google.com/`
- **Releases**: Firebase → App Distribution → Releases
- **Testers**: Firebase → App Distribution → Testers & Groups

## Additional Resources

- [Keystore Generation Guide](./KEYSTORE_GENERATION.md)
- [GitHub Secrets Setup](./GITHUB_SECRETS_SETUP.md)
- [Firebase App Distribution Setup](./FIREBASE_SETUP.md)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Firebase App Distribution Docs](https://firebase.google.com/docs/app-distribution)
- [Android Signing Docs](https://developer.android.com/studio/publish/app-signing)
- [Semantic Versioning](https://semver.org/)
