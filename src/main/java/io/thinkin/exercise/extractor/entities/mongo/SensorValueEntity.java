package io.thinkin.exercise.extractor.entities.mongo;

import io.thinkin.exercise.extractor.dto.SensorData;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.Date;

@Builder(setterPrefix = "with")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SensorValueEntity {

    Integer value;
    Date timestamp;

    public static SensorValueEntity of(@Nullable SensorData sensorData) {

        return sensorData != null ?
                SensorValueEntity.builder()
                        .withValue(sensorData.getValue())
                        .withTimestamp(sensorData.getTimestamp())
                        .build() :
                null;
    }
}
