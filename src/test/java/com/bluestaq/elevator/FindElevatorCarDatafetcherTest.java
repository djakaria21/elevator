package com.bluestaq.elevator;

import com.bluestaq.elevator.codegen.types.ElevatorCar;
import com.bluestaq.elevator.codegen.types.KeypadRequest;
import com.bluestaq.elevator.repository.ElevatorRespository;
import com.bluestaq.elevator.services.FindElevatorCarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
public class FindElevatorCarDatafetcherTest {

    @MockitoBean
    private ElevatorRespository elevatorRespository;

    @Autowired
    private FindElevatorCarService findElevatorCarDatafetcher;

    @MockitoBean
    private KafkaTemplate<String, ElevatorCar> kafkaTemplate;


    private KeypadRequest keypadRequest;
    private ElevatorCar elevatorCar1;
    private ElevatorCar elevatorCar2;
    private ElevatorCar elevatorCar3;
    private ElevatorCar elevatorCar4;
    private List<ElevatorCar> elevatorCars;

    @BeforeEach
    public void setUp() {

        keypadRequest = new KeypadRequest();

        keypadRequest.setStartingFloor(1);
        keypadRequest.setEndingFloor(2);

        elevatorCar1 = new ElevatorCar();
        elevatorCar1.setId("Elevator-123");
        elevatorCar1.setCurrentFloor(2);
        elevatorCar1.setInsidePeople(0);
        elevatorCar1.setCapacity(10);


        elevatorCar2 = new ElevatorCar();
        elevatorCar2.setId("Elevator-456");
        elevatorCar2.setCurrentFloor(3);
        elevatorCar2.setInsidePeople(0);

        elevatorCar3 = new ElevatorCar();
        elevatorCar3.setId("Elevator-789");
        elevatorCar3.setCurrentFloor(1);
        elevatorCar3.setInsidePeople(0);

        elevatorCar4 = new ElevatorCar();
        elevatorCar4.setId("Elevator-999");
        elevatorCar4.setCurrentFloor(2);

    }

    @Test
    void testFindElevatorCar_ClosestAvailable() {
        elevatorCars = List.of(elevatorCar1, elevatorCar2, elevatorCar4);
        when(elevatorRespository.findValidElevators(false)).thenReturn(elevatorCars);

        Optional<ElevatorCar> result = findElevatorCarDatafetcher.findNearestAvailableElevatorCar(keypadRequest);

        assertTrue(result.isPresent());
        assertEquals(elevatorCar1.getId(), result.get().getId());
    }

    //in the case that the elevator is moving it will not show up in the repository query
    @Test
    void testFindElevatorCar_NoAvailableElevators() {
        when(elevatorRespository.findValidElevators(false)).thenReturn(List.of());

        Optional<ElevatorCar> result = findElevatorCarDatafetcher.findNearestAvailableElevatorCar(keypadRequest);

        assertFalse(result.isPresent());
    }

}