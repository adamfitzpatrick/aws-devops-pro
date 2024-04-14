# ECS

AWS Elastic Container Service is a scalable service to manage containerized applications in a
cluster.  The service is fully integrated with AWS tools such as Elastic Container Registry as well
as third-party tools such as Docker.  It is designed to allow teams to focus more on building
applications instead of the environments in which they run.

## Service Layers

- Capacity: the infrastructure in which your containers run, such as EC2, Fargate or an on-premises
compute solution.
- Controller: AWS ECS scheduler that manages your applications
- Provisioning: AWS management console, AWS CLI, SDKs, Copilot or AWS CDK

## AWS ECS Application Lifecycle

- ECR: contains the container image
- Task definition: defines the application
- Scheduler deploys application as task or service
- Manage and monitor application

## ECS Demos

TODO learn about ECS