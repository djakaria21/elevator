package com.bluestaq.elevator;

import com.bluestaq.elevator.codegen.types.ElevatorCar;
import com.bluestaq.elevator.codegen.types.KeypadRequest;
import com.bluestaq.elevator.repository.ElevatorRespository;
import com.bluestaq.elevator.services.FindElevatorCarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class FindElevatorCarServiceTest {

    @Mock
    private ElevatorRespository elevatorRepository;

    @InjectMocks
    private FindElevatorCarService findElevatorCarService;

    private KeypadRequest keypadRequest;
    private ElevatorCar elevator1;
    private ElevatorCar elevator2;
    private ElevatorCar elevator3;
    private List<ElevatorCar> availableElevators;

    @BeforeEach
    void setUp() {
        keypadRequest = new KeypadRequest();
        keypadRequest.setStartingFloor(3);

        // Create test elevator cars
        elevator1 = new ElevatorCar();
        elevator1.setId("1");
        elevator1.setCurrentFloor(1);
        elevator1.setIsMoving(true);
        elevator1.setDestinationFloor(5);
        elevator1.setInsidePeople(2);
        elevator1.setCapacity(8);

        elevator2 = new ElevatorCar();
        elevator2.setId("2");
        elevator2.setCurrentFloor(4);
        elevator2.setIsMoving(false);
        elevator2.setDestinationFloor(4);
        elevator2.setInsidePeople(0);
        elevator2.setCapacity(8);

        elevator3 = new ElevatorCar();
        elevator3.setId("3");
        elevator3.setCurrentFloor(5);
        elevator3.setIsMoving(false);
        elevator3.setDestinationFloor(5);
        elevator3.setInsidePeople(0);
        elevator3.setCapacity(8);

        availableElevators = Arrays.asList(elevator2, elevator3);
    }

    @Test
    void testFindNearestAvailableElevatorCar_Success() {
        // Arrange
        when(elevatorRepository.findValidElevators(false)).thenReturn(availableElevators);

        // Act
        Optional<ElevatorCar> result = findElevatorCarService.findNearestAvailableElevatorCar(keypadRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("2", result.get().getId()); // Elevator 2 is closest to floor 3
        verify(elevatorRepository).findValidElevators(false);
    }

    @Test
    void testFindNearestAvailableElevatorCar_NoElevators() {
        // Arrange
        when(elevatorRepository.findValidElevators(false)).thenReturn(Collections.emptyList());

        // Act
        Optional<ElevatorCar> result = findElevatorCarService.findNearestAvailableElevatorCar(keypadRequest);

        // Assert
        assertTrue(result.isEmpty());
        verify(elevatorRepository).findValidElevators(false);
    }

    @Test
    void testFindNearestAvailableElevatorCar_SameDistance() {
        // Arrange
        ElevatorCar elevator4 = new ElevatorCar();
        elevator4.setId("4");
        elevator4.setCurrentFloor(2);
        elevator4.setIsMoving(false);
        elevator4.setDestinationFloor(2);
        elevator4.setInsidePeople(0);
        elevator4.setCapacity(8);

        List<ElevatorCar> elevators = Arrays.asList(elevator2, elevator4);
        when(elevatorRepository.findValidElevators(false)).thenReturn(elevators);

        // Act
        Optional<ElevatorCar> result = findElevatorCarService.findNearestAvailableElevatorCar(keypadRequest);

        // Assert
        assertTrue(result.isPresent());
        // Should return the first elevator found at the same distance
        assertEquals("2", result.get().getId());
        verify(elevatorRepository).findValidElevators(false);
    }

    @Test
    void testFindNearestAvailableElevatorCar_RepositoryError() {
        // Arrange
        when(elevatorRepository.findValidElevators(false)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                findElevatorCarService.findNearestAvailableElevatorCar(keypadRequest)
        );
        verify(elevatorRepository).findValidElevators(false);
    }
}
