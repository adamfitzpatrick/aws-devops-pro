# CodePipeline

## AWS Documentation

*Source: [Introduction to DevOps on AWS (AWS CodePipeline)](https://docs.aws.amazon.com/pdfs/whitepapers/latest/introduction-devops-aws/introduction-devops-aws.pdf#aws-codepipeline)*

AWS CodePipeline is a continuous delivery service that you can use to model, visualize, and
automate the steps required to release your software. With AWS CodePipeline, you model the full
release process for building your code, deploying to pre-production environments, testing your
application, and releasing it to production. AWS CodePipeline then builds, tests, and deploys your
application according to the defined workflow every time there is a code change. You can integrate
partner tools and your own custom tools into any stage of the release process to form an end-to-
end continuous delivery solution.

AWS CodePipeline has several benefits that align with the DevOps principle of continuous
deployment:

- **Rapid delivery** AWS CodePipeline automates your software release process, allowing you
to rapidly release new features to your users. With CodePipeline, you can quickly iterate on
feedback and get new features to your users faster.
- **Improved quality** By automating your build, test, and release processes, AWS CodePipeline
enables you to increase the speed and quality of your software updates by running all new
changes through a consistent set of quality checks.
- **Easy to integrate** AWS CodePipeline can easily be extended to adapt to your specific needs.
You can use the pre-built plugins or your own custom plugins in any step of your release process.
For example, you can pull your source code from GitHub, use your on-premises Jenkins build
server, run load tests using a third-party service, or pass on deployment information to your
custom operations dashboard.
- **Configurable workflow** AWS CodePipeline enables you to model the different stages of your
software release process using the console interface, the AWS CLI, AWS CloudFormation, or
the AWS SDKs. You can easily specify the tests to run and customize the steps to deploy your
application and its dependencies.

## Required Knowledge

- CodePipeline is a tool which provides a visual workflow to orchestrate CI/CD
- Based on "stages":
    - Basic stage set:
        - Source: CodeCommit, ECR, S3, Bitbucket, GitHub, +
        - Build: CodeBuild, Jenkins, CloudBees, TeamCity, +
        - Test: CodeBuild, AWS Device Farm, +
        - Deploy: CodeDeploy, Elastic Beanstalk, CloudFormation, ECS, S3, +
        - Invoke: Lambda, Step Functions
            - Can be used to follow up/evaluate CodeBuild/CodeDeploy actions, for example
    - Each stage can include multiple sequential and/or parallel actions
    - First stage *must* be Source stage
    - Minimum of two stages required for a pipeline
    - Stages are connected by transitions, which can be disabled between stages
- Actions
    - Various stage types constrain the actions available for that stage
    - Each action has an owner (AWS/3rd party/Custom), action type, provider
    - Each type of action includes a specific number of valid input and output artifacts
    - Action types
        - Source
        - Build
        - Test
        - Deploy
        - Approval
            - Prevents transition to next action until approval is granted
        - Invoke
- Artifacts
    - May be source data, build output, test results, invoke result, +
    - may be passed from stage to stage and may be included in final output
    - Stored in S3 bucket
- Revisions
    - Change made to the source location
    - Can have multiple revisions flowing through the pipe at the same time
- Security
    - An IAM service role must be attached to a pipeline which proper permissions to allow execute
    stages & actions
    - Lack of proper permissions is common source of stage failures
    - Supports Amazon VPC endpoints via AWS PrivateLink -> keeps all traffic inside the VPC and AWS
    network
- CloudWatch Events (EventBridge) can monitor pipeline state changes and respond to them
    - Stage failure spawns events and info about the failure
    - Events can trigger SNS (sending texts or emails to users), lambda, +
    - Can use lambda to diagnose/respond to failures
    - Useful for approval process
        - Pipeline approval requires `codepipeline:GetPipeline` and `codepipeline:PutApprovalResult`
        permissions
- Using with CloudFormation
    - Target of Deploy or Build actions, such as deploying lambda using CDK or SAM
    - Can work with CloudFormation stack sets
    - Can use/manage Template Parameter Overries including
        - predefined/static overrides
        - Dynamic overrides
            - Retrieving parameters from input artifacts passed from prior stages
            - Retrieve from SSM param store
        - Param names must be present in template
    - Action modes
        - Create/replace change set
        - Execute change set
        - Create/update stack
        - Delete stack
        - Replace failed stack
    - Example workflow:
        - Build: CodeBuild creates template
        - Deploy: CloudFormation deploys infrastructure and app code
        - Build: CodeBuild runs tests on deployed application
        - Deploy: CloudFormation deletes test infrastructure
        - Deploy: CloudFormation deploys production infrastructure and application
    - Best Practices
        - Multi-environment deploy
            - 1 pipeline, 1 CodeDeploy -> execute parallel deploy to multiple deployment groups
            - *not* multiple CodeDeploy deployments
        - Multiple actions that can run parallel should do so
            - Set RunOrder value for parallel actions to the same value
            - Example: Source (CodeCommit) -> Two parallel CodeBuild actions
        - Deploy to pre-prod (dev/test/stage) before deploying to prod, and include a deployment
        gate requiring approval to proceed
    - Multi-region pipelines
        - Actions in pipeline can operate in different regions
            - Example: Deploy a lambda function through CloudFormation into multiple regions
        - S3 artifact stores *must* be defined in each region in which actions will occur
            - CodePipeline must have read/write access into every artifact bucket involved
            - Once input artifact names are specified, CodePipeline will handle copying from region
            to region
        - Use CloudWatch Events for change detection

## Console Lab

*Ensure you have an existing CodeCommit repository, such as that created in the
[CodeCommit console lab](./code-commit.md#console-lab).  Additionally, you will be required to have
an S3 bucket in which to deposit your initial build output.*
    
### Create Minimal Pipeline with CodeDeploy stage

1. If you have not completed the CodeDeploy labs or if you removed the demo application from
CodeDeploy, return to the lab and complete the
[third CodeDeploy lab](./code-deploy.md#deploy-an-application-to-lambda). Make a change to the
lambda you created in that lab, and publish it as the next available version.  Modify the appspec
file in the [CodeDeploy lambda lab folder](../labs/code-deploy-lambda/) so that it correctly
references the current version and the target version you just published. Replace the current
content of your CodeCommit repository with the contents of the CodeDeploy lambda folder.  Commit
the changes to the main branch and push your changes.
1. In the AWS console, navigate to CodePipeline, and click "Create pipeline", and give the new
pipeline a catchy name. Ensure the Pipeline type is set to V2
    - Has a JSON structure like V1, but includes additional parameters such as Git tag triggers and
    pipeline-level variables
1. Select "Superseded" under Execution mode
    - Pipelines using "Superseded" allow a more recent execution to overtake an older one (default)
    - In "Queued" pipelines, executions are processed one-by-one in the order received
    - "Parallel" pipelines don't wait for other executions to complete before starting or completing
    a new execution
1. Select "New service role" if you have never created a pipeline before, or choose "Existing
service role" and select a Role ARN from the dropdown list. Under "Advanced settings", ensure the
Artifact store is "Default location" and encryption key is "Default AWS Managed Key". Click "Next"
1. For Source provider, select "AWS CodeCommit", select the repo in which you placed the
[CodeDeploy lambda lab](../labs/code-deploy-lambda/), and select the main branch. Under "Change
detection options" ensure CloudWatch Events is selected, and under "Output artifact format", ensure
"CodePipeline default" is chosen. Click "Next"
1. Click "Skip build stage", and then "Skip" in the confirmation dialog
1. Under "Deploy provider", select "AWS CodeDeploy", and select the CodeDeploy application you
created in [part 1 of the CodeDeploy lab](./code-deploy.md#deploy-an-application-to-lambda).
Select a deployment group under that application.
1. Click "Create pipeline". Observe the progress of your new pipeline, and verify that it completes
successfully. Test the dev alias for your function to verify that your changes were deployed.

### Add Unit Test/Build stage

1. Let's combine the work from the [CodeBuild lab](../labs/code-build/) with the CodeDeploy lab. If
you haven't completed this lab, or if you deleted the build project, return to it and complete
[part 1](./code-build.md#create-a-codebuild-project). *Delete* the lambda you created for the
CodeDeploy lambda lab, as we're going to let CloudFormation create the function and create new
versions for us.
1. Create a new build project called 'demo-test', select AWS CodeCommit as the source provider, and
leave everything at default values except:
    - Service role: Use the same one that you used for the CodeBuild lab project
    - Buildspec: Click "Use a buildpsec file" and enter "testspec.yml" for the file name.
    - Artifact 1 should be placed in a bucket of your choosing and should be zipped
    - Artifact 2 is named "appspec" and should be placed in the same bucket as artifact 1, but not
    zipped
Scroll down and click "Create build project".
1. Return to your CodePipeline codepipeline lick on "Disable transition" between the Source and
Deploy stages, enter any reason and click "Disable" in the dialog. Click "Edit" near the top of the
page, and scroll down to just before the Deploy stage. Click "Add stage". Name the new stage
"Build-Test" and click "Add stage". Now click "Add action group", enter "Unit_Test". Select "AWS
CodeBuild" as the provider. "Input artifacts" should be set to the Source artifact, and project name
should be "demo-test". Click "Done"
1. Click "Add action" next to the Unit_Test action. Add a second CodeBuild action, but name this one
"Build".  Select the Source artifact for input, and enter "demo-build" for the build project.  Click
"Done", and then scroll to the top and click "Done".
1. Copy the files in
[CodePipeline lab folder](../labs/code-pipeline/) to your local repo, overwriting the 
`buildspec.yml`. Commit the changes, and push the repo.  Your pipeline should now execute and
progress through the build stages successfully.
1. Note that the build has succeeded *and* that there is now a "Queued execution" held between the
Build_Test and Deploy stages.
    - What we've done here is add an action to test the application which runs in parallel with an
    action to build the application and perform the initial deploy steps, which include creation
    of an appspec file for CodeDeploy.
    - Note that, if either of these actions fails, the pipeline will not continue on to the Deploy
    stage.

### Add CloudFormation Deploy Action

1. Create a role for CloudFormation:
    - 
1. Edit your pipeline again, and, in the Deploy stage, click "+ Add action group" *before* the 
Deploy action. For "Action name" enter "Deploy_Version", and for "Action provider" select "AWS 
CloudFormation".  Add both the "SourceArtifact" and "BuildArtifact" from previous stages. Set
"Action mode" to "Create or update a stack", and give your new stack a name such as "demo-stack".
Under the "Template" section, "Artifact name" is "SourceArtifact", and "File name" is 
"cloudformation.template.yml".  Select "CAPABILITY_NAMED_IAM" under "Capabilities" and use the
role you created in the previous step.  Expand the "Advanced" area, and enter the following in the
"Parameter overrides" text box:


    ```json
    {
        "LambdaArtifactBucket": { "Fn::GetArtifactAtt": ["BuildArtifact", "BucketName"]},
        "LambdaArtifactKey": { "Fn::GetArtifactAtt": ["BuildArtifact", "ObjectKey"]}
    }
    ```

Click done to finish adding the action to the Deploy stage. Enable transitions between the Build and
Deploy stages, and run the pipeline.  It should succeed through the **Deploy_Version** action, but
fail on the **Deploy** action.
1. Go to your lambda function.  You'll notice now that there is a published verion.  The function
code was update by CloudFormation, which then published a new version.

---

1. Ensure an IAM role exists sufficient to deploy the application:
    - The role must have a trust relationship with CloudFormation:

        ```json
        {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Sid": "",
                    "Effect": "Allow",
                    "Principal": {
                        "Service": "cloudformation.amazonaws.com"
                    },
                    "Action": "sts:AssumeRole"
                }
            ]
        }
        ```

    - The role must allow the following actions:
        - ec2:DescribeSecurityGroups
        - ec2:CreateSecurityGroup
        - ec2:AuthorizeSecurityGroupIngress
        - ec2:DescribeInstances
        - ec2:RunInstances
        - ec2:TerminateInstances
        - ec2:DeleteSecurityGroup
            - *Note: this is required to allow CloudFormation to remove the instance when you are
            done.*
    - For simplicity, the resource value for the above actions can be set to "*"
1. Modify the pipeline created in the previous section by clicking "Edit". Scroll down to the
"Deploy" stage and click "Edit stage". Click the pencil icon that appears on the Deploy action.
1. Change the action provider from "Amazon S3" to "AWS CloudFormation". Change the Action mode to
"Create or update a stack", and enter a suitable name for the stack. Select "SourceArtifact" in
Artifact name, and enter the name of the web server template file into the File field
(`ec2-web-server.template.yml` unless you changed it). Under "Capabilities", select
`CAPABILITY_NAMED_IAM`. Under the "Role name" drop down, select the IAM roll you created above.
Leave everything else as-is, and click "Done". For the Deploy stage, click "Done"
1. Click "Release change" and observe the pipeline execution to confirm success.
1. In CloudFormation, click on the newly created stack.  Click on "Output" and navigate to the
WebsiteURL. Once you have verified that the web server is functioning, return to CodePipeline, and
click on "Review" in the approval step. Approve the result and let the pipeline execution complete.
Verify that the pipeline completed succesfully and your EC2 stack has been deleted.

### Add Testing and Approval Gate

1. Modify your pipeline by clicking "Edit". Click on the "+ Add stage" button directly below the Source stage, and call it "Test", and click "Add stage". Click on "+ "Add action group".  Give the stage a name like "Unit_Test" or similar, and select CodeBuild as the action provider. Select "SourceArtifact" as the input artifact, and click "Create Project".  A pop-up appears allowing you to set up a new CodeBuild project.
1. In the pop-up window, give the new project a catchy name, leave all options set to default values, and allow AWS to create a new service role.  Scroll down to the Build Spec section, ensure "Insert build commands" is selected. Enter `npm test` as the command to run. Scroll to the bottom and click "Continue to CodePipeline". Wait for the pop-up to close. Click "Done" to complete adding the action group, then scroll to the top and click "Save" to finish adding the test stage.
1. Below the Deploy stage, click "+ Add Stage". Name the new stage "Clean-up". Click "+ Add action group". Name the new action "Approval", and select "Manual approval" as the action provider. Click "Done"
1. Click "Add action group" again. *Note: Don't just "Add action", because it will create a parallel action execution, and we want to wait for approval first.* Name the new action "Delete_stack", and select "AWS CloudFormation" as the action provider. Select "Delete a stack" for Action mode, enter the name of the stack you are creating in the previous stage, and select the IAM role you created above. Click "Done", and then click "Done" for the Clean-up stage. Scroll to the top and click "Save". Click "Save" again in the confirmation dialog.
1. Start a pipeline execution, and verify that the added test succeeds, and that the pipeline pauses at the approval stage.
1. Verify that the application has deployed successfully, then approve proceeding on the clean-up stage.
1. Verify that the clean-up stage succeeds and removes the deployment.

#### Redesign

Goals:
- ~~Create basic pipeline with CodeDeploy~~
- Create basic pipeline with CloudFormation
- Add Build/Test stage
- Add manual approval
- Incorporate SNS messaging
- Add lambda invoke stage
- Change source to GitHub