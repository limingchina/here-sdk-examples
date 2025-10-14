# HERE SDK Credentials Setup

## Overview
This project uses environment-based credential injection to avoid hard-coding sensitive HERE SDK credentials in source code.

## Setup Instructions

### 1. Configure Credentials in local.properties

Add your HERE SDK credentials to the `local.properties` file in the project root:

```properties
# HERE SDK Credentials
here.accessKeyId=YOUR_ACCESS_KEY_ID
here.accessKeySecret=YOUR_ACCESS_KEY_SECRET
```

**Important:** The `local.properties` file is already gitignored and will NOT be committed to version control.

### 2. How It Works

1. **Build Configuration**: The `app/build.gradle` file reads credentials from `local.properties` during build time
2. **BuildConfig Generation**: Credentials are injected as `BuildConfig` fields
3. **Runtime Usage**: The app accesses credentials via `BuildConfig.HERE_ACCESS_KEY_ID` and `BuildConfig.HERE_ACCESS_KEY_SECRET`

### 3. Build the Project

After adding your credentials, build the project:

```bash
./gradlew clean build
```

### 4. Verify Setup

The credentials will be automatically injected when the app initializes the HERE SDK in `MainActivity.initializeHERESDK()`.

## Security Notes

- ✅ Credentials are stored in `local.properties` (gitignored)
- ✅ No hard-coded credentials in source code
- ✅ Each developer can use their own credentials
- ⚠️ BuildConfig fields are accessible in the compiled APK - for production apps, consider using a secure backend service

## Alternative Approaches

For production apps, consider:
- Using a backend service to fetch credentials
- Implementing Android Keystore for secure storage
- Using environment variables in CI/CD pipelines

