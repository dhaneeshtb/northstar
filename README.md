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

