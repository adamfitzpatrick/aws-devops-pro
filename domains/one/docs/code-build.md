# CodeDeploy

## AWS Documentation

*Source: [Introduction to DevOps on AWS (AWS CodeBuild)](https://docs.aws.amazon.com/pdfs/whitepapers/latest/introduction-devops-aws/introduction-devops-aws.pdf#aws-codebuild)*

AWS CodeBuild is a fully managed continuous integration service that compiles source code, runs
tests, and produces software packages that are ready to deploy. You don’t need to provision,
manage, and scale your own build servers. CodeBuild can use either of GitHub, GitHub Enterprise,
BitBucket, AWS CodeCommit, or Amazon S3 as a source provider.

CodeBuild scales continuously and can process multiple builds concurrently. CodeBuild offers
various pre-configured environments for various versions of Microsoft Windows and Linux.
Customers can also bring their customized build environments as Docker containers. CodeBuild
also integrates with open source tools such as Jenkins and Spinnaker.

CodeBuild can also create reports for unit, functional, or integration tests. These reports provide
a visual view of how many tests cases were run and how many passed or failed. The build process
can also be run inside an Amazon Virtual Private Cloud (Amazon VPC) which can be helpful if your
integration services or databases are deployed inside a VPC.