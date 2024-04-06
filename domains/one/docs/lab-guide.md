# Lab & Demo Guide

## Prerequisites

1. Create an IAM role called "ec-web-server-deploy-role" which has a trust relationship with the AWS EC2 service.  Ensure the role has the following `Allow` permissions:
	- iam:CreateRole
	- iam:DeleteRole
	- iam:AttachRolePolicy
	- iam:DetachRolePolicy
	- iam:AddRoleToInstanceProfile
	- iam:RemoveRoleFromInstanceProfile
	- iam:DeleteInstanceProfile
	- iam:CreateInstanceProfile
	- iam:PassRole
	- ec2:DescribeInstances
	- ec2:RunInstances
	- ec2:TerminateInstances
	- ec2:DescribeSecurityGroups
	- ec2:CreateSecurityGroup
	- ec2:AuthorizeSecurityGroupIngress
	- ec2:RevokeSecurityGroupIngress
	- ec2:DeleteSecurityGroup

## Suggested Learning Path

1. [CodeCommit console lab](./code-commit.md#console-lab)
1. [CodeDeploy console lab](./code-deploy.md)