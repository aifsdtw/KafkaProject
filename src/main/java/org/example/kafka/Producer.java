package org.example.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    public static void main(String[] args) {
        logger.info("I am a Kafka Producer");

        String bootstrapServers = "127.0.0.1:9092";
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String,String> producer = new KafkaProducer<>(properties);
        ProducerRecord<String,String> record = new ProducerRecord<>("first_topic","hello");
        producer.send(record, new Callback() {
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if(exception == null) {
                    logger.info("Successfully sent record" + "\n" +
                            "Topic: "+ metadata.topic() + "\n" +
                            "Partition: "+ metadata.partition()+ "\n" +
                            "Offset: "+ metadata.offset() + "\n" +
                            "Timestamp: "+ metadata.timestamp());
                }else{
                    logger.error("Error sending record", exception);
                }
            }
        });
        producer.flush();
        producer.close();
    }
}
