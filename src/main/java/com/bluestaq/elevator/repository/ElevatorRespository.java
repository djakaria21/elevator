package com.bluestaq.elevator.repository;

import com.bluestaq.elevator.codegen.types.ElevatorCar;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElevatorRespository extends MongoRepository<ElevatorCar, String> {

    @Query("{'isMoving': false}")
    List<ElevatorCar> findValidElevators(boolean isMoving);
}
