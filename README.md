# AWS DevOps Professional Certification

This repository contains documentation and hands-on labs and demos useful in studying for the AWS DevOps Pro certification.

The repo is organized to give overviews of each of the AWS services evaluated in the exam, as well as demonstration packages showing how services and be combined and leveraged to fully automate the development process.

## Domains

- [Domain One: SDLC Automation](./domains/one/README.md)
- Domain Two
- Domain Three
- Domain Four
- Domain Five
- Domain Six

## A Note on CloudFormation & AWS CDK

Although CloudFormation and AWS CDK are specifically covered under Domain Two, service-specific documentation and study guides within this repository may include CloudFormation & CDK details relevant to the service in question, such as Resource Types, Properties, and Classes. Additionally, the deployable applications in this repo rely heavily upon AWS Cloud Development Kit for rapid configuration and deployment of required infrastructure.

Please refer to the appropriate parts of Domain Two for background on this information.

## Labs and Demonstrators

Please note that labs and demos in this repository are centered around the Node.js ecosystem.  Scripts and deployments have been evaluated using the following software versions: 

- **Node.js** v20.x.x
- **npm** v10.x.x
- **yarn** v4.x.x
- **AWS CDK** v2.130.x

### Account Prep 

Most of the demos in this repo are configured and deployed using AWS CDK.  To use this, your AWS account must first be "bootstrapped":

```bash
cdk bootstrap aws://<ACCOUNT-NUMBER>/<REGION>
```

or, if you have configured a default account and region:

```bash
cdk bootstrap
```