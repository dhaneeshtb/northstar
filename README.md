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
    <artifactId>northstar</artifactId>
    <version>1.0.1</version>
</dependency>

```

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

