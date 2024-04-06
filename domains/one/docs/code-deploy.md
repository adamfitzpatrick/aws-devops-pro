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
        flowchart LR;
        BeforeBlockTraffic-->BlockTraffic;
        BlockTraffic-->AfterBlockTraffic;
        AfterBlockTraffic-->ApplicationStop;
        ApplicationStop-->DownloadBundle;
        DownloadBundle-->BeforeInstall;
        BeforeInstall-->Install;
        Install-->AfterInstall;
        AfterInstall-->ApplicationStart;
        ApplicationStart-->ValidateService;
        ValidateService-->BeforeAllowTraffic;
        BeforeAllowTraffic-->AllowTraffic;
        AllowTraffic-->AfterAllowTraffic;
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
            - Hooks
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
                - Green instances start at ApplicationStop because they initially have no traffice
                - Blue instances get BeforeBlockTraffic, BlockTraffic and AfterBlockTraffic


