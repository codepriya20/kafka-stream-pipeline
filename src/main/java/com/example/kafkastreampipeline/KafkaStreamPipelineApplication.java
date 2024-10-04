package com.example.kafkastreampipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KafkaStreamPipelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamPipelineApplication.class, args);
    }

}
