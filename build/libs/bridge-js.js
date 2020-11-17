console.log(process.argv[2]);
console.log(process.argv[3]);
var child = require('child_process').spawn(
  'java', ['-cp', 'mongoDB_1-1.0-SNAPSHOT.jar:mongo-java-driver-3.12.7.jar', 'mp.MongoDB', process.argv[2], process.argv[3]]
);
console.log('Request Sent');
