package org.example.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.util.Collections;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

class ConsumerTest {

    @Mock
    private KafkaConsumer<String, String> mockConsumer;

    @Mock
    private Logger mockLogger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsumerPollMessages() {
        // Arrange
        ConsumerRecord<String, String> record = new ConsumerRecord<>("first_topic", 0, 0, "key", "value");
        ConsumerRecords<String, String> records = new ConsumerRecords<>(
                Collections.singletonMap(new TopicPartition(record.topic(), record.partition()), Collections.singletonList(record))
        );
        when(mockConsumer.poll(any(Duration.class))).thenReturn(records);

        // Act
        mockConsumer.subscribe(Collections.singletonList("first_topic"));
        ConsumerRecords<String, String> polledRecords = mockConsumer.poll(Duration.ofMillis(100));

        for (ConsumerRecord<String, String> polledRecord : polledRecords) {
            mockLogger.info("message consumed");
            mockLogger.info(polledRecord.value());
            mockLogger.info(polledRecord.topic());
            mockLogger.info(polledRecord.key());
            mockLogger.info(String.valueOf(polledRecord.partition()));
        }

        // Assert
        verify(mockLogger, times(1)).info("message consumed");
        verify(mockLogger, times(1)).info("value");
        verify(mockLogger, times(1)).info("first_topic");
        verify(mockLogger, times(1)).info("key");
        verify(mockLogger, times(1)).info("0");
    }
}