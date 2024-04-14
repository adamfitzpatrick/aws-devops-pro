# CodeBuild

## AWS Documentation

*Source: [Introduction to DevOps on AWS (AWS CodeBuild)](https://docs.aws.amazon.com/pdfs/whitepapers/latest/introduction-devops-aws/introduction-devops-aws.pdf#aws-codebuild)*

AWS CodeBuild is a fully managed continuous integration service that compiles source code, runs
tests, and produces software packages that are ready to deploy. You don’t need to provision,
manage, and scale your own build servers. CodeBuild can use either of GitHub, GitHub Enterprise,
BitBucket, AWS CodeCommit, or Amazon S3 as a source provider.

CodeBuild scales continuously and can process multiple builds concurrently. CodeBuild offers
various pre-configured environments for various versions of Microsoft Windows and Linux.
Customers can also bring their customized build environments as Docker containers. CodeBuild
also integrates with open source tools such as Jenkins and Spinnaker.

CodeBuild can also create reports for unit, functional, or integration tests. These reports provide
a visual view of how many tests cases were run and how many passed or failed. The build process
can also be run inside an Amazon Virtual Private Cloud (Amazon VPC) which can be helpful if your
integration services or databases are deployed inside a VPC.

## Required Knowledge

- Accepts CodeCommit, S3, Bitbucket and GitHub as code sources
- Build instructions
    - buildspec.yml file recommended
    - Can also use manual config via console
- Log output to CloudWatch and S3
    - CloudWatch Metrics to monitor statistics
    - CloudWatch alarms can provide info on failure thresholds
- EventBridge to detect failed builds and trigger notifications
- Projects can be created directly in CodeBuild or via CodePipeline (regardless, they're still projects *in* CodeBuild)
- Build environments
    - Prebuilt images
        - Java
        - Ruby
        - Python
        - Go
        - Node.js
        - Android
        - .NET Core
        - PHP
    - Can use Docker to create or extend any environment
- Process
    - CodeBuild pulls from source, including the buildspec.yml file
    - Pulls the desired environment from either a prebuilt Docker image or a custom one
    - CodeBuild runs instructions in the buildspec.yml
    - Can include optimizations allowing some files to be cached in S3 between builds to speed things up
    - Can log to CloudWatch and/or S3
    - Runs test
    - Produces artifacts which are placed into an S3 bucket
- local builds
    - Troubleshooting builds locally is possible with Docker/CodeBuild Agent
- Inside VPC
    - By default CodeBuild containers are launched outside a VPC
    - It is possible to specify a VPC config with the build to enable resources within the VPC
    - Use cases include integration tests, data query, and internal LBs
- Environment variables
    - Some are defined and provided by AWS
        - `AWS_DEFAULT_REGION`
        - `CODEBUILD_BUILD_ARN`
        - `CODEBUILD_BUILD_ID`
        - `CODEBUILD_BUILD_IMAGE`
    - Custom env vars
        - Static defined at build time (but can be overridden with start-build API call)
        - Dynamic from SSM param store and Secrets Manager
- Security
    - Build specific service role
    - In-transit and at-rest data encryption
    - Artifact encryption
- Build badges (public URL)
- Triggers
    - CodeCommit -> EventBridge -> CodeBuild
    - Could also shove a Lambda before the build for preparatory actions
    - GitHub -> Webhook -> CodeBuild
- Reports
    - Can get visual test reports from CodeBuild UI
    - Not necessary to view logs
    - Accepts JUnit, NUnit, NUnit3, Cucumber, TestNG, VisualStudio TRX
- buildspec.yml
    - possible to override the default buildspec filename and location
    - Can use different files for different builds in same repo, such as `buildspec_debug.yml` and `buildspec_release.yml`
    - Can also store a buildspec file other than the source root
    - Only one buildspec is permitted per project
        - Syntax [https://docs.aws.amazon.com/pdfs/codebuild/latest/userguide/codebuild-user.pdf#build-spec-ref](https://docs.aws.amazon.com/pdfs/codebuild/latest/userguide/codebuild-user.pdf#build-spec-ref):

        ```yaml
        version: 0.2
        run-as: linux-user-name
        env:
            shell: shell-tag
            variables:
                key1: value1
                key2: value2
            parameter-store:
                key1: value1
                key2: value2
            exported-variables:
                - variable1
                - variable2
            secrets-manager:
                key: secret-id:json-key:version-stage:version-id
            git-credential-helper: no | yes
        proxy:
            upload-artifacts: no | yes
            logs: no | yes
        batch:
            fast-fail: false | true
            # build-list:
            # build-matrix:
            # build-graph:
        phases:
            install:
                run-as: linux-user-name
                on-failure: ABORT | CONTINUE
                runtime-versions:
                    runtime: version
                    runtime: version
                commands:
                    - command
                    - command
                finally:
                    - command
                    - command
                # steps:
            pre_build:
                run-as: linux-user-name
                on-failure: ABORT | CONTINUE
                commands:
                    - command
                    - command
                finally:
                    - command
                    - comman
                # steps:
            build:
                run-as: linus-user-name
                on-failure: ABORT | CONTINUE
                commands:
                    - command
                    - command
                finally:
                    - command
                    - command
                # steps:
            post_build:
                ...
        reports:
            report-group-name-or-arn:
                files:
                    - location1
                    - location2
                base-directory: location
                discard-paths: no | yes
                file-format: report-format
        artifacts:
            files:
                - location1
                - location2
            name: artifact-name
            discard-paths: no | yes
            base-directory: location
            exclude-paths: excluded paths
            enable-symlinks: no | yes
            s3-prefix: prefix
            secondary-artifacts:
                artifactIdentifier:
                    files:
                        - location
                        - location
                    name: secondary-artifact-name
                    discard-paths: no | yes
                    base-directory: location
        cache:
            paths:
                - path
                - path
        ```

        - **version**: only use 0.2
        - **run-as**
            - optional
            - available to linus users only
            - grants the specified user read and run permissions.
            - When placed at the top of the buildspec, applies globally
        - **env**
            - optional
            - sensitive information is frequently automatically hidden, such as AWS access key IDs, strings from param store, and strings from Secrets manager
            - **shell**
                - specifies support shell
                - linux
                    - bash
                    - /bin/sh
                - windows
                    - powershell.exe
                    - cmd.exe
            - **variables**
                - required if **env** is specified
                - defines custom environment variables in plain text
                - mapping of key/value pairs
                - don't put sensitive values in here
                - lowest precedence; can be overridden by start build call, then by build project definition
            - **parameter-store**
                - retrieves custom env variables stored in systems manager parameter store
                - mapping of key/value pairs, where the key is used in build commands later, and value is the name of the variable stored in SM parameter store
                - requires the project service role to have `ssm:GetParameters`
                - lowest precendence; first is start build call, second in build project definition
            - **secrets-manager**
                - retrieves custom vars from Secrets Manager
                - reference-key pattern:
                    - key: secret-id:json-key:version-stage:version-id
                    - key: local env var name
                    - secret-id: name or ARN from SM
                    - json-key: SM key-value pair from the secret
                    - version-stage: specifies secret version by staging label; if you use this don't use version-id
                    - version-id: specifies secret version by unique id; don't use version-stage
            - **exported-variables**
                - used to list vars to export
                - used with CodePipeline to export vars to subsequent stages
                - Available starting in `install` phase, and can be changed through the `post_build`
                - Certain values cannot be exported:
                    - SM parameter store secrets
                    - Secrets manager secrets
                    - Environment vars that start with `AWS_`
            - **git-credential-helper**
                - Used to indicate whether CodeBuild uses its credential helper to provide git
                credentials.
                - Cannot be used with public repos
        - **proxy**
            - optional
            - used if the build runs in an explicit proxy server (see
            [Run CodeBuild in an Explicit Proxy Server](https://docs.aws.amazon.com/pdfs/codebuild/latest/userguide/codebuild-user.pdf#%5B%7B%22num%22%3A10153%2C%22gen%22%3A0%7D%2C%7B%22name%22%3A%22XYZ%22%7D%2C36%2C364.903%2Cnull%5D))
            - **upload-artifacts**
                - optional
                - Set to `yes` if proxy server build should upload artifacts; default is no
            - **logs**
                - optional
                - Set to `yes` if your proxy server build should send logs to CloudWatch; default is
                no
        - **phases**
            - REQUIRED
            - Note: in version 0.1, all phases ran in distinct shell instances, making each phase
            unable to easy include or effect the results of other phases.  USE VERSION 0.2.
            - Structure for each phase:
                - **run-as**
                - **on-failure**
                    - `ABORT`
                    - `FAILURE`
                - **finally**
                    - optional
                    - commands to run after the **commands** block
                    - commands are run even if the phase fails
                - **commands**
                    - array of commands to run during the phase
                    - run sequentially in order
            - **install**
                - **runtime-versions**
                    - optional
                    - if specified, at least one runtime must be specified
                    - Can use specific version (20.2 or 20.2.1), specific major version (`20.x`), or
                    `latest`
            - **pre_build**
            - **build**
            - **post_build**
        - **reports**
            - optional
            - **report_group_name_or_arn**
                - Specifies the names or arns to which CodeBuild sends the build report
                - Can have as many as five report groups
                - Specify arns for existing groups
                - Specify name for new groups, and CodeBuild creates a group:
                    - `<project_name>-<report_group_name>`
                - **files**
                    - REQUIRED
                    - represents locations that contain raw data of test results for CodeBuild to
                    use to generate reports
                    - relative to original build location or `base-directory` if set
                    - Can be single file
                    - Can be single file in subdirectory
                    - `**/*` is all files recursively
                    - `subdirectory/*` is all files in a subdirectory
                    - `subdirectory/**/*` is all files recursively in a subdirectory
                - **file-format**
                    - optional
                    - Not case sensitive
                    - Test reports
                        - `JUNITXML` (default)
                        - `CUCUMBERJSON`
                        - `NUNITXML`
                        - `NUNIT3XML`
                        - `TESTNGXML`
                        - `VISUALSTUDIOTRX`
                    - Coverage reports
                        - `CLOVERXML`
                        - `COBERTURAXML`
                        - `JACOCOXML`
                        - `SIMPLECOV`
                - **base-directory**
                    - optional
                    - one or more top-level directories from which to search for test report data
                - **discard-paths**
                    - if `yes`, CodeBuild flattens the report output
        - **artifacts**
            - optional
            - indicates where CodeBuild can find build output and how to prepare for upload to S3
            - **files**
                - REQUIRED
                - Where CodeBuild can find the build output
            - **name**
                - optional
                - name for build artifact
                - Can be calculated at build time, e.g., `myname-$(date +%Y-%m-%d)` or
                `myname-$AWS_REGION`
            - **discard-paths**
                - optional
                - If `yes`, build output will be flattened
            - **base-directory**
                - optional
                - Specifies where to start looking for the **files** for build artifacts
            - **exclude-paths**
                - optional
                - specifies paths to exclude from build artifacts
            - **enable-symlinks**
                - optional
                - if output is ZIP, specifies that internal symbolic links are preserved in the ZIP
                file
            - **s3-prefix**
                - optional
                - Artifacts will be saved as `<s3-prefix>/<build-id>/<name>.zip`
            - **secondary-artifacts**
                - optional
                - Mapping of artifact identifier to artifact definitions
                - Example:
                    ```yaml
                    files:
                        - '**/*'
                    secondary-artifacts:
                        artifact1:
                            files:
                                - directory/file1
                            name: secondary-artifact-1
                    ```
        - **cache**
            - optional
            - Info for CodeBuild to prepare files for uploading to S3 cache
            - **paths**
                - REQUIRED
                - locations of cacheable artifacts, relative to original build location or
                `base-directory`

## Console Lab

### Create a CodeBuild Project

*Note: The test case is pretty contrived here, but we'll see better and more extensive examples of
CodeBuild when we start integrating with CodePipeline and CodeDeploy.*

1. Create a new secret in Secrets Manager; this will be the result expected by our unit tests:
    - Secret type: `Other type of secret`
    - Key/Value pair: `TEST_EXPECTED_RESULT: 107`
    - Secret name: `demo-build/secrets`
1. Create two new parameters in the Systems Manager Parameter Store; this will store input values
used by our unit tests:
    - Parameter 1:
        - Name: `/demo-build/data-point`
        - Type: `String`
        - Value: `5`
    - Parameter 2:
        - Name: `/demo-build/coefficients`
        - Type: `StringList`
        - Value: `3,6,2`
1. Commit the contents of the [code-build lab folder](../labs/code-build/) to the main branch
of the CodeCommit repository you created in the [CodeCommit lab](./code-commit.md#console-lab).
1. Create a new CodeBuild project and give it a suitable name.  Set the source provider to "AWS
CodeCommit", and select therepo to which you committed the lab files.  Choose the main branch. Leave
all the environment settings at their defaults. Have AWS create a new service role, name it and
note the name down. Under the "Buildspec" section, select "Use a buildspec file".  Under "Artifacts", set
the type for Artifact 1 to "Amazon S3". Select a bucket in the same region as your CodeBuild
project. Under "Artifacts packaging", select "Zip".  Leave all other options at their defaults.
1. Find the role that was created for the new build project (it will have "-service-name" appended
to the name you chose above). Note that AWS attached a policy with many of the permissions required,
but you'll need to add an additional inline policy:
    - Actions:
        - ssm:GetParameters
        - secretsmanager:GetSecretValue
    - Resources: "*"
1. Return to your CodeBuild project and start a new build. As the build progresses, note the log
output.  The unit test attempts to display all of the values used to conduct the test.  However,
since they mostly come either from Parameter Store or Secrets manager, they are not logged;
CodeBuild hides the output. The only value logged is the one set as a basic environment variable:
"Solution".  Even when the test attempts to logged the *calculated* value, it is obscured.  You can
even add "echo 107" to the buildspec, but 107 will be obscured because it is the same as a secret
value.
