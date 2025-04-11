package com.bluestaq.elevator.repository;

import com.bluestaq.elevator.codegen.types.ElevatorCar;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElevatorRespository extends MongoRepository<ElevatorCar, String> {

    List<ElevatorCar> findValidElevators(boolean isMoving);
}
