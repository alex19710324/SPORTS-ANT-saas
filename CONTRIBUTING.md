# Contributing to SPORTS ANT SaaS

First off, thanks for taking the time to contribute! 🎉

The following is a set of guidelines for contributing to SPORTS ANT SaaS. These are mostly guidelines, not rules. Use your best judgment, and feel free to propose changes to this document in a pull request.

## Branching Strategy

We follow a simplified **Git Flow** model:

*   **`main`**: Production-ready code. Do not push directly to `main`. Deployments to production happen from here.
*   **`develop`**: The main development branch. All features should be merged into `develop` first. This branch is automatically deployed to the **Test Environment**.
*   **`feature/*`**: Create a new branch for each feature or improvement (e.g., `feature/user-auth`, `feature/hq-dashboard`). Branch off from `develop`.
*   **`hotfix/*`**: For critical bug fixes on production (e.g., `hotfix/login-error`). Branch off from `main`.
*   **`release/*`**: Preparation for a new production release (e.g., `release/v1.0.0`). Branch off from `develop`.

## Commit Messages

We follow the **Conventional Commits** specification. This leads to more readable messages that are easy to follow when looking through the project history.

### Format
`<type>(<scope>): <subject>`

### Types
*   **feat**: A new feature
*   **fix**: A bug fix
*   **docs**: Documentation only changes
*   **style**: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc)
*   **refactor**: A code change that neither fixes a bug nor adds a feature
*   **perf**: A code change that improves performance
*   **test**: Adding missing tests or correcting existing tests
*   **chore**: Changes to the build process or auxiliary tools and libraries such as documentation generation

### Example
`feat(auth): add jwt token validation logic`

## Code Style

### Backend (Java/Spring Boot)
*   Follow standard Java naming conventions (CamelCase for variables/methods, PascalCase for classes).
*   Use 4 spaces for indentation.
*   Ensure all new code is covered by Unit Tests (`mvn test`).

### Frontend (Vue 3/TypeScript)
*   Use **ESLint** and **Prettier** for code formatting.
*   Run `npm run lint` before committing.
*   Component names should be multi-word (e.g., `MembershipCard.vue`).
*   Use Composition API with `<script setup lang="ts">`.

## Pull Request Process

1.  Ensure any install or build dependencies are removed before the end of the layer when doing a build.
2.  Update the README.md with details of changes to the interface, this includes new environment variables, exposed ports, useful file locations and container parameters.
3.  Increase the version numbers in any examples files and the README.md to the new version that this Pull Request would represent.
4.  You may merge the Pull Request in once you have the sign-off of two other developers, or if you do not have permission to do that, you may request the second reviewer to merge it for you.

## Development Setup

Please refer to the [README.md](./README.md) for instructions on how to set up your local development environment.
