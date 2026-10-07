# 🛠️ Master Git: Hands-on Practice Guide with TaskForge

This guide walks you through every fundamental and advanced Git command using this exact **TaskForge** Java repository. Each lab represents a real-world software engineering workflow.

---

## 📑 Curriculum Overview

1. [Lab 1: Git Identity & Repository Initialization](#lab-1-git-identity--repository-initialization)
2. [Lab 2: The Three Trees: Working Directory, Staging, & History](#lab-2-the-three-trees-working-directory-staging--history)
3. [Lab 3: Examining History, Diffs, and Line-by-Line Blame](#lab-3-examining-history-diffs-and-line-by-line-blame)
4. [Lab 4: Branching & Feature Workflow](#lab-4-branching--feature-workflow)
5. [Lab 5: Merging (Fast-Forward vs. 3-Way Merge)](#lab-5-merging-fast-forward-vs-3-way-merge)
6. [Lab 6: Creating & Resolving a Real Merge Conflict](#lab-6-creating--resolving-a-real-merge-conflict)
7. [Lab 7: Git Stash (Saving Work in Progress)](#lab-7-git-stash-saving-work-in-progress)
8. [Lab 8: Undoing Mistakes (Restore, Revert, Reset, Amend)](#lab-8-undoing-mistakes-restore-revert-reset-amend)
9. [Lab 9: Git Rebase & Interactive Rebasing (Squash)](#lab-9-git-rebase--interactive-rebasing-squash)
10. [Lab 10: Tagging Versions & Working with Remote (GitHub/GitLab)](#lab-10-tagging-versions--working-with-remote-githubgitlab)

---

## Lab 1: Git Identity & Repository Initialization

### 1.1 Configure your identity
Git tracks the author of every commit. Set your global name and email:
```bash
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```
Verify your settings:
```bash
git config --list
```

### 1.2 Initialize your repository
Navigate to `/home/kushal/Project` and initialize a new Git repository:
```bash
git init -b main
```
> **Explanation**: `git init -b main` creates the `.git` hidden directory and sets the default root branch name to `main`.

---

## Lab 2: The Three Trees: Working Directory, Staging, & History

Git operates across 3 areas:
1. **Working Directory:** Actual files on disk.
2. **Staging Area (Index):** Snapshot of changes queued for the next commit.
3. **Repository (Git directory / Commit History):** Permanently committed snapshots.

### 2.1 Check working directory status
```bash
git status
```
You will see that files like `pom.xml`, `.gitignore`, `README.md`, and `src/` are listed as **Untracked files**.

### 2.2 Stage files selectively
Stage only the configuration files first:
```bash
git add pom.xml .gitignore README.md
git status
```
Notice `pom.xml`, `.gitignore`, and `README.md` are now in the **Changes to be committed** (staged) section, while `src/` remains untracked.

### 2.3 Stage all remaining files
```bash
git add src/
git status
```

### 2.4 Create your first commit
```bash
git commit -m "feat: initial commit for TaskForge project management system"
```

---

## Lab 3: Examining History, Diffs, and Line-by-Line Blame

### 3.1 Inspect commit logs
```bash
git log
```
To see a concise, graph-based view:
```bash
git log --oneline --graph --decorate --all
```

### 3.2 View a specific commit's details
```bash
git show HEAD
```

### 3.3 Make a change and view the diff
Open `src/main/java/com/taskforge/model/TaskPriority.java` and notice the priorities.
Let's see what `git diff` does:
1. Make a small edit in `README.md` (e.g., add a line at the end).
2. Run:
```bash
git diff
```
3. Stage the file and check staged diff:
```bash
git add README.md
git diff --staged
```
4. Commit the change:
```bash
git commit -m "docs: enhance README documentation"
```

### 3.4 Check who modified what line (`git blame`)
```bash
git blame src/main/java/com/taskforge/model/Task.java
```
This shows line numbers, author, commit hash, and timestamp for each line.

---

## Lab 4: Branching & Feature Workflow

Branches allow you to build features in isolation without touching the production-ready `main` branch.

### 4.1 Create and switch to a feature branch
```bash
git switch -c feature/task-tags
```
*(Alternative older command: `git checkout -b feature/task-tags`)*

To list all local branches:
```bash
git branch
```

### 4.2 Make changes on the feature branch
Edit `src/main/java/com/taskforge/model/Task.java` to add a helper method:
```java
public boolean hasTag(String tag) {
    return tag != null && tags.contains(tag.trim().toLowerCase());
}
```
Check status:
```bash
git status
git diff
```

### 4.3 Commit the feature
```bash
git add src/main/java/com/taskforge/model/Task.java
git commit -m "feat(task): add hasTag helper method"
```

---

## Lab 5: Merging (Fast-Forward vs. 3-Way Merge)

### 5.1 Switch back to `main`
```bash
git switch main
```

### 5.2 Merge the feature branch
Because `main` did not change since you branched off, Git performs a **Fast-Forward** merge:
```bash
git merge feature/task-tags
```

### 5.3 Delete the merged feature branch
```bash
git branch -d feature/task-tags
```

---

## Lab 6: Creating & Resolving a Real Merge Conflict

Merge conflicts happen when two branches modify the **same line** of code differently.

### 6.1 Create Branch 1: `feature/status-archived`
```bash
git switch -c feature/status-archived
```
Open `src/main/java/com/taskforge/model/TaskStatus.java` and add `ARCHIVED("Archived")` right after `BLOCKED`:
```java
    BLOCKED("Blocked"),
    ARCHIVED("Archived");
```
Commit it:
```bash
git add src/main/java/com/taskforge/model/TaskStatus.java
git commit -m "feat(status): add ARCHIVED status"
```

### 6.2 Switch to main and create Branch 2: `feature/status-canceled`
```bash
git switch main
git switch -c feature/status-canceled
```
Open `src/main/java/com/taskforge/model/TaskStatus.java` and add `CANCELED("Canceled")` in that same spot:
```java
    BLOCKED("Blocked"),
    CANCELED("Canceled");
```
Commit it:
```bash
git add src/main/java/com/taskforge/model/TaskStatus.java
git commit -m "feat(status): add CANCELED status"
```

### 6.3 Merge Branch 1 into `main`
```bash
git switch main
git merge feature/status-archived
```
*(This succeeds cleanly!)*

### 6.4 Now merge Branch 2 into `main` (trigger the conflict!)
```bash
git merge feature/status-canceled
```
💥 **CONFLICT!** Git outputs:
```
CONFLICT (content): Merge conflict in src/main/java/com/taskforge/model/TaskStatus.java
Automatic merge failed; fix conflicts and then commit the result.
```

### 6.5 Resolve the conflict
Open `src/main/java/com/taskforge/model/TaskStatus.java`. You will see Git conflict markers:
```java
<<<<<<< HEAD
    BLOCKED("Blocked"),
    ARCHIVED("Archived");
=======
    BLOCKED("Blocked"),
    CANCELED("Canceled");
>>>>>>> feature/status-canceled
```
Decide to keep **both** statuses:
```java
    BLOCKED("Blocked"),
    ARCHIVED("Archived"),
    CANCELED("Canceled");
```

### 6.6 Finalize the merge commit
```bash
git add src/main/java/com/taskforge/model/TaskStatus.java
git commit -m "merge: resolve TaskStatus conflict by supporting both ARCHIVED and CANCELED"
```
Verify the clean status and run tests:
```bash
git status
mvn test
```
Clean up the branches:
```bash
git branch -d feature/status-archived
git branch -d feature/status-canceled
```

---

## Lab 7: Git Stash (Saving Work in Progress)

Need to switch branches urgently, but your work isn't ready to commit? Use **Stash**.

1. Start editing a file:
   In `src/main/java/com/taskforge/model/User.java`, add a temporary field:
   ```java
   private String phoneNumber;
   ```
2. Check status:
   ```bash
   git status
   ```
3. Stash your uncommitted changes:
   ```bash
   git stash push -m "WIP: adding phone number to User"
   ```
   Notice that `git status` is now completely clean!
4. List your stashes:
   ```bash
   git stash list
   ```
5. Re-apply your changes when you return:
   ```bash
   git stash pop
   ```

---

## Lab 8: Undoing Mistakes (Restore, Revert, Reset, Amend)

### 8.1 Discard unstaged changes (`git restore`)
Suppose you made a typo or unwanted edit in `pom.xml`:
```bash
git restore pom.xml
```

### 8.2 Unstage a staged file (`git restore --staged`)
If you accidentally ran `git add <file>`:
```bash
git add README.md
git restore --staged README.md
```

### 8.3 Amend the latest commit (`git commit --amend`)
Forgot a file or typo in the last commit message?
```bash
git commit --amend -m "new improved commit message"
```

### 8.4 Safe undo in shared branches (`git revert`)
`git revert` creates a *new* commit that undoes the changes of an earlier commit without rewriting history:
```bash
git revert HEAD --no-edit
```

### 8.5 Resetting commits (Local only!)
- `git reset --soft HEAD~1`: Moves branch back 1 commit; changes stay staged.
- `git reset --mixed HEAD~1`: Moves branch back 1 commit; changes stay in working directory unstaged (default).
- `git reset --hard HEAD~1`: ⚠️ Completely deletes changes from working directory and index.

---

## Lab 9: Git Rebase & Interactive Rebasing (Squash)

### 9.1 Rebase feature branch on updated `main`
Instead of merge commits, rebase replays your commits linearly onto the tip of `main`:
```bash
git switch -c feature/audit-filter
# make changes & commit
git switch main
# main updates
git switch feature/audit-filter
git rebase main
```

### 9.2 Squash multiple messy commits into one (`git rebase -i`)
To clean up your last 2 commits before opening a pull request:
```bash
git rebase -i HEAD~2
```
In the editor, change `pick` to `squash` (or `s`) for the second commit, save, and exit.

---

## Lab 10: Tagging Versions & Working with Remote (GitHub/GitLab)

### 10.1 Tag a release version
```bash
git tag -a v1.0.0 -m "Release v1.0.0: Initial TaskForge core release"
git tag -l
git show v1.0.0
```

### 10.2 Connect to a remote repository (GitHub)
When you create a repository on GitHub / GitLab:
```bash
git remote add origin https://github.com/your-username/taskforge.git
git branch -M main
git push -u origin main --tags
```
To fetch and pull updates:
```bash
git fetch origin
git pull --rebase origin main
```

---

## 🎯 Quick Command Cheat Sheet

| Command | Purpose |
|---|---|
| `git status` | Show status of working directory & staging area |
| `git add <file>` / `git add .` | Stage file(s) for the next commit |
| `git commit -m "msg"` | Save snapshot to local history |
| `git log --oneline --graph` | View formatted visual branch history |
| `git switch -c <name>` | Create and switch to a new branch |
| `git switch <name>` | Switch to an existing branch |
| `git merge <name>` | Merge specified branch into current branch |
| `git stash` / `git stash pop` | Temporarily shelve/restore uncommitted changes |
| `git restore <file>` | Discard changes in working directory |
| `git revert <hash>` | Safely invert an existing commit with a new commit |
| `git rebase <branch>` | Reapply commits on top of another base tip |
| `git tag -a <version>` | Create an annotated release tag |
| `git blame <file>` | Inspect line-by-line commit authorship |
