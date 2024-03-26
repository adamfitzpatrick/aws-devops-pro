# CodeCommit

## AWS Documentation

*Source: [Introduction to DevOps on AWS (AWS CodeCommit)](https://docs.aws.amazon.com/pdfs/whitepapers/latest/introduction-devops-aws/introduction-devops-aws.pdf#aws-codecommit)*

AWS CodeCommit is a secure, highly scalable, managed source control service that hosts private
git repositories. CodeCommit reduces the need for you to operate your own source control system
and there is no hardware to provision and scale or software to install, configure, and operate.
You can use CodeCommit to store anything from code to binaries, and it supports the standard
functionality of GitHub, allowing it to work seamlessly with your existing Git-based tools. Your
team can also use CodeCommit’s online code tools to browse, edit, and collaborate on projects.

AWS CodeCommit has several benefits:

- **Collaboration** AWS CodeCommit is designed for collaborative software development. You
can easily commit, branch, and merge your code, which helps you easily maintain control of your
team’s projects. CodeCommit also supports pull requests, which provide a mechanism to request
code reviews and discuss code with collaborators.
- **Encryption** You can transfer your files to and from AWS CodeCommit using HTTPS or SSH,
as you prefer. Your repositories are also automatically encrypted at rest through AWS Key
Management Service (AWS KMS) using customer-specific keys.
- **Access control** AWS CodeCommit uses AWS Identity and Access Management (IAM) to control
and monitor who can access your data in addition to how, when, and where they can access it.
CodeCommit also helps you monitor your repositories through AWS CloudTrail and Amazon
CloudWatch.
- **High availability and durability** AWS CodeCommit stores your repositories in Amazon Simple
Storage Service (Amazon S3) and Amazon DynamoDB. Your encrypted data is redundantly
stored across multiple facilities. This architecture increases the availability and durability of your
repository data.
- **Notifications and custom scripts** You can now receive notifications for events impacting
your repositories. Notifications will come as Amazon Simple Notification Service (Amazon SNS)
notifications. Each notification will include a status message as well as a link to the resources
whose event generated that notification. Additionally, using AWS CodeCommit repository cues,
you can send notifications and create HTTP webhooks with Amazon SNS or invoke AWS Lambda
functions in response to the repository events you choose.

## Required Knowledge

- Git-based version control
- No repo size limit
- As with most services, they emphasis "fully managed" and "high availability"
- [Security](https://docs.aws.amazon.com/codecommit/latest/userguide/security-iam.html)
    - Provides private repos only, as access is controlled by IAM
    - Can authenticate with SSH keys or HTTPS using AWS CLI credential helper or git credentials for IAM user
    - Credentials are generated within IAM and are distinct from other credentials such as Access Keys or console access
        - Possible to provide cross account access without sharing SSH keys or AWS creds via an IAM Role with AWS STS
    - Encryption
        - At rest using AWS KMS
        - In transit using HTTPS or SSH
    - Authorization
        - Can restrict changes to specific branches via IAM policies
        - Resource policies are *not* currently supported
- Integrates with AWS CI tools as well as third party tools
    - CodeBuild
    - CodeDeploy
    - CodePipeline
    - EventBridge
        - Can monitor and respond to all CodeCommit events via EventBridge
    - Jenkins
    - others
- Console UI is relatively minimal because it is intended for use from the command line like most git-based tools
- Pull Request
    - Can specify pool of principals that are permitted to approve
    - Can specify minimum number of acceptances for merging
    - Approval templates can be created and applied to specific branches
- Cross-region replication
    - CodeCommit repos are region-specific
    - Allows lower latency pulls for global developers
    - Permits repo backup
    - Use EventBridge with CodeCommit events
    
> Example:
>
> - RepoA in us-west-2 receives push
> - EventBridge captures the event and invokes an ECS Task
> - ECS Task replicates to RepoB in eu-west-1.

## Console Lab

### Repo Creation

1. Log into the AWS console if you haven't already, and navigate to CodeCommit.
1.  If you have no existing repos, you will likely see the AWS CodeCommit intro page. Otherwise, you will see a list of existing repositories. Click on "Create Repository" button available on either page.
1. Enter a suitable repository name
    - Must be unique by region
    - Case sensitive
    - Restricted to 100 alphanumeric, dash and underscores
    - Cannot end in .git
    - Cannot include any of the following: ``!?@#$%^&*()+-{}[]|/\><~`'";:``
1. Optionally add a description and tags
1. Click the "Additional configuration" drop down to view the AWS KMS key configuration.  Note there is no option to skip encryption.
1. Leave the "Enable Amazon CodeGuru Reviewer..." unchecked
1. Click "Create"
1. Once created, the repository page shows a tutorial on connection steps and requirements, as well as the repository content listing (currently empty)

### Generate Git Credentials

1. In the AWS console, navigate to IAM.
1. Select the your user entry
1. For SSH credentials:
    1. Under the "Security credentials" tab, scroll down to "SSH public keys for AWS CodeCommit" to upload your SSH public key.

    For HTTPS credentials:

    1. Under "HTTPS Git credentials for AWS CodeCommit" click "Generate credentials".  In this case, a pop-up will appear with the generated user name and password.  *Note that this is the only time you can view the password or download your credentials*.  
    1. Click close once you have recorded them.

### Use Identity Policies to Add Access

*Note: If your user account has certain privileges such as those provided by the **AdministratorAccess** policy, the policy to attach below will have no effect.*

1. Navigate to AWS IAM and then your user
1. Under the "Permissions" tab, click "Add permissions" then "Create inline policy.
1. Click the "JSON" button in the Policy editor.
1. Add the following policy, replacing `REGION`, `ACCOUNT_NUMBER` and `REPO_NAME` with the appropriate values:

    ```json
    {
        "Version": "2012-10-17",
        "Statement" : [
            {
                "Effect" : "Allow",
                "Action" : [
                    "codecommit:*"
                ],
                "Resource" : "arn:aws:codecommit:<REGION>:<ACCOUNT_NUMBER>:<REPO_NAME>"
            }
        ]
    }
    ```

1. Click "Next"
1. Give the policy a catchy name
1. Click on "Create Policy"

### Clone & Commit

1. Clone the repo using standard git commands.  The commands including the repo URL can be copied directly from the console.
1. Add content to the repo, commit the change and push.  You should be prompted for credentials if required.  It is possible to use the AWS CLI credential helper via `git config --global credential.helper '!aws codecommit credential-helper $@'` (Refer to the [credential-helper docs](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/codecommit/credential-helper/index.html)).
1.  Return to the AWS console and verify your changes have been pushed to the repo.

### Use Identity Policies to Restrict Access

*If you also have broader privileges such as those included with the **AdministratorAccess** policy, restricting access in this manner will not work.  You will need to remove the broader permissions first.*

1. Modify the IAM policy created above by changing "codecommit:*" to "codecommit:GitPull"
1. Make a local change to the repo, commit it, and attempt to push.
1. Verify that your push was rejected with a 403.

### Use Identity Policies to Protect Branches

1. Undo the changes made in the last section.
1. Modify the above policy to restrict pushing to the main branch by adding a new statement:

    ```json
    {
        "Effect": "Deny",
        "Action": [
            "codecommit:GitPush",
            "codecommit:DeleteBranch",
            "codecommit:PutFile",
            "codecommit:Merge*"
        ],
        "Resource": "arn:aws:codecommit:<REGION>:<ACCOUNT_NUMBER>:<REPO_NAME>",
        "Condition": {
            "StringEqualsIfExists": {
                "codecommit:References": [
                    "refs/heads/main"   
                ]
            },
            "Null": {
                "codecommit:References": "false"
            }
        }
    }
    ```

1. Make another change, commit it, and push (to the main branch)
1. Verify that your push was rejected with the message "You don't have permission to push changes to this branch"
1. Checkout a new branch named "dev", make your changes to the new branch, and push.
1. Verify that you can push changes to a non-main branch.
1. Verify that the new branch exists in CodeCommit

### Create an IAM User for Approvals

*Note: This is being done because it is not possible to approve your own pull request, either through the console or the CLI.  This step creates a user for the sole purpose of approving your work. You will need sufficient privileges to create a user in your account.*
  
*Note: REMOVE THE USER ACCOUNT WHEN YOU ARE DONE.*

1. In the AWS IAM console, navigate to Users.
1. Click "Create User"
1. Give the temporary user a clear name, and check the "Provide user access to the AWS Management Console" checkbox.
1. In the "Are you providing console access to a person?" box, ensure "I want to create an IAM user" is selected.
1. For simplicity, uncheck "Users must create a new password at next sign-in".
1. Click "Next"
1. In "Permissions options", select "Attach policies directly"
1. Attach the managed policy "AWSCodeCommitFullAccess"
1. Click "Next"
1. Click "Create user"
1. Record the user name and password

### Create an Approval Rule Template

1. In the AWS CodeCommit console, in the left sidebar, click on "Approval rule templates"
1. Click on "Create template"
1. Create a rule template name and optionally add a description
1. Enter 1 as the number of approvals required to merge a pull request
1. Click "Add approval pool member", and enter the name of the user you created in the previous section.
1. It is possible to add a branch filter (for example, if you only want to require the template to apply to PRs targeted to main)
1. In the "Associated repositories" drop down, select your repository. *Note: if you do not select any repositories, the approval template will not have any effect.*
1. Return to CodeCommit -> Repositories -> your repo.  Click "Create pull request".
1. Ensure the Destination is "main" and select "dev" from the Source dropdown.
1. Click "Compare"
1. Click "Create pull request"
1. Note the badge near the top of the resulting PR which says "0 of 1 rules satisfied", and also note that you cannot merge the PR.
1. Log out of the AWS console, and then log in again with the credentials of the user you created above.
1. Return to CodeCommit, navigate into your repo, and select "Pull requests" from the left sidebar.  You should see the PR you created above.
1. Review that PR, and click on "Approve".
1. Log back in using the credentials you used to create the PR.
1. Return to the PR in CodeCommit.
1. Note that a green "Approved" badge has replaced the "0 of 1 rules satisfied" badge, and that a "Merge" button is now available next to "Close pull request"