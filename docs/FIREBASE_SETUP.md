# Firebase App Distribution Setup Guide

This guide walks you through setting up Firebase App Distribution for the Nutshell app.

## Prerequisites

- Google account
- Access to your GitHub repository settings
- Generated release keystore (see DEPLOYMENT.md)

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" or "Create a project"
3. Enter project name: **Nutshell** (or your preferred name)
4. Disable Google Analytics (optional, not needed for App Distribution)
5. Click "Create project"

## Step 2: Add Android App to Firebase

1. In your Firebase project, click the Android icon to add an Android app
2. Fill in the details:
   - **Android package name**: `com.nutshell` (must match exactly)
   - **App nickname**: Nutshell (optional, for your reference)
   - **Debug signing certificate SHA-1**: Leave blank for now (optional)
3. Click "Register app"
4. **Download `google-services.json`**
   - Click "Download google-services.json"
   - Save this file - you'll add it to your project later
5. Skip the "Add Firebase SDK" step (we'll handle this differently)
6. Click "Next" and "Continue to console"

## Step 3: Enable Firebase App Distribution

1. In the Firebase Console left sidebar, click **Release & Monitor** → **App Distribution**
2. Click "Get started" if this is your first time
3. You should see your Android app listed

## Step 4: Create Tester Group

1. Still in App Distribution, click the **"Testers & Groups"** tab
2. Click **"Add Group"**
3. Enter group name: `testers`
4. Add tester email addresses (people who will receive the app)
5. Click "Save"

> **Important**: The group name must be `testers` to match the GitHub Actions workflow, or update the workflow file if you use a different name.

## Step 5: Create Firebase Service Account

This allows GitHub Actions to upload builds automatically.

### 5a. Enable Firebase App Distribution API

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select your Firebase project from the dropdown at the top
3. Go to **APIs & Services** → **Library**
4. Search for "Firebase App Distribution API"
5. Click on it and click **"Enable"**

### 5b. Create Service Account

1. In Google Cloud Console, go to **IAM & Admin** → **Service Accounts**
2. Click **"Create Service Account"**
3. Fill in the details:
   - **Service account name**: `github-actions-firebase`
   - **Service account ID**: `github-actions-firebase` (auto-filled)
   - **Description**: "Service account for GitHub Actions to upload to Firebase App Distribution"
4. Click "Create and Continue"

### 5c. Grant Permissions

1. In the "Grant this service account access to project" section:
   - Click "Select a role"
   - Search for and select: **Firebase App Distribution Admin**
   - Click "Continue"
2. Skip the "Grant users access" section
3. Click "Done"

### 5d. Generate JSON Key

1. Find your newly created service account in the list
2. Click the three dots (⋮) on the right → **"Manage keys"**
3. Click **"Add Key"** → **"Create new key"**
4. Select **JSON** format
5. Click "Create"
6. A JSON file will download automatically - **save this securely!**

## Step 6: Get Your Firebase App ID

1. Go back to Firebase Console → Project Settings (gear icon)
2. Scroll down to "Your apps" section
3. Find your Android app
4. Copy the **App ID** (format: `1:123456789:android:abcdef123456`)

## Step 7: Configure GitHub Secrets

Now you need to add secrets to your GitHub repository:

1. Go to your GitHub repository
2. Click **Settings** → **Secrets and variables** → **Actions**
3. Click **"New repository secret"** and add each of these:

### Keystore Secrets (from your keystore generation)

| Secret Name | Value | How to Get |
|-------------|-------|------------|
| `KEYSTORE_FILE` | Base64-encoded keystore | Run: `base64 -i /path/to/nutshell-release.jks \| pbcopy` (macOS) or `base64 /path/to/nutshell-release.jks` (Linux) |
| `KEYSTORE_PASSWORD` | Your keystore password | From when you created the keystore |
| `KEY_ALIAS` | Your key alias | Usually `nutshell` |
| `KEY_PASSWORD` | Your key password | From when you created the keystore |

### Firebase Secrets

| Secret Name | Value | How to Get |
|-------------|-------|------------|
| `FIREBASE_APP_ID` | Your Firebase App ID | From Step 6 above (format: `1:123456789:android:abcdef123456`) |
| `FIREBASE_SERVICE_ACCOUNT_JSON` | Service account JSON content | Open the JSON file from Step 5d, copy the entire contents |

> **Tip**: For `FIREBASE_SERVICE_ACCOUNT_JSON`, open the downloaded JSON file in a text editor, select all, and paste the entire JSON content as the secret value.

## Step 8: Add google-services.json to Your Project

1. Copy the `google-services.json` file you downloaded in Step 2
2. Place it in: `app/google-services.json` (inside the `app/` directory)
3. **Important**: Make sure this file is tracked by git for the workflow to work

To check if it's tracked:
```bash
git add app/google-services.json
git commit -m "Add Firebase configuration"
```

## Step 9: Add Firebase Plugin to Gradle

Update your `app/build.gradle.kts`:

1. At the top of the file, add to the plugins block:
```kotlin
plugins {
    // ... existing plugins
    id("com.google.gms.google-services") version "4.4.0" apply false
}
```

2. Update your project-level `build.gradle.kts`:
```kotlin
plugins {
    id("com.google.gms.google-services") version "4.4.0" apply false
}
```

## Step 10: Test the Setup

1. Commit and push your changes to the `main` branch:
```bash
git add .
git commit -m "Add Firebase App Distribution setup"
git push origin main
```

2. Go to your GitHub repository → **Actions** tab
3. You should see the "Firebase App Distribution" workflow running
4. Wait for it to complete (usually 5-10 minutes)

5. Check Firebase Console → App Distribution → Releases
6. You should see your first release!

## Troubleshooting

### Workflow fails with "Permission denied"

- Check that all GitHub Secrets are set correctly
- Verify the service account has "Firebase App Distribution Admin" role

### "App ID not found"

- Double-check the `FIREBASE_APP_ID` secret
- Ensure the App ID format is correct: `1:123456789:android:abcdef123456`

### "Invalid keystore"

- Ensure `KEYSTORE_FILE` is properly base64-encoded
- Verify `KEYSTORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD` match your keystore

### Testers don't receive notification

- Check that testers are added to the "testers" group in Firebase Console
- Testers need to accept the invitation email from Firebase

## Next Steps

- See [DEPLOYMENT.md](./DEPLOYMENT.md) for how to use the workflow
- Configure release notes and changelog automation
- Set up Play Store publishing (coming soon)

## Security Best Practices

✅ **DO:**
- Store keystores and credentials only in GitHub Secrets
- Use unique, strong passwords for keystores
- Backup your keystore file securely (you can't recover it if lost)
- Rotate service account keys periodically

❌ **DON'T:**
- Commit keystores to git (they're excluded in `.gitignore`)
- Share keystore passwords in plain text
- Give service accounts more permissions than needed
- Share service account JSON files publicly
