# CodeArtifact

## AWS Documentation

*Source: [Introduction to DevOps on AWS (AWS CodeArtifact)](https://docs.aws.amazon.com/pdfs/whitepapers/latest/introduction-devops-aws/introduction-devops-aws.pdf#aws-codeartifact)*

AWS CodeArtifact is a fully managed artifact repository service that can be used by organizations
to securely store, publish, and share software packages used in their software development
process. CodeArtifact can be configured to automatically fetch software packages and
dependencies from public artifact repositories so developers have access to the latest versions.

Software development teams increasingly rely on open-source packages to perform common tasks
in their application package. It has become critical for software development teams to maintain
control on a particular version of the open-source software to ensure the software is free of
vulnerabilities. With CodeArtifact, you can set up controls to enforce this.

CodeArtifact works with commonly used package managers and build tools such as Maven, Gradle,
npm, yarn, twine, and pip, making it easy to integrate into existing development workflows.

## Required Knowledge

- Works with Maven, Gradle, npm, yarn, twine, pip and NuGet
- All artifacts reside within the VPC
- Define domains which uniquely identify a set of repositories
    - Distinct from DNS domains such as those in Route53
- Can serve as a proxy for public repos, like npm, pip, NuGet and Maven
    - Provides caching for public repos
- Possible to push own artifacts
- CodeBuild can pull from CodeArtifact
- Provides EventBridge integration
- Security
    - Resource policies used to provide another account access to CodeArtifact
        - Such a principal can either read all the packages in repo or none of them
    - Repository auth tokens have a validity period between 15 minutes to 12 hours
- Upstream repos
    - Can have multiple upstream repos
    - Allows package manager client to access artifacts that are spread across multiple repos, but
    consumer only needs to access a single repository
    - Each repo can have up to 10 upstream repos
    - Each repo can only have one external connection, such as NPM
        - Example:
            - Repo A has external connection to NPM
            - Call to get `rimraf` package from Repo A
            - `rimraf` cannot be found, so the call is forwarded to NPM
            - `rimraf` is cached in Repo A
            - Repos B, C & D can all use A as an upstream, thereby gaining access to NPM
    - Retention
        - If requested package version is found in upstream repo, a reference is retained and always
        available from downstream repo
        - Retrained package version is not affected by changes to the upstream repo, such as 
        deleting or updating the package
        - Intermediate repos do not keep the package, only the repo that received the package
        request and the repo that connects to the external repo
- Domains
    - Deduplicated storage: asset only stored once in domain even if it is available in many repos
    (pay only once for storage)
    - Fast copying: only metadata records are updated when packages are pulled from an upstream
    CodeArtifact repo into a downstream one.
    - Easy sharing: same metadata, assets and KMS key
    - Apply policy across multiple repos
        - Can restrict which accounts have access to repos in domain
        - Can control who can config connections to public repos

## Console Lab

1. In CodeArtifact, click on "Create Repository".  In the "Repository" section, name the repo
"demo-repo". Select connections to two public repos: NPM and maven-central-store. Click "Next".
1. Select "This AWS account". Most likely, there are no CodeArtifact domains in your account.  Create one, and you'll see the name prepended to your Domain URL. Click "Next"
1. One the next page, you get a graphical layout of your repo configuration.  Note that you were
able to select two external repos to which to connect.  AWS handled this by setting up two
automatically generated repos upstream of demo-repo, each of which connect to the external store.
As such, the rule of each repo only having a single public connection is maintained. Scroll down to
the bottom and click "Create Repository.
1. Click on "View Connection Instructions", and select a client to access the NPM repository.
Follow the instructions to access the repo, and make a request for an NPM package, such as `rimraf`
or `lodash`. Verify that the package is installed locally, and then return to your repo in the
console. You should be able to see the package listed in your repo, as it has been cached there.
1.Click on "View Connection Instructions" again, and select a client to access maven. Follow the
instructions as before, add a dependency to a Java project, and perform `mvn install`.  Return to
the console and verify that the dependencies you fetched now appear in the repository.