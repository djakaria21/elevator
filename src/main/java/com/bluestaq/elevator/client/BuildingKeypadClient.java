package com.bluestaq.elevator.client;

import com.bluestaq.elevator.codegen.types.BuildingKeypad;
import com.bluestaq.elevator.codegen.types.Floor;
import com.bluestaq.elevator.codegen.types.KeypadRequest;
import com.bluestaq.elevator.exceptions.FloorNotFoundException;
import com.bluestaq.elevator.exceptions.SameFloorException;
import com.bluestaq.elevator.repository.KeypadRepository;
import jakarta.websocket.ClientEndpoint;
import org.apache.kafka.clients.admin.NewTopic;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

@ClientEndpoint
public class BuildingKeypadClient {

    private final KafkaTemplate<String, KeypadRequest> kafkaTemplate;

    @Autowired
    private KeypadRepository keypadRepository;

    @Autowired
    public BuildingKeypadClient(KafkaTemplate<String, KeypadRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Bean
    public NewTopic topic(){
        return TopicBuilder.name("ElevatorTopic").partitions(2).replicas(3).build();
    }


    public void callElevator(BuildingKeypad buildingKeypadFloor1, int destinationFloor) {

        checkValidDestination(buildingKeypadFloor1, destinationFloor);
        KeypadRequest keypadRequest = buildKeypadRequest(buildingKeypadFloor1, destinationFloor);
        kafkaTemplate.send("ElevatorTopic", keypadRequest);
        keypadRepository.save(keypadRequest);
    }

    @NotNull
    private static KeypadRequest buildKeypadRequest(BuildingKeypad buildingKeypadFloor1, int destinationFloor) {
        KeypadRequest keypadRequest = new KeypadRequest();
        keypadRequest.setStartingFloor(buildingKeypadFloor1.getCurrentFloor());
        keypadRequest.setEndingFloor(destinationFloor);
        return keypadRequest;
    }

    private static void checkValidDestination(BuildingKeypad buildingKeypadFloor1, int destinationFloor) {
        if(buildingKeypadFloor1.getCurrentFloor() == destinationFloor)
        {
            throw new SameFloorException("You are already on the floor");
        }
        buildingKeypadFloor1.getFloors().stream().filter(floor -> floor == destinationFloor).findFirst().orElseThrow(() -> new FloorNotFoundException("Floor not found"));
    }
}
