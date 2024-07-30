import http from 'http';

const APP_REVISION = 1;

const server = http.createServer((req, res) => {
    res.end(`I am application revision ${APP_REVISION}!`);
});

server.listen(8080, () => console.log('Listening on 8080'));