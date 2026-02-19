# Branch Protection Checklist

## For `staging`
- Require a pull request before merging: ON
- Require approvals: 1
- Dismiss stale pull request approvals when new commits are pushed: ON
- Require status checks to pass before merging: ON
- Required checks:
  - `build-test-lint-sonar`
- Require branches to be up to date before merging: ON

## For `main`
- Require a pull request before merging: ON
- Require approvals: 1
- Dismiss stale pull request approvals when new commits are pushed: ON
- Require status checks to pass before merging: ON
- Required checks:
  - `build-test-lint-sonar`
- Require branches to be up to date before merging: ON
