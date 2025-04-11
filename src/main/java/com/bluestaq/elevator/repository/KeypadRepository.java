package com.bluestaq.elevator.repository;

import com.bluestaq.elevator.codegen.types.KeypadRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeypadRepository extends MongoRepository<KeypadRequest, String> {
}
