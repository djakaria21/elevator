package com.bluestaq.elevator;

import com.bluestaq.elevator.controller.BuildingKeypadController;
import com.bluestaq.elevator.codegen.types.BuildingKeypad;
import com.bluestaq.elevator.codegen.types.KeypadRequest;
import com.bluestaq.elevator.exceptions.FloorNotFoundException;
import com.bluestaq.elevator.exceptions.SameFloorException;
import com.bluestaq.elevator.repository.KeypadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import java.util.ArrayList;

import static com.bluestaq.elevator.constants.KafkaConstants.KEYPAD_REQUEST_TOPIC;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = BuildingKeypadController.class)
public class BuildingKeypadClientTest {

    @MockitoBean
    private KafkaTemplate<String, KeypadRequest> kafkaTemplate;

    @Autowired
    private BuildingKeypadController client;

    private BuildingKeypad buildingKeypadFloor1;
    private BuildingKeypad buildingKeypadFloor2;
    private BuildingKeypad buildingKeypadFloor3;

    private ArrayList<Integer> floors;

    @MockitoBean
    private KeypadRepository keypadRepository;


    @BeforeEach
    public void setUpBuildingKeypads() {

        floors = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            floors.add(i);
        }

        buildingKeypadFloor1 = new BuildingKeypad("1", 1, floors);
        buildingKeypadFloor2 = new BuildingKeypad("2", 2, floors);
        buildingKeypadFloor3 = new BuildingKeypad("3", 3, floors);

    }

    @Test
    void callElevatorTest() {

       client.callElevator(buildingKeypadFloor1, 2);
        client.callElevator(buildingKeypadFloor2, 3);
        client.callElevator(buildingKeypadFloor3, 1);

       verify(kafkaTemplate, times(3)).send(eq(KEYPAD_REQUEST_TOPIC), any(KeypadRequest.class));

       verify(keypadRepository, times(3)).save(any(KeypadRequest.class));
    }

    @Test
    void sameFloorExceptionTest() {
        //elevator can't call its own floor
        assertThrows(SameFloorException.class, () -> {
            client.callElevator(buildingKeypadFloor1,1);
        });
    }

    @Test
    void floorNotFoundExceptionTest() {
        //elevator can't call its own floor
        assertThrows(FloorNotFoundException.class, () -> {
            client.callElevator(buildingKeypadFloor1,56);
        });
    }



}
