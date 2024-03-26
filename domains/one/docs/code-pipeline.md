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
- Actions
    - Various stage types constrain the actions available for that stage
    - Each action has an owner (AWS/3rd party/Custom), action type, provider
    - Each type of action includes a specific number of valid input and output artifacts
- Artifacts
    - May be source data, build output, test results, invoke result, +
    - may be passed from stage to stage and may be included in final output
    - Stored in S3 bucket
- Security
    - An IAM service role must be attached to a pipeline which proper permissions to allow execute stages & actions
    - Lack of proper permissions is common source of stage failures
- CloudWatch Events (EventBridge) can monitor pipeline state changes and respond to them
    - Stage failure spawns events and info about the failure
    - Events can trigger SNS (sending texts or emails to users), lambda, +
    - Can use lambda to diagnose/respond to failures
    - Useful for approval process
        - Pipeline approval requires `codepipeline:GetPipeline` and `codepipeline:PutApprovalResult` permissions
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
        - Deploy to pre-prod (dev/test/stage) before deploying to prod, and include a deployment gate requiring approval to proceed
    - Multi-region pipelines
        - Actions in pipeline can operate in different regions
            - Example: Deploy a lambda function through CloudFormation into multiple regions
        - S3 artifact stores *must* be defined in each region in which actions will occur
            - CodePipeline must have read/write access into every artifact bucket involved
            - Once input artifact names are specified, CodePipeline will handle copying from region to region

## Console Lab
    
