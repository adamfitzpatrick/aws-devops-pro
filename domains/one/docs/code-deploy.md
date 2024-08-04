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
- Components
    - Applications
        - A specific application to be deployed, which wraps around other elements such as the
        Deployment Groups and Deployments
    - Deployment Group
        - A way to define a specific subset of deployment methods for the application, such as
        specific instance tags, deployment configuration, service roles, triggers, alarms and
        rollback configs
    - Deployment Configuration
        - Specified number of instances that must remain available at any time during deploy
        - Can use pre-defined deployment configs
            - CodeDeployDefault.AllAtOnce
            - CodeDeployDefault.HalfAtATime
            - CodeDeployDefault.OneAtATime
        - Can also create custom deploy config
    - Deployment
        - A single attempt to deploy from a deployment group
    - Application Configuration
- Can automate rollbacks for failed deployments or trigger CloudWatch alarms
- Can leverage gradual deployment strategies such partial deploys, canary and blue-green
- Hooks permit scripts to be run at various stages of deploy process, but not all stages are
available for each type of deploy
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
                ApplicationStop-->DownloadBundle-->BeforeInstall-->Install-->AfterInstall
                AfterInstall-->ApplicationStart-->ValidateService
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
                - When identified by ASG, any new instances will receive thedeploy
            - When a load balancer in use, traffic is stopped to the instances being
            updated
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
                - BlueGreenDeploymentConfiguration.BlueInstanceTerminationOption object in
                CodeDeploy API
                    - properties
                        - action
                            - TERMINATE: instances are terminated after specified wait time
                            - KEEP_ALIVE: instances are left running after being deregistered from
                            the load balancer and removed from deployment group
                        - terminationWaitTimeInMinutes
                            - number of minutes to wait after successful deploy before carrying out
                            the action property
            - Hooks are same as for in-place deploy, but execute differently based on whether they
            are blue or green instances
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
    - Permissions
        - Both CodeDeploy and target EC2 instances require specific permissions for successful
        deployment
        - instances must have an Allow policy for
            - `s3:GetObject`
            - `s3:GetObjectVersion`
            - `s3:ListBucket`
        - CodeDeploy can use the managed policy "AWSCodeDeployRole", which permits a number of
        actions in `autoscaling`, `ec2`, `cloudwatch`, and `elasticloadbalancing`
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
        - Since lambda can be asynchronous, need to make a call to
        PutLifecycleEventHookExecutionStatusCommand to tell CodeDeploy when the hook lambda has
        completed
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
            - Allows new version to be verified with subset of traffic, then released to all traffic
        - AllAtOnce
            - Shift all traffic to new version simultaneously
    - example (CodePipeline):
        
        ```mermaid
        flowchart TB
        push(dev pushes changes to repo)-->cc(source stage pulls from CodeCommit)
        cc-->cb(CodeBuild deploys new function version\nand places appspec.yml file in S3)
        cb-->cd(CodeDeploy updates alias and shifts traffic)
        ```

- ECS
    - Automates deployment of new ECS task definition
    - No CodeDeploy agent is required
    - only permits blue/green deployments
        - Initial deployment to green
        - Developer must create new task definition, publish container images and create appspec.yml
        file
        - Can use Canary, Linear or AllAtOnce strategies just like Lambda deployments
    - Requires your ECS-based application to include a Load Balancer with at least one production
    listener and at least two Target Groups.
    - Stages

        ```mermaid
        flowchart TB
        BeforeInstall-->Install-->AfterInstall-->AllowTestTraffic-->AATT[AfterAllowTestTraffic]
        AATT-->BeforeAllowTraffic-->AllowTraffic-->AfterAllowTraffic
        ```
        
    - Hooks
        - All scripts must be lambda functions
        - Permitted for `BeforeInstall`, `AfterInstall`, `AfterAllowTestTraffic`,
        `BeforeAllowTraffic`, `AfterAllowTraffic`
    - appspec.yml example

        ```yaml
        version: 0.0
        Resources:
            - TargetService:
                Type: AWS::ECS::Service
                Properties:
                    TaskDefinition: |
                      “arn:aws:ecs:aws-region:aws-account:task-definition/ecs-task-def-name:revision-number”
                    LoadBalancerInfo:
                    ContainerName: “whatevs”
                    ContainerPort: 9999
        ```

    - Example (CodePipeline)

        ```mermaid
        flowchart TB
        push(dev pushes changes with ECR image definition and appspec.yml)
        push-->cc(source stage pulls from CodeCommit)
        cc-->cb(CodeBuild pushes ECR image, creates ECS\ntask definition, places appspec in S3)
        cb-->cd(CodeDeploy uses appspec file as input artifact)
        ```

