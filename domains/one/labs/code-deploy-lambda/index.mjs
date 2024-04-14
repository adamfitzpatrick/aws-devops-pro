async function handler () {
    const ENVIRONMENT_VAR = process.env['ENVIRONMENT_VAR'];
    const bodyJson = {
        ENVIRONMENT_VAR,
        handled: 'by Lambda version 1'
    };

    return {
        statusCode: 200,
        body: JSON.stringify(bodyJson)
    };
}

export { handler };