Contributing
============

[![Build Status](https://travis-ci.org/twitter/scala_school2.png?branch=master)](https://travis-ci.org/twitter/scala_school2)

This work is 

Workflow
--------

Setup:

1. Fork the [main repository](https://github.com/twitter/scala_school2) to your account.

2. Clone your GitHub repository locally:

    ```shell
    git clone git@github.com:<username>/scala_school2.git
    ```

3. Your local repository will have your remote GitHub repository bound to `origin`. You should also add the main repository as `upstream`:

    ```shell
    git remote add upstream git@github.com:twitter/scala_school2.git
    ```

Derp:

1. Pull the latest changes from `upstream`:

    ```shell
    git pull upstream master
    ```

2. Do your work on a branch:

    ```shell
    git checkout -b <branchname>
    ...
    git commit
    ```

3. Push the new branch to `origin`:

    ```shell
    git push -u origin <branchname>
    ```

4. Submit a pull request.
