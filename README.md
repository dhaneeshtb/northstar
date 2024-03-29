# Northstar (Nano HTTP Server)

### Lightweight Nano HTTP Server

NorthStar is a lightweight, high-performance Nano HTTP server designed to handle a massive number of concurrent HTTP connections efficiently while maintaining a low memory footprint. It's built with Java, making it highly portable and easy to integrate into various projects.

## Features

- **High Performance**: NorthStar is optimized for handling a large number of concurrent HTTP connections without compromising on performance.
- **Low Footprint**: Designed to be lightweight, ensuring minimal memory consumption.
- **Easy Integration**: Simple to integrate into existing Java applications or projects.
- **Scalable**: Capable of scaling horizontally to accommodate growing demands.
- **HTTP/1.1 Compliant**: Supports HTTP/1.1 standards for seamless interoperability with web clients.

## Installation

To use NorthStar in your Java project, you can include it as a dependency using Maven or Gradle.

**Maven:**
```xml
<dependency>
    <groupId>io.github.dhaneeshtb</groupId>
    <artifactId>northstar-server</artifactId>
    <version>1.0.9</version>
</dependency>

```
### 1. Starting server with default status route
```java
    HttpServer.HttpServerBuilder builder=HttpServer.HttpServerBuilder.createBuilder();
    builder.withPort(8080).withRoute(new DefaultStatusRoute())
        .withJWTParser(new JWTKeyImpl("", Algorithm.HMAC512("test".getBytes(StandardCharsets.UTF_8))));
    HttpServer server=builder.build();
    try {
        server.start();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
```
### 2. Starting server with custom route
```java
HttpServer.HttpServerBuilder builder = HttpServer.HttpServerBuilder.createBuilder();
builder.withPort(8080).withRoute(new DefaultStatusRoute())
        .withRoute(new AbstractRoute() {
            @Override
            public String baseLayer() {
                return "/test";
            }

            @Override
            public boolean isAuthNeeded() {
                return false;
            }

            @Override
            public RequestRoutingResponse handle(HttpRequest request) throws Exception {
                return RequestRoutingResponse.response(HttpResponseStatus.OK, new RouteMessage.RouteAttributeMessage(Map.of("name", "value")));
            }
        });
server = builder.build();
try {
    server.start();
} catch (Exception e) {
    throw new RuntimeException(e);
}

```

### 3. Custom server with Functional route

```java
public class CheckServerWithCustom {
    public static void main(String[] args) {
        start(8080);
    }

    public static void start(int port) {
        HttpServer.HttpServerBuilder builder= HttpServer.HttpServerBuilder.createBuilder();
        builder.withPort(port).withRoute(new DefaultStatusRoute())
                .withRoute("/test/{id}/{yy}/testing",false,(request,authInfo,match)->
                        RequestRoutingResponse.response(HttpResponseStatus.OK, new RouteMessage.RouteAttributeMessage(match.getAttributes()))).withJWTParser(new JWTKeyImpl("", Algorithm.HMAC512("test".getBytes(StandardCharsets.UTF_8))));
        HttpServer server=builder.build();
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

### 4. Server with default login enabled

```java
public class CheckServerWithLogin {
    public static void main(String[] args) {
        start(8080);
    }

