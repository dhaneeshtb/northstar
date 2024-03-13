package org.northstar.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PerformanceTest {

    public static void main(String[] args) {

        int port=8080;
        CheckServer.start(port);
        fireTest(port);
    }


    private static int getResponse(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setReadTimeout(15000);
            conn.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
            }
            return conn.getResponseCode();
        } finally {
            conn.disconnect();
        }
    }

    public static void fireTest(int port) {
        AtomicInteger ai = new AtomicInteger(0);
        AtomicInteger aiSuccess = new AtomicInteger(0);
        ExecutorService es = Executors.newFixedThreadPool(200);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            es.submit(() -> {
                for (int j = 0; j < 500; j++) {
                    try {
                        int rs = getResponse("http://localhost:"+port+"/status");
                        if (rs != 200) {
                            ai.incrementAndGet();
                            System.out.println(rs);

                        } else {
                            aiSuccess.incrementAndGet();
                        }
                    } catch (Exception e) {
                        ai.incrementAndGet();
                        e.printStackTrace();

                    }
                }
            });
        }
        es.shutdown();
        try {
            es.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Failure count -> " + ai.get());
        System.out.println("Success count -> " + aiSuccess.get());
        System.out.println("Time taken  -> " + (System.currentTimeMillis() - start));
    }
}
