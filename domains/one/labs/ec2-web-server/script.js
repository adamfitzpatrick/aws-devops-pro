let exporter;
try {
    exporter = global;
} catch (e) {
    exporter = window;
}
exporter.sayHello = input => `Hello, ${input}!`