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

## Labs and Demonstrators

*Note: the deployable applications in this repo rely heavily upon AWS Cloud Development Kit for rapid configuration and deployment of required infrastructure.  This technology is described in detail as part of AWS DevOps Pro Domain Two, but can be used easily prior to reviewing that material.*

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