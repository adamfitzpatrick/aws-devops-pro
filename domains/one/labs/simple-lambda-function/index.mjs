async function handler (event) {
    const bodyJson = {
        event,
        handled: 'by Lambda version 1'
    };

    return {
        statusCode: 200,
        body: JSON.stringify(bodyJson)
    };
}

export { handler };