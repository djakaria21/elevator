package com.bluestaq.elevator.config;

import com.bluestaq.elevator.codegen.types.ElevatorCar;
import com.bluestaq.elevator.codegen.types.KeypadRequest;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static com.bluestaq.elevator.constants.KafkaConstants.BOOTSTRAP_SERVER_URL;
import static com.bluestaq.elevator.constants.KafkaConstants.KEYPAD_REQUEST_TOPIC;

@EnableKafka
@Configuration
public class KafkaConfig {

    // Producer Configuration for KeypadRequest
    @Bean
    public ProducerFactory<String, KeypadRequest> keypadProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER_URL);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // Corrected
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // Corrected
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // Producer Configuration for ElevatorCar
    @Bean
    public ProducerFactory<String, ElevatorCar> elevatorCarProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER_URL);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // KafkaTemplate for KeypadRequest
    @Bean
    public KafkaTemplate<String, KeypadRequest> keypadKafkaTemplate() {
        return new KafkaTemplate<>(keypadProducerFactory());
    }

    // KafkaTemplate for ElevatorCar
    @Bean
    public KafkaTemplate<String, ElevatorCar> elevatorCarKafkaTemplate() {
        return new KafkaTemplate<>(elevatorCarProducerFactory());
    }

    // Topic Configuration
    @Bean
    public NewTopic keypadTopic() {
        return TopicBuilder.name(KEYPAD_REQUEST_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic elevatorTopic() {
        return TopicBuilder.name("ElevatorCar")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Consumer Configuration for KeypadRequest
    @Bean
    public ConsumerFactory<String, KeypadRequest> keypadConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER_URL);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "elevator-system");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.bluestaq.elevator.codegen.types"); // Corrected and more specific
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); //Added explicitly

        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(KeypadRequest.class));
    }
}
