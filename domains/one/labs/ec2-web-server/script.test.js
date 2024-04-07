const assert = require('node:assert/strict');
require('./script');

assert.equal(global.sayHello('test value'), 'Hello, test value!');

console.log('Test passes!');