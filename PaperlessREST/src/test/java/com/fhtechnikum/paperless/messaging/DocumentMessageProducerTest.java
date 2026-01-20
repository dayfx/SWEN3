package com.fhtechnikum.paperless.messaging;

import com.fhtechnikum.paperless.config.RabbitMQConfig;
import com.fhtechnikum.paperless.messaging.dto.DocumentMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DocumentMessageProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private DocumentMessageProducer producer;

    @Test
    void sendOcrMessage_ShouldSendCorrectMessageToQueue() {

        Long docId = 123L;
        String filename = "test-doc.pdf";
        String minioKey = "buckets/test-doc.pdf";

        producer.sendOcrMessage(docId, filename, minioKey);

        // capture the argument sent to RabbitMQ to inspect
        ArgumentCaptor<DocumentMessage> messageCaptor = ArgumentCaptor.forClass(DocumentMessage.class);

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.OCR_QUEUE_NAME), // ensure it goes to the right queue
                messageCaptor.capture()            // catch the message object
        );

        DocumentMessage sentMessage = messageCaptor.getValue();
        assertEquals(docId, sentMessage.getDocumentId());
        assertEquals(filename, sentMessage.getFilename());
        assertEquals(minioKey, sentMessage.getMinioObjectKey());
    }
}