package com.bluestaq.elevator.listeners;

import com.bluestaq.elevator.codegen.types.KeypadRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.bluestaq.elevator.constants.KafkaConstants.KEYPAD_REQUEST_TOPIC;

@Component
public class ElevatorSystem {

    private static final Logger log = LoggerFactory.getLogger(ElevatorSystem.class);

    @KafkaListener(topics = KEYPAD_REQUEST_TOPIC, groupId = "elevator-system")
    public void recieve(ConsumerRecord<String, KeypadRequest> consumerRecord) {
        log.info("Received KeypadRequest: {}", consumerRecord.toString());
        //TODO: process kafka request
        KeypadRequest recievedRequest = consumerRecord.value();
        // Process the received KeypadRequest here (e.g., trigger elevator logic)
    }
}
