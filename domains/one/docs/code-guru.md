# CodeGuru

## AWS Documentation

*Source: [Introduction to DevOps on AWS (AWS CodeGuru)](https://docs.aws.amazon.com/pdfs/whitepapers/latest/introduction-devops-aws/introduction-devops-aws.pdf#amazon-codeguru)*

Amazon CodeGuru is a developer tool that provides intelligent recommendations to improve
code quality and identify an application’s most expensive lines of code. Integrate CodeGuru
into your existing software development workflow to automate code reviews during application
development and continuously monitor application's performance in production and provide
recommendations and visual clues on how to improve code quality, application performance, and
reduce overall cost. CodeGuru has two components:

- **Amazon CodeGuru Reviewer** Amazon CodeGuru Reviewer is an automated code review
service that identifies critical defects and deviation from coding best practices for Java
and Python code. It scans the lines of code within a pull request and provides intelligent
recommendations based on standards learned from major open-source projects as well as
Amazon codebase.
- **Amazon CodeGuru Profiler** Amazon CodeGuru Profiler analyzes the application runtime
profile and provides intelligent recommendations and visualizations that guide developers on
how to improve the performance of the most relevant parts of their code.

## Required Knowledge

- ML powered service for automated code reviews and performance recommendations
- Two functions:
    - Reviewer
        - Automated static code analysis
            - Finds critical issues, security vulnerabilities, deviations from common
            best practices
            - Supports Java and Python
            - Secrets detector
                - Finds hardcoded secrets and suggests remediation
    - Profiler
        - Shows app performance during runtime
        - Makes recommendations
        - Finds what consumes excessive capacity and can identify inefficiencies
        - May help decrease compute costs
        - Shows heap summary
        - Anomaly detection
        - Supports apps running on AWS or on-prem
        - Minimal overhead
        - Can be applied to lambda functions
            - Decorator `@with_lambda_profiler`
            - Or use console to enable

## Console Lab

TODO