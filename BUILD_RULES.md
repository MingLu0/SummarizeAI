# Build Rules

## Mandatory Build Requirements

### 1. Successful Build Before Commits
**RULE: The app MUST build successfully before any commits to GitHub.**

#### Pre-commit Checklist:
- [ ] Run `./gradlew assembleDebug` and verify BUILD SUCCESSFUL
- [ ] Run `./gradlew assembleRelease` and verify BUILD SUCCESSFUL (if release build is needed)
- [ ] Fix all compilation errors before committing
- [ ] Fix all lint warnings that are blocking the build
- [ ] Ensure all tests pass: `./gradlew test`

#### Build Verification Commands:
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Check lint issues
./gradlew lint
```

### 2. Build Failure Protocol
If a build fails:

1. **STOP** - Do not commit the failing code
2. **IDENTIFY** - Review build output for specific errors
3. **FIX** - Address all compilation and type mismatch errors
4. **VERIFY** - Re-run build commands until successful
5. **COMMIT** - Only commit after successful build

### 3. Common Build Issues to Watch For:
- Unresolved references (missing imports or functions)
- Type mismatches (Flow<Unit> vs Flow<List<T>>, etc.)
- Missing dependency injections
- Incorrect method signatures
- Missing mapper functions
- Database entity mapping issues

### 4. Build Status Monitoring:
- Always check the "Build Output" tab in Android Studio
- Monitor for compilation errors in real-time
- Use the terminal to run gradle commands for verification
- Keep build times reasonable (< 2 minutes for debug builds)

## Implementation Notes:
This rule ensures code quality and prevents broken code from entering the repository. All team members must follow this protocol to maintain a stable codebase.
