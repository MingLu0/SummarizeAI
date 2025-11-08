# Generate Release Keystore

This guide walks you through creating a release keystore for signing your Android app.

## Prerequisites

- Java Development Kit (JDK) installed
- Terminal/Command line access

## Generate Keystore

Run the following command in your terminal:

```bash
keytool -genkey -v -keystore nutshell-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias nutshell
```

### Command Breakdown

- `-genkey`: Generate a new key pair
- `-v`: Verbose output
- `-keystore nutshell-release.jks`: Name of the keystore file
- `-keyalg RSA`: Algorithm (RSA)
- `-keysize 2048`: Key size in bits
- `-validity 10000`: Valid for 10,000 days (~27 years)
- `-alias nutshell`: Alias to identify this key

## Fill in the Information

You'll be prompted for information. Here's what to enter:

1. **Enter keystore password**: Create a strong password (remember this!)
2. **Re-enter password**: Confirm the password
3. **First and last name**: Your name or company name
4. **Organizational unit**: Your team/department (or press Enter)
5. **Organization**: Your company name (or press Enter)
6. **City/Locality**: Your city
7. **State/Province**: Your state
8. **Two-letter country code**: e.g., US, UK, CA
9. **Confirm**: Type "yes"
10. **Key password**: Press Enter to use same password as keystore, or create a different one

## Example Output

```
Enter keystore password: ********
Re-enter new password: ********
What is your first and last name?
  [Unknown]:  John Doe
What is the name of your organizational unit?
  [Unknown]:  Development
What is the name of your organization?
  [Unknown]:  Nutshell
What is the name of your City or Locality?
  [Unknown]:  San Francisco
What is the name of your State or Province?
  [Unknown]:  California
What is the two-letter country code for this unit?
  [Unknown]:  US
Is CN=John Doe, OU=Development, O=Nutshell, L=San Francisco, ST=California, C=US correct?
  [no]:  yes

Generating 2,048 bit RSA key pair and self-signed certificate (SHA256withRSA) with a validity of 10,000 days
        for: CN=John Doe, OU=Development, O=Nutshell, L=San Francisco, ST=California, C=US
[Storing nutshell-release.jks]
```

## Store Credentials Securely

**CRITICAL**: Save these values somewhere secure (password manager recommended):

- **Keystore file location**: `/path/to/nutshell-release.jks`
- **Keystore password**: The password you entered
- **Key alias**: `nutshell`
- **Key password**: Same as keystore password (or different if you chose)

## Backup the Keystore

⚠️ **IMPORTANT**: 
- Backup `nutshell-release.jks` to a secure location (cloud storage, encrypted drive)
- If you lose this file, you **cannot** update your app on Play Store
- You'll need to publish a new app with a different package name

## Next Steps

1. **Base64 encode the keystore** for GitHub Secrets:
   ```bash
   # macOS
   base64 -i nutshell-release.jks | pbcopy
   
   # Linux
   base64 nutshell-release.jks | xclip -selection clipboard
   
   # Windows (PowerShell)
   [Convert]::ToBase64String([IO.File]::ReadAllBytes("nutshell-release.jks")) | Set-Clipboard
   ```
   The base64 string is now in your clipboard.

2. Follow the [GitHub Secrets Setup Guide](./GITHUB_SECRETS_SETUP.md) to configure CI/CD

## Verify Keystore

To verify your keystore was created correctly:

```bash
keytool -list -v -keystore nutshell-release.jks -alias nutshell
```

Enter your keystore password when prompted. You should see details about your certificate.

## Troubleshooting

### "keytool: command not found"

The JDK is not installed or not in your PATH.

- **macOS**: Install via Homebrew: `brew install openjdk@17`
- **Linux**: Install via package manager: `sudo apt install openjdk-17-jdk`
- **Windows**: Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [Adoptium](https://adoptium.net/)

### "keystore password was incorrect"

Make sure you're entering the correct password. There's no way to recover a forgotten password.

### Already have a keystore?

If you already have a keystore from a previous setup, use that one! Don't create a new keystore for an existing app on Play Store.

