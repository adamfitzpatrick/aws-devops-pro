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