- Redeploy & Rollbacks
    - Can be
        - Automatic (when deploy fails or when CloudWatch threshold is met)
        - Manual
        - Disabled
    - When a rollback occurs, the last known good revision is deployed as a *new deployment*.
- Common Troubleshooting
    - AccessDenied during artifact download
        - Most likely the proper permissions are not available on instances
    - CodeDeploy agent was not able to receive the lifecycle event
    - InvalidSignatureException: data and time on EC2 instances must match signature date of the
    deploy request
    - When the deploy, or when all lifecycle events are skipped in EC2/on-prem deployments, and one
    of the following errors is shown:
        - `Overall deployment failed because too many individual instances failed deployment`
        - `Too few healthy instances are available for deployment`
        - `Some instances in your deployment group are experiencing problems (Error code: HEALTH_CONSTRAINTS)`
        - The source may be
            - CodeDeploy Agent might not be installed, running or reachable
            - CodeDeploy service role or IAM instance profile might not have required permissions
            - You're using HTTP proxy, and didn't configure CodeDeploy Agent with `:proxy_uri:`
            parameter
            - Date and time mismatch between CodeDeploy and agent(s)
    - Deploying to an ASG during a scale-out event may result in some instances in the ASG having
    out-dated versions
        - CodeDeploy automatically starts a follow-on deploy to update any outdated EC2 instances
        - For scale-in events, CodeDeploy allows you to configure a "Termination Deployment", which
        means that CodeDeploy does not actually deploy anything to instances that are being terminated
        by the scale-in, but maintains lifecylce hooks to enable script executions
    - Blue/Green failing `AllowTraffic`
        - ELB may have incorrectly configured health checks

## Console Lab

### Create and Deploy an EC2 application

1. You'll need to create two roles, one for EC2 and one for CodeDeploy:
    - Role for ec2 instances (`ec2-role-for-codedeploy`)
        - Trust relationship: Service ec2.amazonaws.com
        - Managed policies:
            - AmazonEC2RoleforAWSCodeDeploy
            - AmazonSSMManagedInstanceCore (so we can access the instance w/o a key pair)
    - Role for CodeDeploy (`codedeploy-to-ec2-role`)
        - Trust relationship: Service codedeploy.amazonaws.com
        - Managed policy: AWSCodeDeployRole
When you create these roles, it is worth taking a moment to review the permissions granted by the
attached policies.
1. Launch an EC2 instance using the following settings:
    - tagged with the key "codedeploy-target"
    - Amazon Linux 2023 AMI with 64-bit architecture
    - t2.micro type
    - Proceed without a key pair
    - Security group permitting HTTP and HTTPS (for session manager) traffic from the internet.
    *Do not open the group to port 22*
    - Leave "Configure Storage" at default settings
    - Under "Advanced", leave everything default except "IAM instance profile": in this field,
    select the `ec2-role-for-codedeploy` you created above.
