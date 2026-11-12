# How to work on Goose Patrol

## Once

1. Accept the the github invitation it expiers after 7 days.
2. Install Java 17
3. Tell Git Who you are. use an email on your Github account, so commits count as yours:

   ```
   git config --global user.name "Your Name"
   git config --global user.email "you@york.ac.uk"
   ```

4. Log Git in to GitHub with your own account. Either use [GitHub CLI](https://cli.github.com):

   ```
   gh auth login
   gh auth setup-git
   ```

   Or use Git Credential Manager. Git for Windows already has it. On a Mac:

   ```
   brew install git
   brew install --cask git-credential-manager
   ```

   Either way, you log in through your browser and never handle a token. Don't use
   `git config credential.helper store`.

5. Clone the repo, then that it builds and runs. on windows use `gradlew.bat`    instead of `./gradlew`.
   The first build downloads Gradle and the Java it runs on, so it needs internet.

   ```
   git clone https://github.com/goose-meat-eng1/goose-patrol.git
   cd goose-patrol
   ./gradlew build
   ./gradlew lwjgl3:run
   ```

## every piece of work

Every branch starts from the newest `main`, and every pull request goes back into `main`.

1. choose a Github issue and assign yourself to it. Working   directory in the repo and you have to install Github 

   ```
   gh issue create --title "..." --body "..." --assignee @me
   ```
   Or create using [Github](https://github.com/goose-meat-eng1/goose-patrol/issues/1)
   
2. update  `main`and make branch named after the issuezs requirment ID (from our requirements document, Req1):
   ```
   git switch main
   git pull
   git switch -c FR_PAUSE-pause-key
   ```

   If `git status` shows changes you want to keep, such as work started on an old branch, run `git stash` before
   these three commands and `git stash pop` after them. The changes then move to the new branch. If
   `git stash pop` reports a conflict, fix those files, then run `git stash drop`.

3. Commit in small steps. start each message with ID, for example `FR_PAUSE: pause key toggles the run` 
4. Push the branch: `git push -u origin FR_PAUSE-pause-key`
5. Open a pull request to `main` write `closes #N` whith the issue number in the description and ask for review.
6 Once it's aproved, click **Merge pull request**, then delete the branch. If nobody reviw it in 24 hours, merge it yourself once  `./gradlew build` passes.
7. Update your copy: `git switch main`, then `git pull`, then delete your branch with `git branch -d FR_PAUSE-pause-key`.
   `-d` won't delete commits that aren't merged or pushed, so nothing is lost.

## Rules

- Never commit straight to `main`, and never force-push.
- If `main` changes while your branch is open, commit your work, then merge `main` into your branch so it still
  builds with everyone else's work:
  ```
  git switch main
  git pull
  git switch FR_PAUSE-pause-key
  git merge --no-edit main
  ```
  `--no-edit` keeps Git's own merge message, so no editor opens. If Git reports conflicts, fix those files,
  `git add` them and run `git commit --no-edit`. Check `./gradlew build` passes, then `git push`. Don't rebase
  instead: a rebased branch needs a force-push.
- When a slice is finished (all its issues merged), tag `main` with the minor version one higher than the last tag:
  slice 2 is `v0.2.0`, slice 3 is `v0.3.0`.
  ```
  git switch main
  git pull
  git tag -a v0.2.0 -m "Slice 2: ..."
  git push origin v0.2.0
  ```
- Don't change the repo's visibility. It stays private until 12:00 GMT on Mon 16 Nov 2026.
