# Java Linting Setup for SootUp Project

This project has been configured with comprehensive Java linting tools to ensure code quality, style consistency, and bug detection.

## Linters Included

### 1. **Checkstyle** - Code Style Enforcement
- **Purpose**: Enforces coding standards and style guidelines
- **Configuration**: `checkstyle.xml`
- **Suppressions**: `checkstyle-suppressions.xml`

### 2. **SpotBugs** - Bug Detection
- **Purpose**: Static analysis to find potential bugs and vulnerabilities
- **Include Filter**: `spotbugs-include.xml`
- **Exclude Filter**: `spotbugs-exclude.xml`

### 3. **PMD** - Code Quality Analysis
- **Purpose**: Finds common programming flaws like unused variables, empty catch blocks, unnecessary object creation
- **Reports**: Generates detailed XML reports in `target/pmd.xml`

## Running Linters

### Individual Linter Commands

```bash
# Set Java 17 environment (if using SDKMAN)
source ~/.sdkman/bin/sdkman-init.sh && sdk use java 17.0.12-oracle

# Run Checkstyle only
mvn checkstyle:check

# Run SpotBugs only
mvn spotbugs:check

# Run PMD only
mvn pmd:check
```

### Run All Linters Together

```bash
# Full build with all linters
mvn clean compile

# Run all static analysis tools
mvn clean compile spotbugs:check pmd:check
```

### Generate Reports (without failing build)

```bash
# Generate reports for review
mvn site
mvn spotbugs:spotbugs pmd:pmd checkstyle:checkstyle
```

## Configuration Files

### Checkstyle Configuration (`checkstyle.xml`)
- Line length limit: 120 characters
- Enforces naming conventions
- Requires braces for all control structures
- Checks for unused imports
- Enforces modifier order
- And many more style rules

### SpotBugs Filters
- **Include Filter** (`spotbugs-include.xml`): Specifies which packages to analyze
- **Exclude Filter** (`spotbugs-exclude.xml`): Suppresses false positives and unimportant warnings

### PMD Configuration
- Target JDK: 17
- Minimum tokens for code duplication: 100
- Excludes generated code

## Current Status

### Latest Run Results:
- ✅ **Checkstyle**: 40+ warnings found (non-blocking)
- ✅ **SpotBugs**: No bugs found
- ❌ **PMD**: 6 violations found
  - 3 unused import violations
  - 3 useless overriding method violations

## Fixing Common Issues

### Unused Imports
Remove these imports from your files:
- `Driver.java`: Remove lines 7 and 10
- `Graph.java`: Remove line 22

### Useless Overriding Methods
In edge classes (`CFGEdge`, `ControlDependencyEdge`, `DataDependencyEdge`), remove `hashCode()` methods that only call `super.hashCode()`.

### Checkstyle Issues
Common fixes needed:
- Add `final` to method parameters
- Use braces `{}` for single-line if statements
- Fix else-if formatting (put `}` on same line as `else`)

## CI/CD Integration

The linters are configured to run automatically during:
- `mvn compile` (Checkstyle runs in validate phase)
- `mvn test` (if tests are run)
- Manual execution of specific goals

## Configuration Customization

### Making Linters Fail the Build
To make linters fail the build on violations, change in `pom.xml`:

```xml
<!-- Checkstyle -->
<failsOnError>true</failsOnError>

<!-- SpotBugs -->
<failOnError>true</failOnError>
```

### Adjusting Rules
- **Checkstyle**: Modify `checkstyle.xml`
- **SpotBugs**: Update filter files
- **PMD**: Add exclusions in `pom.xml` configuration

## IDE Integration

### IntelliJ IDEA
1. Install Checkstyle-IDEA plugin
2. Configure to use project's `checkstyle.xml`
3. Install PMD plugin
4. Install SpotBugs plugin

### VS Code
1. Install "Checkstyle for Java" extension
2. Install "PMD" extension
3. Configure workspace settings to use project configurations

## Reports Location

After running linters, reports are generated in:
- `target/checkstyle-result.xml`
- `target/spotbugsXml.xml`
- `target/pmd.xml`
- `target/site/` (for HTML reports when running `mvn site`)

## Continuous Integration

Add to your CI pipeline:

```yaml
# Example GitHub Actions step
- name: Run Linters
  run: |
    source ~/.sdkman/bin/sdkman-init.sh
    sdk use java 17.0.12-oracle
    mvn clean compile spotbugs:check pmd:check
``` 