# Domain One: SDLC Automation

22% of exam content

## Relevant Services

*Source: [Introduction to DevOps on AWS](https://docs.aws.amazon.com/pdfs/whitepapers/latest/introduction-devops-aws/introduction-devops-aws.pdf)*

The tools presented here and tested on the certification exam are designed to introduce consistency and stability to the development process while reducing errors by automating as many steps as possible.

- [CodeCommit](./docs/code-commit.md) Provides git-based version control of source code which integrates smoothly with AWS IAM and the other tools listed here
- [CodePipeline](./docs/code-pipeline.md) Visual workflow orchestrate continuous integration and continuous delivery of software
- [CodeBuild](./docs/code-build.md) Fully-managed continuous integration service that integrates tightly with AWS developer tools as well as third-party tools
- [CodeDeploy](./docs/code-deploy.md) Fully-managed service that automates deployment to a variety of compute services, including AWS EC2, AWS Fargate, AWS Lambda, and on-premises servers
- [CodeArtifact](./docs/code-artifact.md) Managed artifact repository service that can be used to store, publish and share software packages
- [CodeGuru](./docs/code-guru.md) Dev tool that provides "intelligent recommendations" to improve code and identify an application's most expensive lines of code
- [EC2 Image Builder](./docs/ec2-image-builder.md) Managed service to automate the creation, maintenance, validation, sharing and deployment of customize, secure and up-to-date Linux and Windows AMIs
- [Amplify](./docs/amplify.md) Web and mobile app development tool that provides a flexible collection of modular cloud services and libraries for full-stack development

## Required Knowledge

### 1.1 Implement CI/CD Pipelines
- Software development lifecycle concepts, phases and models
  - Stages
  - Methodologies
    - Linear sequential (waterfall)
    - Verification model
    - Prototype model
    - Agile Model
    - Big bang model
  - CI/CD is an enhancement of SDLC to speed things up and make more reliable; fail fast
    - CI: regular fast check ins
    - Continuous Delivery: Deployment to “production-like” environments for testing.  May proceed to production, but there is always a manual step to prevent final deploy to prod
    - Continuous Deployment: Fully automated regular deployments to production w/o human intervention
  - Pipeline deployment patterns for single- and multi-account environments

#### Skills

- Configuring code, image, and artifact repos
- Using version control to integrate pipelines with application environments
- Setting up build processes
- Managing build and deployment secrets
- Determining appropriate deployment strategies

## Software Development Lifecycle

*Source: [What is SDLC?](https://aws.amazon.com/what-is/sdlc/)*

AWS defines the SDLC as the cost-effective and time-efficient process that development teams use to build **high quality software**.  They outline a number of different steps as a basic framework for an effective SDLC:

- **Plan** Define requirements, and develop models, goals, timelines, cost-benefit analysis, etc
- **Design** Decide on architecture, features, infrastructure, user experience, user interface, security, etc
- **Implement** Create source code and supporting material, such as documentation
- **Test** Combine automated and manual testing to identify bugs, deficiencies and performance issues with the application.  This stage should ideal be completed in parallel with the **Implement** phase and may not be considered distinct (consider TDD and BDD)
- **Deploy** Applications should be deployed continually to development environments for testing and production environments for consumers to use
- **Maintain** Iterate on this process, improving source code and testing procedures, and seeking uniformity and consistency in the on-going process
