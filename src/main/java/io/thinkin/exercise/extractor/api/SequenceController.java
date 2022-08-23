package io.thinkin.exercise.extractor.api;

import io.thinkin.exercise.extractor.dto.Sequence;
import io.thinkin.exercise.extractor.dto.SequenceFilter;
import io.thinkin.exercise.extractor.service.SensorSequenceExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SequenceController {

    @Autowired
    SensorSequenceExtractor sensorSequenceExtractor;

    @PostMapping("/sequence")
    public List<Sequence> getSequences(@RequestBody SequenceFilter filter) {

        return sensorSequenceExtractor.retrieveOverlappingSequences(
                filter.getSensorId(),
                filter.getDateFrom(),
                filter.getDateTo());
    }
}
