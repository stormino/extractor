package io.thinkin.exercise.extractor.repository;

import io.thinkin.exercise.extractor.entities.mongo.SensorValueEntity;
import io.thinkin.exercise.extractor.entities.mongo.SequenceEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SequenceRepository {

    Optional<SequenceEntity> getLastSequenceBySensorId(Integer sensorId);
    void addValueToMostRecentSequence(Integer sensorId, SensorValueEntity value);
    void startNewSequence(Integer sensorId, SensorValueEntity value);
    List<SequenceEntity> retrieveOverlappingSequences(Integer sensorId, Date dateFrom, Date dateTo);
    SequenceEntity retrieveFirstLongestSequence();
}
