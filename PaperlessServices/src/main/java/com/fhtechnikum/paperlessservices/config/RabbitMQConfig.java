package com.fhtechnikum.paperlessservices.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String OCR_QUEUE_NAME = "ocr-queue";

    /**
     * Define the OCR queue that this worker will listen to.
     * Must match the queue name used in PaperlessREST.
     */
    @Bean
    public Queue ocrQueue() {
        return new Queue(OCR_QUEUE_NAME, false);
    }
}
