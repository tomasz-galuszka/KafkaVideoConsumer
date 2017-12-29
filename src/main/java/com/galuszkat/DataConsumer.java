package com.galuszkat;

import avro.CameraImage;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;

public class DataConsumer {

    private final static String TOPIC = "laptop-images";
    private final KafkaConsumer<String, CameraImage> kafkaConsumer;
    private final Properties properties = new Properties();
    {
        properties.put("bootstrap.servers", "broker:9092");
        properties.put("group.id", "cameras.consumers");
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", KafkaAvroDeserializer.class.getName());
        properties.put("schema.registry.url", "http://127.0.0.1:8081");
        properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

    }

    public DataConsumer() {
        kafkaConsumer = new KafkaConsumer<>(properties);
    }

    public void consume(OnConsumed onConsumed) {
        kafkaConsumer.subscribe(Collections.singletonList(TOPIC));

        while(true) {
            List<String> images = new ArrayList<>();
            for (ConsumerRecord<String, CameraImage> image : kafkaConsumer.poll(1000)) {
                String image64 = new String(Base64.getEncoder().encode(image.value().getData().array()));
                images.add(image64);

                kafkaConsumer.commitAsync();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                kafkaConsumer.close();
            }
            onConsumed.processData(images);
        }
    }
}
