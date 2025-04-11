package com.bluestaq.elevator.datafetchers;

import com.bluestaq.elevator.codegen.types.ElevatorCar;
import com.bluestaq.elevator.codegen.types.KeypadRequest;
import com.bluestaq.elevator.repository.ElevatorRespository;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@DgsComponent
public class FindElevatorCarDatafetcher {

    @Autowired
    private ElevatorRespository elevatorRepository;

    @DgsQuery(field = "findElevatorCar")
    public Optional<ElevatorCar> getFindElevatorCar(
            KeypadRequest keypadRequest) {

        int startingFloor = keypadRequest.getStartingFloor();
        int endingFloor = keypadRequest.getEndingFloor();

        Optional<ElevatorCar> elevatorCar = elevatorRepository.findValidElevators(startingFloor, endingFloor, false).stream().findFirst();
        return elevatorCar;
    }
}
