package com.bluestaq.elevator.listeners;

import com.bluestaq.elevator.codegen.types.ElevatorCar;
import com.bluestaq.elevator.codegen.types.KeypadRequest;
import com.bluestaq.elevator.services.FindElevatorCarService;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.bluestaq.elevator.constants.KafkaConstants.KEYPAD_REQUEST_TOPIC;

@Component
public class ElevatorSystem {

    @Autowired
    private FindElevatorCarService findElevatorCarService;

    private final KafkaTemplate<String, KeypadRequest> keypadRequestKafkaTemplate;

    private final KafkaTemplate<String, ElevatorCar> elevatorKafkaTemplate;

    private final Gson gson = new Gson();

    private static final Logger log = LoggerFactory.getLogger(ElevatorSystem.class);

    @Autowired
    public ElevatorSystem(KafkaTemplate<String, ElevatorCar> elevatorKafkaTemplate, KafkaTemplate<String, KeypadRequest> keypadRequestKafkaTemplate) {
        this.elevatorKafkaTemplate = elevatorKafkaTemplate;
        this.keypadRequestKafkaTemplate = keypadRequestKafkaTemplate;
    }

    @KafkaListener(topics = KEYPAD_REQUEST_TOPIC, groupId = "elevator-consumer-group")
    public void recieve(ConsumerRecord<String, KeypadRequest> consumerRecord) {
        log.info("Received KeypadRequest: {}", consumerRecord.value());
        KeypadRequest recievedRequest = gson.fromJson(String.valueOf(consumerRecord.value()), KeypadRequest.class);
        Optional<ElevatorCar> elevatorCar = findElevatorCarService.getFindElevatorCar(recievedRequest);
        if (elevatorCar.isEmpty()) {
            elevatorCar = waitForAvailableElevator(elevatorCar, recievedRequest);
        } else {
            log.info("Sending ElevatorCar: {}", elevatorCar);
            send(elevatorCar.get());
        }
    }

    @NotNull
    private Optional<ElevatorCar> waitForAvailableElevator(Optional<ElevatorCar> elevatorCar, KeypadRequest recievedRequest) {
        for (int i = 0; i < 10 && !elevatorCar.isPresent(); i++) {
            log.info("Waiting for elevator car data...");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            elevatorCar = findElevatorCarService.getFindElevatorCar(recievedRequest);
        }

        if (elevatorCar.isPresent()) {
            log.info("Sending ElevatorCar: {}", elevatorCar.get());
            send(elevatorCar.get());
        } else {
            log.warn("Failed to get elevator car data after 10 retries");
        }
        return elevatorCar;
    }

    private void send(ElevatorCar elevatorCar) {
        elevatorKafkaTemplate.send("ElevatorSystemTopic", elevatorCar);

    }
}
