package com.bluestaq.elevator.client;

import com.bluestaq.elevator.codegen.types.BuildingKeypad;
import com.bluestaq.elevator.codegen.types.KeypadRequest;
import com.bluestaq.elevator.exceptions.FloorNotFoundException;
import com.bluestaq.elevator.exceptions.SameFloorException;
import com.bluestaq.elevator.repository.KeypadRepository;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.bluestaq.elevator.constants.ExceptionConstants.SAME_FLOOR_EXCEPTION_WORDING;
import static com.bluestaq.elevator.constants.KafkaConstants.BOOTSTRAP_SERVER_URL;
import static com.bluestaq.elevator.constants.KafkaConstants.KEYPAD_REQUEST_TOPIC;

@RestController
public class BuildingKeypadController {


    private final KafkaTemplate<String, KeypadRequest> kafkaTemplate;

    @Autowired
    private KeypadRepository keypadRepository;

    @Autowired
    public BuildingKeypadController(KafkaTemplate<String, KeypadRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/callElevator")
    public void callElevator(@RequestBody BuildingKeypad buildingKeypadFloor1,@RequestParam int destinationFloor) {

        checkValidDestination(buildingKeypadFloor1, destinationFloor);
        KeypadRequest keypadRequest = buildKeypadRequest(buildingKeypadFloor1, destinationFloor);
        kafkaTemplate.send(KEYPAD_REQUEST_TOPIC, keypadRequest);
        keypadRepository.save(buildMongoKeypadRequest(buildingKeypadFloor1, destinationFloor));

    }

    @NotNull
    private static MongoKeypadRequest buildMongoKeypadRequest(BuildingKeypad buildingKeypadFloor1, int destinationFloor) {
        //casting is needed in order to have mongo recognize the request and put it in a collection
        MongoKeypadRequest keypadRequest = new MongoKeypadRequest();
        keypadRequest.setStartingFloor(buildingKeypadFloor1.getCurrentFloor());
        keypadRequest.setEndingFloor(destinationFloor);
        return keypadRequest;
    }

    @NotNull
    private static KeypadRequest buildKeypadRequest(BuildingKeypad buildingKeypadFloor1, int destinationFloor) {
        KeypadRequest keypadRequest = new KeypadRequest();
        keypadRequest.setStartingFloor(buildingKeypadFloor1.getCurrentFloor());
        keypadRequest.setEndingFloor(destinationFloor);
        return keypadRequest;
    }

    private static void checkValidDestination(BuildingKeypad buildingKeypadFloor1, int destinationFloor) {
        if(buildingKeypadFloor1.getCurrentFloor() == destinationFloor)
        {
            throw new SameFloorException(SAME_FLOOR_EXCEPTION_WORDING);
        }
        buildingKeypadFloor1.getFloors().stream().filter(floor -> floor == destinationFloor).findFirst().orElseThrow(() -> new FloorNotFoundException("Floor not found"));
    }

    @Configuration
    public static class KafkaProducerConfig {

        @Bean
        public ProducerFactory<String, KeypadRequest> keypadProducerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER_URL); // Your Kafka brokers
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, KeypadRequest> keypadKafkaTemplate() {
            return new KafkaTemplate<>(keypadProducerFactory());
        }

        @Bean
        public NewTopic elevatorTopic() {
            return TopicBuilder.name(KEYPAD_REQUEST_TOPIC)
                    .partitions(3) // Adjust as needed
                    .replicas(1)   // Adjust as needed
                    .build();
        }
    }
}
