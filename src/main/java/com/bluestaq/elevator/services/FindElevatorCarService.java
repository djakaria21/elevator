package com.bluestaq.elevator.services;

import com.bluestaq.elevator.codegen.types.ElevatorCar;
import com.bluestaq.elevator.codegen.types.KeypadRequest;
import com.bluestaq.elevator.repository.ElevatorRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class FindElevatorCarService {

    @Autowired
    private ElevatorRespository elevatorRepository;

    public Optional<ElevatorCar> findNearestAvailableElevatorCar(
            KeypadRequest keypadRequest) {

        int startingFloor = keypadRequest.getStartingFloor();

        List<ElevatorCar> availableElevators = elevatorRepository.findValidElevators(false);

        if (availableElevators.isEmpty()) {
            return Optional.empty();
        }

        Optional<ElevatorCar> closestElevator = availableElevators.stream()
                .min(Comparator.comparingInt(elevator -> Math.abs(elevator.getCurrentFloor() - startingFloor)));

        return closestElevator;
    }
}