If you navigate to the public IP address for this instance, you will see that it is not reachable.
1. Review the appspec file in [code-deploy-ec2 lab folder](../labs/code-deploy-ec2/).  Note that
CodeDeploy will copy website content to the standard httpd distribution folder, and will run the
bash script in codedeploy-before-allow-traffic.sh during the AfterInstall lifecycle stage.  Zip the
contents of the lab folder and place the zipfile in an S3 bucket of your choosing. *NOTE: THE BUCKET
MUST BE IN THE SAME REGION AS YOUR CODEDEPLOY APPLICATION.*
1. Navigate to CodeDeploy, and click on "Create application".  Enter a catchy name for the app, and
select "EC2/On-premises" for the compute platform. At the bottom, click "Create application".
1. On the application details page, click "Create deployment group", and give the group a suitable
name. For service role, use the `codedeploy-to-ec2-role` you created above. Leave "Choose how to
deploy your application" set to "In-place". Under "Environment configuration" select "Amazon EC2
instances", and enter "codedeploy-target" as a tag. Leave "Agent configuration with AWS Systems
Manager" alone, and leave "Deployment configuration" set to "AllAtOnce". Uncheck "Enable load
balancing". At the bottom, click "Create deployment group".
1. Click "Create deployment". Ensure the deployment group your created above is selected, and that
"Revision type" is set to "My application is stored in Amazon S3". Enter the S3 URI for the zip file
you uploaded above.  The revision type should default to "zip" based on the URI you entered. At the
bottom, click "Create deployment". Wait for the deployment to finish.  Navigate to the public URL
for your EC2 and verify that the website is now available.

### Perform a blue/green deployment into EC2

1. Add an inline policy to the `codedeploy-to-ec2-role` you created above:
    - Allow:
        - `iam:PassRole`
        - `ec2:RunInstances`
        - `ec2:CreateTags`
1. Create an EC2 launch template which matches the EC2 instance you created in the last section.
*Do not forget to include the `ec2-role-for-codedeploy` IAM role in the template.*
Create an auto scaling group which uses this launch template.  During ASG creation, choose to create
a new Load Balancer, and configure an Application Load Balancer which targets the existing EC2
instance. Verify the load balancer is functioning by navigating to its public address; you should
see the same simple web page displayed at the end of the previous section.
1. Make a modification to the [index.html](../labs/code-deploy-ec2/index.html) file inside the
[code-deploy-ec2 lab folder](../labs/code-deploy-ec2/) which you will be able to see in the web site.
Zip up the new package and deploy it over the original zip file in S3.
1. Create a new deployment group in CodeDeploy. Give the new deployment group a suitable name, and 
attach the `codedeploy-to-ec2-role` IAM role to it.  Select the "Blue/green" deployment type, 
and leave "Automatically copy Amazon EC2 Auto Scaling group" selected. Note the termination hook
that can be used to handle scale-in events during deployment, but otherwise leave it alone. Modify
the termination time from 1 hour to 5 minutes. Select "Application Load Balancer" under Load
Balancer.  Click "Create deployment group".
1. Click on "Create deployment", and copy the S3 URI for the zip file as you did in the previous
section.  Select "Overwrite the content" under "Additional deployment settings", then click "Create
deployment".  Observe that the application remains available through the load balancer while the new
instances are started. After traffic to the old instance has been stopped, reload the web page to
view your changes. Observe the deploy process and note that the new instance(s) come up, and a
short time later the old instances are terminated.

### Deploy an application to Lambda

1. Create a new IAM role for CodeDeploy (`codedeploy-for-lambda`):
    - Trust relationships: codedeploy.amazonaws.com
    - Managed policy: AWSCodeDeployRoleForLambda
