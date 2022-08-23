package io.thinkin.exercise.extractor.service;

import io.thinkin.exercise.extractor.dto.SensorData;
import io.thinkin.exercise.extractor.dto.SensorValue;
import io.thinkin.exercise.extractor.dto.Sequence;
import io.thinkin.exercise.extractor.entities.mongo.SensorValueEntity;
import io.thinkin.exercise.extractor.entities.mongo.SequenceEntity;
import io.thinkin.exercise.extractor.repository.SequenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SensorSequenceExtractor {

    @Autowired
    SequenceRepository sequenceRepository;

    @Bean
    public Function<List<SensorData>, List<SensorData>> sequenceExtractor() {

        return data -> {
            log.info("Received: " + data);
            data.forEach(this::handleReceivedSensorValue);
            return data;
        };
    }

    private void handleReceivedSensorValue(SensorData sensorData) {

        // Getting the most recent sequence for this sensor, if any
        log.info("Handling sensor data: {}", sensorData);
        Optional<SequenceEntity> s = sequenceRepository.getLastSequenceBySensorId(sensorData.getSensorId());
        s.ifPresentOrElse(
                // If sequence for this sensor have been already extracted
                // adding an increasing value to this sequence, otherwise starting a new sequence
                sequence -> {
                    // Retrieving max value of the last sequence saved
                    log.info("Last sequence for sensor {}: {}", sensorData.getSensorId(), sequence.getValues());
                    SensorValueEntity max = sequence.getValues()
                            .stream()
                            .max(Comparator.comparing(SensorValueEntity::getValue))
                            .orElseThrow(() -> new IllegalStateException("Error: sequence with no values"));
                    log.info("Max value: {}", max);
                    if (sensorData.getValue() > max.getValue()) {
                        // Adding current value to this sequence as it's increasing
                        sequenceRepository.addValueToMostRecentSequence(sequence.getSensorId(), SensorValueEntity.of(sensorData));
                        log.info("Added value to existing sequence");
                    } else {
                        // Creating a new sequence
                        sequenceRepository.startNewSequence(sensorData.getSensorId(), SensorValueEntity.of(sensorData));
                        log.info("Started a new sequence");
                    }
                },
                // Starting a new sequence as this sensor hasn't been already saved
                () -> {
                    sequenceRepository.startNewSequence(sensorData.getSensorId(), SensorValueEntity.of(sensorData));
                    log.info("Started a new sequence");
                }
        );
    }

    public List<Sequence> retrieveOverlappingSequences(Integer sensorId, Date dateFrom, Date dateTo) {

        return sequenceRepository.retrieveOverlappingSequences(sensorId, dateFrom, dateTo).stream()
                .map(s -> Sequence.builder()
                        .withValues(s.getValues().stream()
                                .map(v -> SensorValue.builder()
                                        .withValue(v.getValue())
                                        .withTimestamp(v.getTimestamp())
                                        .build())
                                .collect(Collectors.toList()))
                        .withSensorId(s.getSensorId())
                        .build())
                .collect(Collectors.toList());
    }
}
