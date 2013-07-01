# Contributing

[![Build Status](https://travis-ci.org/twitter/scala_school2.png?branch=master)](https://travis-ci.org/twitter/scala_school2)

## Workflow

We use GitHub's normal [fork-and-pull](https://help.github.com/articles/using-pull-requests#fork--pull) collaboration model, which usually works like this:

### Initial setup

1. [Fork](https://help.github.com/articles/fork-a-repo) the [main Scala School 2 repository](https://github.com/twitter/scala_school2) from the Twitter account to your personal account.

2. Clone your fork from GitHub to your local development machine:

    ```shell
    git clone git@github.com:<username>/scala_school2.git
    ```

3. Your local repository will have `origin` bound to your remote repository on GitHub, where it was cloned from. You'll also want to be able to pull directly from the upstream Twitter repository, so:

    ```shell
    git remote add upstream git@github.com:twitter/scala_school2.git
    ```

### For each pull request

1. Pull the latest changes from `upstream/master` (Twitter's repo) into your local `master` branch:

    ```shell
    git checkout master
    git pull upstream master
    ```

2. Do your work on a feature branch:

    ```shell
    git checkout -b <branchname>
    ...
    git commit
    ```

3. Push the new branch to `origin` (your personal GitHub remote):

    ```shell
    git push -u origin <branchname>
    ```

4. The new branch will then appear in your GitHub repository, along with a helpful suggestion to "compare & pull request." [Do it](https://help.github.com/articles/using-pull-requests)! It should probably reference an issue number in our [issue tracker](https://github.com/twitter/scala_school2/issues?state=open), like: `fix #123: teach all the scalas`.

## Rapid Development

Since the server doesn't automatically pick up code or resource changes on the fly when using `sbt run`, it's typically handiest to use the [sbt-revolver](https://github.com/spray/sbt-revolver) plugin instead; this is already configured in the project. From within the `sbt` console, invoke:

```shell
~re-start
```

This will kick the server every time you save a change to disk, which should take just a couple seconds. Leave the console running in the background while you work, and if you notice that changes you're making aren't showing up, check the console for compile errors.