    public static void start(int port) {
        HttpServer.HttpServerBuilder builder= HttpServer.HttpServerBuilder.createBuilder();
        builder.withPort(port).
                 withUserLoginConf(new UserFileStore("sampleusers.json"))
                .withRoute(new DefaultStatusRoute())
                .withDomain("localhost")
                .withRoute("/test/{id}/{yy}/testing",false,(request,authInfo,match)->
                        RequestRoutingResponse.response(HttpResponseStatus.OK, new RouteMessage.RouteAttributeMessage(match.getAttributes()))).withJWTParser(new JWTKeyImpl("", Algorithm.HMAC512("test".getBytes(StandardCharsets.UTF_8))));
        HttpServer server=builder.build();
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```
#### sampleusers.json
```json
{
  "users": [
    {
      "username": "dhaneesh",
      "password": "$$$$$"
    }
  ]
}
```

### 5. Server with custom cookie handler
```java
package org.northstar.server;

import com.auth0.jwt.algorithms.Algorithm;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import org.northstar.servers.HttpServer;
import org.northstar.servers.auth.CookieHandler;
import org.northstar.servers.auth.UserFileStore;
import org.northstar.servers.jwt.AuthRequest;
import org.northstar.servers.jwt.JWTKeyImpl;
import org.northstar.servers.routing.DefaultStatusRoute;
import org.northstar.servers.routing.RequestRoutingResponse;
import org.northstar.servers.routing.RouteMessage;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public class CheckServerWithLoginCookieHandler {
    public static void main(String[] args) {
        start(8080);
    }

    public static void start(int port) {
        HttpServer.HttpServerBuilder builder= HttpServer.HttpServerBuilder.createBuilder();
        builder.withPort(port).
                 withUserLoginConf(new UserFileStore("sampleusers.json"))
                .withRoute(new DefaultStatusRoute())
                .withCookieHandle(new CookieHandler() {
                    @Override
                    public Cookie onSetCookie(HttpRequest request, AuthRequest.LoginResponse loginResponse) {
                        DefaultCookie cookie= new DefaultCookie("test","tes");
                        cookie.setPath("/");
                        cookie.setMaxAge(100);//Seconds
                        return cookie;
                    }

                    @Override
                    public String onReadToken(HttpRequest request, Set<Cookie> cookies) {
                        Cookie cookie=  cookies.stream().filter(c->c.name().equalsIgnoreCase(cookieName())).findFirst().orElse(null);
                        return cookie!=null?cookie.value():null;
                    }

                    @Override
                    public String cookieName() {
                        return "token";
                    }
                })
                .withDomain("localhost")
                .withRoute("/test/{id}/{yy}/testing",false,(request,authInfo,match)->
                        RequestRoutingResponse.response(HttpResponseStatus.OK, new RouteMessage.RouteAttributeMessage(match.getAttributes()))).withJWTParser(new JWTKeyImpl("", Algorithm.HMAC512("test".getBytes(StandardCharsets.UTF_8))));
        HttpServer server=builder.build();
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

```




### 6. E2End Encryption support
```java
package org.northstar.server;

import com.auth0.jwt.algorithms.Algorithm;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.northstar.servers.End2EndEncryption;
import org.northstar.servers.HttpServer;
import org.northstar.servers.jwt.JWTKeyImpl;
import org.northstar.servers.routing.DefaultStatusRoute;
import org.northstar.servers.routing.RequestRoutingResponse;
import org.northstar.servers.routing.RouteMessage;

import java.nio.charset.StandardCharsets;

public class CheckServerWithE2EEncryption {
    public static void main(String[] args) {
        start(8080);
    }

    public static void start(int port) {

        try {
            End2EndEncryption e2ee= new End2EndEncryption();
            HttpServer.HttpServerBuilder builder= HttpServer.HttpServerBuilder.createBuilder();
            builder.withEnd2EndEncryption(e2ee);
            builder.withPort(port).withRoute(new DefaultStatusRoute())
                    .withRoute("/test/{id}/{yy}/testing",false,true,(request,authInfo,match)->
                            RequestRoutingResponse.response(HttpResponseStatus.OK, new RouteMessage.RouteAttributeMessage(match.getAttributes()))).withJWTParser(new JWTKeyImpl("", Algorithm.HMAC512("test".getBytes(StandardCharsets.UTF_8))));
            HttpServer server=builder.build();

            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

```
#### Client decrypt code
```java
const crypto = require('crypto')
const axios =require('axios');
const ALGORITHM = 'aes-256-gcm';
const TAG_LENGTH = 16;
const KEY_LENGTH = 32;
const ITERATION = 65536;

function generateKeyFiles() {
   const keyPair = crypto.generateKeyPairSync('rsa', {
      modulusLength: 4096,
      publicKeyEncoding: {
         type: 'spki',
         format: 'pem'
      },
      privateKeyEncoding: {
         type: 'pkcs8',
         format: 'pem',
      }
   });
    return keyPair;
}

class GCM {
    constructor(secret,options) {
      this.secret=secret;
      this.options=options;
    }
    getKey(salt) {
        return crypto.pbkdf2Sync(this.secret, salt, this.options.ITERATION_COUNT||ITERATION, this.options.KEY_LENGTH||KEY_LENGTH, 'sha256');
    }


    decrypt(cipherText,salt,iv,aad) {
        const key = this.getKey(salt);
        const authTagLength = this.options.TAG_LENGTH||16;
        const decipher = crypto.createDecipheriv(ALGORITHM, key, iv, { authTagLength:authTagLength });
        decipher.setAutoPadding(true);
        const tl=cipherText.length;
        if(aad){
            decipher.setAAD(aad);
        }
        decipher.setAuthTag(cipherText.slice(tl-authTagLength,tl));
        const dec=decipher.update(cipherText.slice(0,tl-authTagLength),'binary');
        return dec.toString('utf-8')+decipher.final('utf-8');
    }
}

function numberFromBytes(buffer) {
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


const server="http://localhost:8080"

async function getPublicKey(){
    return (await axios.get(server+"/publickey")).data.publicKey;
}

async function fetchData(){
    const key=await getPublicKey();
    const randomPassword=crypto.randomBytes(20).toString('hex');
    const encryptPass = crypto.publicEncrypt({
                                                   key: key,
                                                   padding: crypto.constants.RSA_PKCS1_OAEP_PADDING
                                                 },Buffer.from(randomPassword)).toString("base64")
    let resp=null;
    try{
        resp = await axios.get(server+"/test/tt/ff/testing",{headers:{
        "X-Client-Token":encryptPass
        }});
    }catch(e){
        console.log(e)
        return;
    }
    const buf = Buffer.from(resp.data,'base64');
    const s = numberFromBytes(buf.slice(0,4))
    const vs = numberFromBytes(buf.slice(4,8))
    console.log("Decrypt with server public key")
    const originalData = crypto.publicDecrypt({
                              key: key,
                              padding: crypto.constants.RSA_PKCS1_PADDING
                          },Buffer.from(buf.slice(8,s+8).toString('utf-8'),'base64'));
    const info = JSON.parse(originalData);
    //console.log(info)
    info.password=randomPassword+info.password
    const gcm=new GCM(info.password,info.constants);
    const db=Buffer.from(buf.slice(s+8),'utf-8')
    const dec = gcm.decrypt(db,Buffer.from(info.salt,'base64'),Buffer.from(info.iv,"base64"),info.aad && Buffer.from(info.aad,"utf8"));
    console.log(dec);
}


async function fetchDataWithClentCert({ publicKey, privateKey }){
    const key=await getPublicKey();
    const randomPassword=crypto.randomBytes(20).toString('hex');
    console.log("Generated Keys")
    const encryptPass = crypto.publicEncrypt({
                                                   key: key,
                                                   padding: crypto.constants.RSA_PKCS1_OAEP_PADDING
                                                 },Buffer.from(randomPassword)).toString("base64")
    const resp = await axios.get(server+"/test/tt/ff/testing",{headers:{
    "X-Client-Cert":Buffer.from(publicKey).toString('base64'),
    "X-Client-Token":encryptPass
    }});
    const buf = Buffer.from(resp.data,'base64');
    const s = numberFromBytes(buf.slice(0,4))
    const vs = numberFromBytes(buf.slice(4,8))
    console.log("Decrypt with server public key")
    const originalData = crypto.privateDecrypt({
                              key: privateKey,
                              padding: crypto.constants.RSA_PKCS1_OAEP_PADDING
                          },Buffer.from(buf.slice(8,s+8).toString('utf-8'),'base64'));
    const info = JSON.parse(originalData);
    console.log(info)
    info.password=randomPassword+info.password
    const gcm=new GCM(info.password,info.constants);
    const db=Buffer.from(buf.slice(s+8),'utf-8')
    const dec = gcm.decrypt(db,Buffer.from(info.salt,'base64'),Buffer.from(info.iv,"base64"),info.aad && Buffer.from(info.aad,"utf8"));
    console.log(dec);
}

//Fetch with client key
fetchData();

//Fetch with client RSA certificate
fetchDataWithClentCert(generateKeyFiles());

```


## Performance Test Report

### Server Performance Metrics

| Metric             | Value        |
|--------------------|--------------|
| Total Requests     | 500,000      |
| Total Time         | 8.04 seconds |
| Requests per Second| 62,189       |
| Average Latency    | 0.0001289 seconds/request |

### Conclusion

Based on the performance test results, the HTTP server demonstrated robust performance capabilities, effectively serving 500,000 requests within a short duration of 8.04 seconds. With proper monitoring and optimization strategies, the server can continue to deliver reliable and responsive service to users under varying workloads.




## Contributing
Contributions to NorthStar are welcome! If you find any issues or have suggestions for improvements, feel free to open an issue or submit a pull request on GitHub.


## License
MIT

## Support
For any inquiries or support, you can contact the maintainers at dhaneeshtnair@gmail.com.

