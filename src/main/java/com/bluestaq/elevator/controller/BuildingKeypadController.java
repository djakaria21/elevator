package com.bluestaq.elevator.controller;

import com.bluestaq.elevator.codegen.types.BuildingKeypad;
import com.bluestaq.elevator.codegen.types.KeypadRequest;
import com.bluestaq.elevator.exceptions.FloorNotFoundException;
import com.bluestaq.elevator.exceptions.SameFloorException;
import com.bluestaq.elevator.mongoentities.MongoKeypadRequest;
import com.bluestaq.elevator.repository.KeypadRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.bluestaq.elevator.constants.ExceptionConstants.SAME_FLOOR_EXCEPTION_WORDING;
import static com.bluestaq.elevator.constants.KafkaConstants.KEYPAD_REQUEST_TOPIC;

@RestController
public class BuildingKeypadController {

    @Autowired
    private final KafkaTemplate<String, KeypadRequest> kafkaTemplate;

    @Autowired
    private KeypadRepository keypadRepository;

    @Autowired
    public BuildingKeypadController(KafkaTemplate<String, KeypadRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/callElevator")
    public void callElevator(@RequestBody BuildingKeypad buildingKeypadFloor1, @RequestParam int destinationFloor) {

        checkValidDestination(buildingKeypadFloor1, destinationFloor);
        KeypadRequest keypadRequest = buildKeypadRequest(buildingKeypadFloor1, destinationFloor);
        kafkaTemplate.send(KEYPAD_REQUEST_TOPIC, keypadRequest);
        keypadRepository.save(buildMongoKeypadRequest(buildingKeypadFloor1, destinationFloor));

    }

    @NotNull
    private static MongoKeypadRequest buildMongoKeypadRequest(BuildingKeypad buildingKeypad, int destinationFloor) {
        //casting is needed in order to have mongo recognize the request and put it in a collection
        MongoKeypadRequest keypadRequest = new MongoKeypadRequest();
        keypadRequest.setId(buildingKeypad.getId());
        keypadRequest.setStartingFloor(buildingKeypad.getCurrentFloor());
        keypadRequest.setEndingFloor(destinationFloor);
        return keypadRequest;
    }

    @NotNull
    private static KeypadRequest buildKeypadRequest(BuildingKeypad buildingKeypadFloor1, int destinationFloor) {
        KeypadRequest keypadRequest = new KeypadRequest();
        keypadRequest.setStartingFloor(buildingKeypadFloor1.getCurrentFloor());
        keypadRequest.setEndingFloor(destinationFloor);
        return keypadRequest;
    }

    private static void checkValidDestination(BuildingKeypad buildingKeypadFloor1, int destinationFloor) {
        if (buildingKeypadFloor1.getCurrentFloor() == destinationFloor) {
            throw new SameFloorException(SAME_FLOOR_EXCEPTION_WORDING);
        }
        buildingKeypadFloor1.getFloors().stream().filter(floor -> floor == destinationFloor).findFirst().orElseThrow(() -> new FloorNotFoundException("Floor not found"));
    }


}
