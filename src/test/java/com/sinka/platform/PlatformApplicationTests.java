package com.sinka.platform;

import org.apache.logging.log4j.message.ThreadDumpMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class PlatformApplicationTests {

    @Test
    void contextLoads() {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        pool.execute(() -> {
            System.out.println(Thread.currentThread().getName());
        });

        pool.execute(() -> {
            System.out.println(Thread.currentThread().getName());
        });

        pool.shutdown();
    }

}
