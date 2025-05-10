package org.example.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProducerTest {

    @Mock
    private KafkaProducer<String, String> mockProducer;

    @Mock
    private Logger mockLogger;

    private Producer producer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        producer = new Producer();
    }

    @Test
    void testProducerSendSuccess() {
        // Arrange
        String topic = "first_topic";
        String message = "hello";
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        RecordMetadata metadata = new RecordMetadata(null, 0, 0, 0, Long.valueOf(0), 0, 0);

        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(1);
            callback.onCompletion(metadata, null);
            return null;
        }).when(mockProducer).send(eq(record), any(Callback.class));

        // Act
        mockProducer.send(record, (metadata1, exception) -> {
            if (exception == null) {
                mockLogger.info("Successfully sent record");
            }
        });

        // Assert
        verify(mockLogger, times(1)).info("Successfully sent record");
    }

    @Test
    void testProducerSendFailure() {
        // Arrange
        String topic = "first_topic";
        String message = "hello";
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        Exception exception = new Exception("Error sending record");

        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(1);
            callback.onCompletion(null, exception);
            return null;
        }).when(mockProducer).send(eq(record), any(Callback.class));

        // Act
        mockProducer.send(record, (metadata, ex) -> {
            if (ex != null) {
                mockLogger.error("Error sending record", ex);
            }
        });

        // Assert
        verify(mockLogger, times(1)).error(eq("Error sending record"), eq(exception));
    }
}