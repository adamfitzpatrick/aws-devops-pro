# EC2 Image Builder

## AWS Documentation

*Source: [Introduction to DevOps on AWS (EC2 Image Builder)](https://docs.aws.amazon.com/pdfs/whitepapers/latest/introduction-devops-aws/introduction-devops-aws.pdf#ec2-image-builder)*

EC2 Image Builder is a fully managed AWS service that helps you to automate the creation,
maintenance, validation, sharing, and deployment of customized, secure, and up-to-date Linux or
Windows custom AMI. EC2 Image Builder can also be used to create container images. You can use
the AWS Management Console, the AWS CLI, or APIs to create custom images in your AWS account.

EC2 Image Builder significantly reduces the effort of keeping images up-to-date and secure by
providing a simple graphical interface, built-in automation, and AWS-provided security settings.
With EC2 Image Builder, there are no manual steps for updating an image nor do you have to build
your own automation pipeline.

## Required Knowledge

- Service used to automate creation, maintainence, validation and testing AMIs
    - Runs on a schedule
    - No charge other than those associated with active instances
    - Publishes to multiple regions and accounts
- CICD architecture
    - Pipeline:
        - CodeCommit: developer pushes new image configuration
        - CodeBuild: Assembles image specifications
        - CloudFormation calls EC2 image builder, which creates a new AMI
        - second CloudFormation stage then performs rolling updates of Auto
        Scaling Groups with the new AMI
- Can use AWS Resource Access Manager to share images, recipes, and components across accounts
or througout organizations
- Can track latest AMIs in SSM Parameter Store with workflow:
    - EC2 Image Builder -> EventBridge to SNS -> invoke Lambda -> lambda stores ID in SSM param
    store