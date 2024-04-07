# CodeDeploy

## AWS Documentation

*Source: [Introduction to DevOps on AWS (AWS CodeDeploy)](https://docs.aws.amazon.com/pdfs/whitepapers/latest/introduction-devops-aws/introduction-devops-aws.pdf#aws-codedeploy)*

AWS CodeDeploy is a fully managed deployment service that automates software deployments to
a variety of compute services such as Amazon Elastic Compute Cloud (Amazon EC2), AWS Fargate,
AWS Lambda, and your on-premises servers. AWS CodeDeploy makes it easier for you to rapidly
release new features, helps you avoid downtime during application deployment, and handles
the complexity of updating your applications. You can use CodeDeploy to automate software
deployments, reducing the need for error-prone manual operations. The service scales to match
your deployment needs.

CodeDeploy has several benefits that align with the DevOps principle of continuous deployment:

- **Automated deployments** CodeDeploy fully automates software deployments, allowing you to
deploy reliably and rapidly.
- **Centralized control** CodeDeploy enables you to easily launch and track the status of your
application deployments through the AWS Management Console or the AWS CLI. CodeDeploy
gives you a detailed report enabling you to view when and to where each application revision
was deployed. You can also create push notifications to receive live updates about your
deployments.
- **Minimize downtime** CodeDeploy helps maximize your application availability during the
software deployment process. It introduces changes incrementally and tracks application health
according to configurable rules. Software deployments can easily be stopped and rolled back if
there are errors.
- **Easy to adopt** CodeDeploy works with any application, and provides the same experience
across different platforms and languages. You can easily reuse your existing setup code.
CodeDeploy can also integrate with your existing software release process or continuous delivery
toolchain (for example, AWS CodePipeline, GitHub, Jenkins).
AWS CodeDeploy supports multiple deployment options. For more information, refer to the
Deployment strategies section of this document.

## Required Knowledge

- CodeDeploy automates app deployment to EC2, on-premises, Lambda and ECS
- Can automate rollbacks for failed deployments or trigger CloudWatch alarms
- Can leverage gradual deployment strategies such partial deploys, canary and blue-green
- Hooks permit scripts to be run at various stages of deploy process, but not all stages are available for each type of deploy
    - Files for scripts and stages are specified in the appspec.yml file, example:

        ```yaml
        hooks:
          BeforeInstall:
            - location: scripts/doscript.sh
              timeout: 300
              runas: owner
        ```

- EC2/on-premises
    - Can execute in-place or blue-green deployments
    - Instances must be running CodeDeploy Agent
    - Deploy stages

        ```mermaid
        flowchart LR
            subgraph one[Load Balancer]
                direction TB
                BeforeBlockTraffic-->BlockTraffic-->AfterBlockTraffic
            end
            subgraph two[Installation]
                direction TB
                ApplicationStop-->DownloadBundle-->BeforeInstall-->Install-->AfterInstall-->ApplicationStart-->ValidateService
            end
            subgraph three[Load Balancer]
                direction TB
                BeforeAllowTraffic-->AllowTraffic-->AfterAllowTraffic
            end
            style one fill:#a6a5f9,stroke:#000
            style two fill:#fff,stroke:#000
            style three fill:#a6a5f9,stroke:#000
            one-->two-->three
        ```

    - Methods
        - In-Place
            - Can define deploy speed
                - AllAtOnce: most downtime
                - HalfAtATime: reduces capacity by 50%
                - OneAtATime: lowest availability impact, slowest
                - Custom speed
            - Can select autoscaling groups for replacement using tags
                - When identified by ASG, any new instances will receive the deploy
            - When a load balancer in use, traffic is stopped to the instances being updated
            - Hooks (stages where scripts allowed)
                - BeforeBlockTraffic (when a load balancer is in place)
                - AfterBlockTraffic (with load balancer)
                - ApplicationStop
                - BeforeInstall
                - AfterInstall
                - ApplicationStart
                - ValidateService
                - BeforeAllowTraffic (with load balancer)
                - AfterAllowTraffic (with load balancer)
        - Blue/Green
            - Requires a load balancer
            - Replaces all instances in an autoscaling group behind the load balancer
            - A new autoscaling group is started hosting the new app version
            - Load balancer is switched to the new group
            - Manual mode provisions new groups/terminates old groups based on tags 
            - Automatic mode allows CodeDeploy to determine which ASGs to provision for
            - Instance termination
                - BlueGreenDeploymentConfiguration.BlueInstanceTerminationOption object in CodeDeploy API
                    - properties
                        - action
                            - TERMINATE: instances are terminated after specified wait time
                            - KEEP_ALIVE: instances are left running after being deregistered from the load balancer and removed from deployment group
                        - terminationWaitTimeInMinutes
                            - number of minutes to wait after successful deploy before carrying out the action property
            - Hooks are same as for in-place deploy, but execute differently based on whether they are blue or green instances
                - Green instances start at `ApplicationStop` because they initially have no traffic
                - Blue instances get `BeforeBlockTraffic`, `BlockTraffic` and `AfterBlockTraffic`
    - Agent
        - Must be running on EC2 and on-premises instances
        - Can be installed automatically with Systems Manager in EC2
        - EC2 instances must have sufficient privileges
    - Triggers
        - During deploy, CodeDeploy generates several events which can be published to SNS
            - DeploymentStart
            - DeploymentSuccess
            - DeploymentFailure
            - DeploymentStop
            - DeploymentRollback
            - DeploymentReady
            - InstanceStart
            - InstanceSuccess
            - InstanceFailure
            - InstanceReady (Blue/Green deployment only)
- Lambda
    - Automates traffic shifting for lambda aliases to perform deploys
    - Integrates with SAM framework
    - No CodeDeploy agent is required
    - Process:
        - Ensure alias is established pointing at currently deployed function version
        - Specify version information in appspec.yml file
        - CodeDeploy deploys new function version, and updates to point the alias at the new version
    - hooks
        - scripts for hooks must be lambda functions
        - only `BeforeAllowTraffic` and `AfterAllowTraffic`
    - appspec.yml example:

        ```yaml
        version: 0.0
        Resources:
            - myLambdaFunction:
                Type: AWS::Lambda::Function
                Properties:
                    Name: myLambdaFunction
                    Alias: myLambdaFunctionAlias
                    CurrentVersion: 1
                    TargetVersion: 2
        ```
    - Methods
        - linear
            - Shift *x%* of traffic to new version every *y%*
        - canary
            - Initially shift *x%* of traffic to new version for *y%* minutes, then shift remainder
            - Allows new version to be verified with subset of traffic, then released to all traffice
        - AllAtOnce
            - Shift all traffic to new version simultaneously
    - example (CodePipeline):
        
        ```mermaid
        flowchart TB
        push(dev pushes changes to repo)-->cc(source stage pulls from CodeCommit)-->cb(CodeBuild deploys new function version\nand places appspec.yml file in S3)-->cd(CodeDeploy updates alias and shifts traffic)
        ```

- ECS
    - Automates deployment of new ECS task definition
    - No CodeDeploy agent is required
    - only permits blue/green deployments
        - Initial deployment to green
        - Developer must create new task definition, publish container images and create appspec.yml file
        - Can use Canary, Linear or AllAtOnce strategies just like Lambda deployments
    - Stages

        ```mermaid
        flowchart TB
        BeforeInstall-->Install-->AfterInstall-->AllowTestTraffic-->AfterAllowTestTraffic-->BeforeAllowTraffic-->AllowTraffic-->AfterAllowTraffic
        ```
        
    - Hooks
        - All scripts must be lambda functions
        - Permitted for `BeforeInstall`, `AfterInstall`, `AfterAllowTestTraffic`, `BeforeAllowTraffic`, `AfterAllowTraffic`
    - appspec.yml example

        ```yaml
        version: 0.0
        Resources:
            - TargetService:
                Type: AWS::ECS::Service
                Properties:
                    TaskDefinition: “arn:aws:ecs:aws-region: aws-account:task-definition/ecs-task-def-name:revision-number”
                    LoadBalancerInfo:
                    ContainerName: “whatevs”
                    ContainerPort: 9999
        ```

    - Example (CodePipeline)

        ```mermaid
        flowchart TB
        push(dev pushes changes with ECR image definition and appspec.yml)-->cc(source stage pulls from CodeCommit)-->cb(CodeBuild pushes ECR image, creates ECS\ntask definition, places appspec in S3)-->cd(CodeDeploy uses appspec file as input artifact)
        ```

- Deployment configurations
    - Specified number of instances that must remain available at any time during deploy
    - Can use pre-defined deployment configs
        - CodeDeployDefault.AllAtOnce
        - CodeDeployDefault.HalfAtATime
        - CodeDeployDefault.OneAtATime
    - Can also create custom deploy config
- Redeploy & Rollbacks
    - Can be
        - Automatic (when deploy fails or when CloudWatch threshold is met)
        - Manual
        - Disabled
    - When a rollback occurs, the last known good revision is deployed as a *new deployment*.
- Common Troubleshooting
    - InvalidSignatureException: data and time on EC2 instances must match signature date of the deploy request
    - When the deploy, or when all lifecycle events are skipped in EC2/on-prem deployments, and one of the following errors is shown:
        - `Overall deployment failed because too many individual instances failed deployment`
        - `Too few healthy instances are available for deployment`
        - `Some instances in your deployment group are experiencing problems (Error code: HEALTH_CONSTRAINTS)`
        - The source may be
            - CodeDeploy Agent might not be installed, running or reachable
            - CodeDeploy service role or IAM instance profile might not have required permissions
            - You're using HTTP proxy, and didn't configure CodeDeploy Agent with `:proxy_uri:` parameter
            - Date and time mismatch between CodeDeploy and agent(s)
    - Deploying to an ASG during a scale-out event may result in some instances in the ASG having out-dated versions
        - CodeDeploy automatically starts a follow-on deploy to update any outdated EC2 instances
    - Blue/Green failing `AllowTraffic`
        - ELB may have incorrectly configured health checks

## Console Lab