1. Create a Node.js 20.x function in AWS Lambda named "demo-lambda" and copy the contents of
[index.mjs](../labs/code-deploy-lambda/index.mjs) from the
[code-deploy-lambda lab folder](../labs/code-deploy-lambda/) to the function code. Publish the
function, and create an alias called `dev` to point to the published version.
1. Make a small change to the lambda function code, and publish the updated code to a new version.
1. Examine the [appspec](../labs/simple-lambda-function/appspec.yml) file for this lab. Ensure the
Name field matches the name of your AWS function. Note the Alias version (`dev`), CurrentVersion (1)
and TargetVersion (2).  Your function alias should currently be pointing to version 1, and
CodeDeploy will handle the transition.  Upload the appspec file to an S3 bucket (in the same region
as your deployment), and ensure it is nested under an S3 "folder" named "CodeDeploy" (in other
words, the object key should be "CodeDeploy/**/appspec.yml"). Ensure that the appspec.yml object in
S3 has the following tag/value pair:

    ```
    UseWithCodeDeploy: true
    ```

This placement and tagging is due to the way the AWSCodeDeployRoleForLambda is configured.
I encourage you to check it out.
1. In lambda, select the `dev` alias, and run a test to verify that the response
is from version 1.
1. In CodeDeploy, create a new application, name it and select AWS Lambda as the
compute platform. Click on "Create application". Create a deployment group, and
assign it the `codedeploy-for-lambda` role you created above. Under deployment
configuration, click "Create deployment configuration", call the new config
something sensible, and select "Canary" type. For step, enter 50, and 5 for
interval; this means the deployment will shift 50% of traffic to the new version
for the first 5 minutes post-deployment, and then all will go to the new version.
*Note: you'd never want to send half your traffic to a new version when doing
a canary deployment, but this is a good way to demonstrate the effect.* Click
"Create deployment configuration". Select your new deployment config from the 
Deployment configuration dropdown menu, then click "Create deployment group".
1. Click on "Create deployment", and enter the S3 object URI for the appspec
file you uploaded above, and select `.yaml` from the revision file type dropdown.
Scroll down and click create deployment. Wait a moment for the deployment to
complete 50% of the traffic shifting phase. Go back to the function, and test
the alias several times.  You should not approximately half the responses
come from the original function version, and half come from the updated version.
1. Wait for the deployment to complete fully, and test the function again.
Now all responses come from the new function.

### Use Hooks During Lambda Deployment

1. Create an IAM role for a new lambda function:
    - Trust relationship: lambda.amazonaws.com
    - Statement 1:
        - Allows: logs.CreateLogGroup
        - Resource: arn:aws:logs:<region>:<account>:*
    - Statement 2:
        - Allows:
            - logs.CreateLogStream
            - logs.PutLogEvents
        - Resource: arn:aws:logs:<region>:<account>:log-group:/aws/lambda/CodeDeployHook_before-allow-traffic:*
    - Statement 3:
        - Allows:
            - lambda:UpdateFunctionConfiguration
        - Resource: arn:aws:lambda:<region>:<account>:function:demo-lambda
    - Statement 4:
        - Allows:
            - codedeploy:PutLifecycleEventHookExecutionStatus
        - Resource: *
Replace <region> and <account> above with your own values.  This is nearly identical to the default
execution role that lambda automatically creates for a new function, with the addition of statement 3.
1. Create a second lambda function running Node.js 20.x, name it "CodeDeployHook_before-allow-traffic"
and assign the role you created above as the execution policy. Copy the code from
[CodeDeployHook_before-allow-traffic.mjs](../labs/code-deploy-lambda/CodeDeployHook_before-allow-traffic.mjs)
into the function. Examine the code for this function to note a couple of things:
    - This function is updating the Environment for the original lambda, and specifically targets
    version 3 to do so.
    - The new function calls CodeDeploy to update it on the lifecycle hook status
1. Note that, in the [index.mjs](../labs/code-deploy-lambda/index.mjs) code, the response includes
an ENVIRONMENT_VAR, but no value is returned because the environment variable is not set.  In this
lab, we use the BeforeAllowTraffic hook to set an environment variable for the function. Modify the
[appspec.yml file](../labs/code-deploy-lambda/appspec.yml) as follows:

    ```yaml
    ...
          CurrentVersion: 2
          TargetVersion: 3
    Hooks:
      - BeforeAllowTraffic: CodeDeployHook_before-allow-traffic
    ```
