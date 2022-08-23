package io.thinkin.exercise.extractor.entities.mongo;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Builder(setterPrefix = "with")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document("sequence")
public class SequenceEntity {

    private Integer sensorId;

    private List<SensorValueEntity> values;
}
