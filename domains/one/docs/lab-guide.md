# Lab & Demo Guide

## Prerequisites

This course assumes that you are able to do many tasks in the AWS console without instruction.
These include the following:

- Create users, roles and policies in IAM
- Launch EC2 instances
- Create S3 buckets, and upload and download objects to and from them
- View logs and metrics in CloudWatch
- Create ALBs and register targets
- Create, monitor and test a function in AWS Lambda

## Tagging

Tagging is a great way to keep track of resources that you create and use for training and labs.
This allows you to easily delete resources that are no longer needed to keep your training
costs low without interfering with resources that are needed longer term.  I always encourage you
to create a specific AWS account for training, but that doesn't necessarily mean you want to
delete all the resources in an account once a single course or lab is completed.  With tags, you can
easily run multiple training courses in a single account, possibly saving the time and effort
needed to create oft-used resources, while deleting resources that are not longer needed for a
specific course. For example, I often tag resources with *purpose = training* when I know I will
want to clean up the resources in an account after I finish a course or training goal.

## Suggested Learning Path

1. [CodeCommit console lab](./code-commit.md#console-lab)
1. [CodeBuild console lab](./code-build.md#console-lab)
1. [CodeArtifact console lab](./code-artifact.md#console-lab)
1. [CodeDeploy console lab](./code-deploy.md#console-lab)