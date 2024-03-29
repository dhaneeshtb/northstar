


function convertDataURIToBinary(base64) {
  var raw = window.atob(base64);
  var rawLength = raw.length;
  var array = new Uint8Array(new ArrayBuffer(rawLength));

  for(i = 0; i < rawLength; i++) {
    array[i] = raw.charCodeAt(i);
  }
  return array;
}

const buf = Buffer.from("AAACrAAAAPpReUtJUGNpYUt3Y3lUakM2Sm5KVG5ZNi9KVUFmbEcwdXFzRnhhdFFzOURWVlRtaWlmcEZrZTEzL3RLWTNWOWpudVd6aTNqNkFOczR2b084UUpQa2dhUUw1Ym9lSWh5N2xkY0JWTjRNUkRMZ2I0R1k4eTdJSGJxdWZKdGROSjZ3UTRSY1dxVWluaHZka2FJTEh6WnFEWkMzd0tjZkl0bFdQTWVUWVNPalFIVlo2MnJhNjRHSFUyejBZbVZWTVlBMytJcUJnUkFWTUk2Tm1JSkJpcWJ0dmVJNGJ2OGZyM0s3eUQ3bHBQQS9mZVdESDRMRXJtRG5PZ1R5by9Ic2NnMWtIdVZ2L04wQlluUHgvTDZyejdiUDkwdzBGR1ZkcFdxVitsb3dJZk9KUGVuaEhMSjluZUZsZzF3Q1prbzhDZDJoWlhKY0poeWpXb1NYM0JTMEpGVWJDQkoybXRybDE4Z0JtVmtyV1hCQUw2aldYUnQxNEdlRFpLdkozNlpjSXFaZWFpSFI3ZTBlSDVxREZXVU1lUklUWThZTC9DczlVUmNoTG5tMVh0MFJzeFdoTEQyNDcrL1A5d3E0N01jUzlETm1aTDlnMUJReExyWkZYWHdxWitjYzBUQXRGUDV0QjRBUE85SlJzY0hxVHZURnI2UU81YkwvVmhSTnJPc0tuRDU1ZE9lSTdYMVhEcUpFNHo1eUVyVy9QOXN1OUJoOXM1SllSY2FSYklITk0wdldiTjRsMGJQVEpNc1RnWXNFRXlNQ2ZkRFRKR0ppZkZuSERjcUVCVmxZcFJxWjRYRmVnSUNPQ0FnYWdlclZhbW1oR1Yvd0xqYWZobnVPRTZSNVcvT2xqZWw0UzM2WStORkN2eFZibmM5Mk9SZTJPRTBYUjdsNGl4eFduYW1GRXFpTT0/TbMAyIRCxa5DbbjP3cv3/mWaUIDCx6B5qMinLD5vGi5TEvB1yn6/ylxTAjh03TwJmSageht9Z7io2ByRSlU+UUJt4c45gYbfnPhOFxlsBTq0+EvnY50paFyzrZK5+PAMKeJf43oka7ik96Ofw1YfXTw+MZkQLLVzvXcqiF4RO7ltgrybcGWHbO+CTOHx0gddJ21z363Mm2R1AJZoxcpAXbJLvdnlSt40NdPAhanOawMXY3KK54H97kD3gUXVPi6k93sa4MGpiRKMbofhLUVC/OcciEDZTF3+6UFYZzGm7cSkUdqj0fgNmg3Kh8zDoidDzjgcZ5IU7v+E", 'base64');

console.log(buf.length)

var y = buf.slice(0,4);

function fromBytes(buffer) {
  const bytes = new Uint8ClampedArray(buffer);
  const size = bytes.byteLength;
  let x = 0;
  for (let i = 0; i < size; i++) {
    const byte = bytes[i];
    x *= 0x100;
    x += byte;
  }
  return x;
}

const s = fromBytes(y)

console.log(s+8);

console.log(buf.slice(8,s+8).toString('utf8'));

console.log(buf.slice(s+8)[1]);