Make a modification to demo-lambda, and publish the changes to version 3.  Upload the updated
appspec file to the same location as before, and re-run the deployment. Once traffic has begun
shifting, test the function with the "dev" alias.  Note that the function will return the
environment value only for the most recent function deploy, and that test responses will oscillate
between the most recent and previous deployments until the full transition has been completed.

### Deploying to ECS with CodeDeploy

1. You'll need an appropriate role for allowing CodeDeploy to update ECS:
    - CodeDeploy ECS role (`codedeploy-ecs-role`)
        - Trust relationship: codedeploy.amazonaws.com
        - Managed policy: AWSCodeDeployRoleForECS
    - ECS task execution role (`ecs-task-execution-role`):
        - Trust relationship: ecs-tasks.amazonaws.com
        - Managed policy: AmazonECSTaskExecutionRolePolicy
1. Create a public ECR repository, name it "codedeployecslab/apache", and click "Create repository"
1. From within the `labs/code-deploy-ecs` folder, build the docker image:

    ```bash
    docker build -t codedeployecslab/apache .
    ```
Note that if you are building the image on a Mac, you will need to tell it you are on an AMD64
platform:

    ```bash
    docker buildx build --platform=linux/amd64 -t codedeployecslab/apache .
    ```
Follow the push instructions associated with the ECR repository you created above to push the image.
1. Deploy ECS infrastructure required for the lab with the included CloudFormation template:

    ```bash
    aws cloudformation create-stack --stack-name CodeDeployEcsDemo --template-body file://<relative_path_to_template_file> --parameters '[{"ParameterKey":"ContainerImageUri","ParameterValue":"<uri_for_your_image>"}]' --capabilities CAPABILITY_NAMED_IAM
    ```

1. Once the infrastructure is deployed, navigate to the Load Balancer created by the CloudFormation
template, copy the DNS name, and paste it into a new browser tab.  You should see "Greetings from
ECS Fargate, version 1!!".
1. Modify `labs/code-deploy-ecs/index.html` so the `h1` tag indicates this is "version 2", rebuild
the docker image, then tag it with "2" and push the new image to ECR.
1. Create a new revision under the existing task definition created by CloudFormation.  The only
is that it should reference your ECR image tagged "2".
1. Create a new application in CodeDeploy, give it a suitable name and select "Amazon ECS" as the
compute platform
1. Create a deployment group with a catchy name and select the `codedeploy-ecs-role` role for the
Service role. Under "Environment configuration", select the `codedeploy-ecs-demo-cluster`, and the
service with a name like `CodeDeployEcsDemo-EcsService`. Select the `CodeDeployEcsDemoAlb` for your
load balancer, and select the only production listener and target group available with this ALB.
1. Under "Deployment Settings", click on "Create deployment configuration", give  your new
deployment configuration a catchy name like "HalfAndHalf", select "Linear" as the type, enter "50"
for the step value, and pick something short like 1 minute for the interval, so you can see the
transition in action. Click "Create deployment configuration", and then make sure your new
deployment configuration is selected for your new deployment group. Click "Create deployment group".
1. Click "Create deployment".  Your new deployment group should already be selected.  Select "Use
AppSpec editor", and select "YAML". Copy the contents of the `appspec.yml` file in this lab folder,
and paste them into the AppSpec editor. Scroll down and click "Create deployment".
1. Monitor the deployment, and when 50% has been deployed, reload the load balancer DNS name a few
times.  You should see the application version oscillate between 1 and 2.  After the deployment
finishes, it will settle on version 2.


#### TODO Complete ECS CodeDeploy lab

### Using Hooks During ECS Deployment