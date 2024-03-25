import * as cdk from 'aws-cdk-lib';
import { Template } from 'aws-cdk-lib/assertions';
import { DomainOneStack } from '../lib/domain-one-stack';

test('demo CodeCommit repository created', () => {
  const app = new cdk.App();
  const stack = new DomainOneStack(app, 'TestStack');
});
