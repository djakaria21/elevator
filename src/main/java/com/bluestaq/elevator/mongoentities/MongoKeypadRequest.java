package com.bluestaq.elevator.client;

import com.bluestaq.elevator.codegen.types.KeypadRequest;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Keypad")
@TypeAlias("KeypadRequest")
public class MongoKeypadRequest extends KeypadRequest {
    public MongoKeypadRequest() {
        super();
    }
}
