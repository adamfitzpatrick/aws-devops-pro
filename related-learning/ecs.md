# ECS

AWS Elastic Container Service is a scalable service to manage containerized applications in a
cluster.  The service is fully integrated with AWS tools such as Elastic Container Registry as well
as third-party tools such as Docker.  It is designed to allow teams to focus more on building
applications instead of the environments in which they run.

## Tasks

A *task* in ECS is a JSON file that describes the parameters and one or more containers that form an application.

Some of the parameters that can be specified are as follows:

- The launch type to use
- The docker image to use
- How much CPU and memory to use with each task or container within a task
- Memory and CPU requirements
- OS of the container
- Docker networking mode to use for containers
- Logging config to use
- Whether the task continues to run if the container finishes or fails
- Command the container runs when it is started
- Any data volumes to be used with containers in the task
- IAM role for tasks to use

After a task definition is created, it is possible to run it as a task or service.  There is a distinction between running it as a *task* or as a *service*:

- *Tasks* are the instantiation of task definitions within a cluster.  You can specify the number of tasks to run on your cluster
- *Services* run and maintain your desired number of tasks simultaneously in an ECS cluster.  If any of your tasks fail or stop, ECS service scheduler launches another instance based on your task definition, thereby maintaining your desired number of tasks in the service

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

## AWS ECS Workshop

https://ecsworkshop.com/