# GitHub Secrets Setup Guide

This guide walks you through configuring GitHub Secrets for automated builds and Firebase App Distribution.

## Prerequisites

- GitHub repository with admin access
- Release keystore generated (see [KEYSTORE_GENERATION.md](./KEYSTORE_GENERATION.md))
- Firebase project configured (see [FIREBASE_SETUP.md](./FIREBASE_SETUP.md))

## Access GitHub Secrets

1. Go to your GitHub repository: `https://github.com/YOUR_USERNAME/SummaerizerAIApp`
2. Click **Settings** (top navigation)
3. In the left sidebar, click **Secrets and variables** → **Actions**
4. You'll see the "Repository secrets" section

## Required Secrets Checklist

Use this checklist to ensure all secrets are configured correctly.

### Keystore Secrets (4 required)

#### ☐ KEYSTORE_FILE

**Description**: Base64-encoded release keystore file

**How to get the value**:

```bash
# macOS
base64 -i nutshell-release.jks | pbcopy

# Linux
base64 nutshell-release.jks | xclip -selection clipboard

# Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("nutshell-release.jks")) | Set-Clipboard
```

**Steps**:
1. Navigate to where you saved `nutshell-release.jks`
2. Run the appropriate command above (the base64 string will be copied to clipboard)
3. In GitHub, click **New repository secret**
4. Name: `KEYSTORE_FILE`
5. Secret: Paste the base64 string (will be very long, ~2000+ characters)
6. Click **Add secret**

---

#### ☐ KEYSTORE_PASSWORD

**Description**: Password for the keystore file

**How to get the value**: This is the password you entered when creating the keystore

**Steps**:
1. Click **New repository secret**
2. Name: `KEYSTORE_PASSWORD`
3. Secret: Enter your keystore password
4. Click **Add secret**

---

#### ☐ KEY_ALIAS

**Description**: Alias for the signing key

**How to get the value**: If you followed the guide, this is `nutshell`

**Steps**:
1. Click **New repository secret**
2. Name: `KEY_ALIAS`
3. Secret: `nutshell` (or whatever alias you used)
4. Click **Add secret**

---

#### ☐ KEY_PASSWORD

**Description**: Password for the signing key

**How to get the value**: This is usually the same as your keystore password (unless you set a different one)

**Steps**:
1. Click **New repository secret**
2. Name: `KEY_PASSWORD`
3. Secret: Enter your key password
4. Click **Add secret**

---

### Firebase Secrets (2 required)

#### ☐ FIREBASE_APP_ID

**Description**: Firebase App ID for Android app

**How to get the value**:
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Click the gear icon (⚙️) → **Project settings**
4. Scroll to "Your apps" section
5. Find your Android app
6. Copy the **App ID** (format: `1:123456789:android:abcdef123456`)

**Steps**:
1. Click **New repository secret**
2. Name: `FIREBASE_APP_ID`
3. Secret: Paste your Firebase App ID
4. Click **Add secret**

---

#### ☐ FIREBASE_SERVICE_ACCOUNT_JSON

**Description**: Service account credentials for Firebase App Distribution

**How to get the value**:
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select your Firebase project
3. Go to **IAM & Admin** → **Service Accounts**
4. Find the service account you created (e.g., `github-actions-firebase`)
5. Click the three dots (⋮) → **Manage keys**
6. Click **Add Key** → **Create new key**
7. Select **JSON** format
8. Click **Create** (a JSON file will download)
9. Open the JSON file in a text editor
10. Copy the **entire contents** (all the JSON)

**Steps**:
1. Click **New repository secret**
2. Name: `FIREBASE_SERVICE_ACCOUNT_JSON`
3. Secret: Paste the entire JSON content (should start with `{` and end with `}`)
4. Click **Add secret**

---

## Verify Secrets

After adding all secrets, you should see 6 repository secrets:

1. ✅ KEYSTORE_FILE
2. ✅ KEYSTORE_PASSWORD
3. ✅ KEY_ALIAS
4. ✅ KEY_PASSWORD
5. ✅ FIREBASE_APP_ID
6. ✅ FIREBASE_SERVICE_ACCOUNT_JSON

**Note**: You cannot view secret values after saving them. If you need to check or update a secret, click on it and choose "Update secret".

## Security Best Practices

✅ **DO:**
- Keep your keystore file and credentials in a secure password manager
- Backup your keystore file to secure cloud storage
- Use strong, unique passwords
- Rotate service account keys periodically (every 90 days recommended)
- Review who has access to your repository

❌ **DON'T:**
- Commit keystores or credentials to git
- Share secrets via email or Slack
- Use the same password for multiple keystores
- Give service accounts more permissions than needed
- Share your repository with untrusted users

## Troubleshooting

### Secret value is cut off when pasting

Make sure you copied the entire value. For `KEYSTORE_FILE`, the base64 string should be very long (2000+ characters).

### Workflow fails with "Invalid keystore"

- Verify `KEYSTORE_FILE` is properly base64-encoded
- Check that `KEYSTORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD` match exactly what you used when creating the keystore
- Ensure there are no extra spaces or newlines in the secret values

### Workflow fails with "Firebase App Distribution API not enabled"

Go to [Google Cloud Console](https://console.cloud.google.com/) → APIs & Services → Library → Search "Firebase App Distribution API" → Enable

### Workflow fails with "Permission denied" for Firebase

- Verify the service account has "Firebase App Distribution Admin" role
- Check that `FIREBASE_SERVICE_ACCOUNT_JSON` contains the complete JSON (starts with `{` and ends with `}`)
- Ensure you copied the JSON from the correct service account

## Next Steps

Once all secrets are configured:
1. Commit and push your code changes
2. Update `versionName` in `app/build.gradle.kts` to trigger a release
3. Check GitHub Actions to see the workflow run
4. Verify the build appears in Firebase App Distribution

See [DEPLOYMENT.md](./DEPLOYMENT.md) for usage instructions.

