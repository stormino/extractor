package io.thinkin.exercise.extractor.component;

import io.thinkin.exercise.extractor.entities.mongo.SequenceEntity;

import java.io.PrintWriter;
import java.io.Writer;

public class SequencePrintWriter extends PrintWriter {

    public SequencePrintWriter(Writer out) {

        super(out);
    }

    public void writeSequence(SequenceEntity sequence) {

        sequence.getValues().forEach(
            v -> println(String.format("%d,%d,%d", sequence.getSensorId(), v.getValue(), v.getTimestamp().getTime()))
        );
    }
}
