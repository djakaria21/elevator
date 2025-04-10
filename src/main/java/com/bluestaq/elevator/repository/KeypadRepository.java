package com.bluestaq.elevator.repository;

import com.bluestaq.elevator.codegen.types.KeypadRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface KeypadRepository extends MongoRepository<KeypadRequest, String> {
}
