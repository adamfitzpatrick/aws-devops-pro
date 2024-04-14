import { LambdaClient, UpdateFunctionConfigurationCommand } from '@aws-sdk/client-lambda';
import { CodeDeployClient, PutLifecycleEventHookExecutionStatusCommand } from '@aws-sdk/client-codedeploy';

let lambdaClient;
let codeDeployClient;

async function handler (event) {
  lambdaClient = lambdaClient || new LambdaClient();
  codeDeployClient = codeDeployClient || new CodeDeployClient();

  const deploymentId = event.DeploymentId;
  const lifecycleEventHookExecutionId = event.LifecycleEventHookExecutionId

  const updateFunctionConfigInput = {
    FunctionName: 'demo-lambda',
    Environment: {
      Variables: {
        ENVIRONMENT_VAR: 'a useful value'
      }
    },
    Version: 3
  };
  const updateFunctionConfigCommand = new UpdateFunctionConfigurationCommand(updateFunctionConfigInput);
  
  const lifeCycleStatusInput = {
    deploymentId,
    lifecycleEventHookExecutionId,
    status: 'Succeeded'
  };

  try {
    await lambdaClient.send(updateFunctionConfigCommand);
  } catch (err) {
    console.log(err);
    lifeCycleStatusInput.status = 'Failed';
  }

  const lifeCycleStatusCommand = new PutLifecycleEventHookExecutionStatusCommand(lifeCycleStatusInput);
  return codeDeployClient.send(lifeCycleStatusCommand);
}

export { handler }