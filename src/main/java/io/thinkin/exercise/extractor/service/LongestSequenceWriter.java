package io.thinkin.exercise.extractor.service;

import io.thinkin.exercise.extractor.component.SequencePrintWriter;
import io.thinkin.exercise.extractor.dto.SensorData;
import io.thinkin.exercise.extractor.entities.mongo.SequenceEntity;
import io.thinkin.exercise.extractor.repository.SequenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
public class LongestSequenceWriter {

    private static final String FILENAME = "longestsequence.log";

    @Autowired
    SequenceRepository sequenceRepository;

    @Bean
    public Consumer<List<SensorData>> writeLongestSequence() {

        return d -> d.forEach(data -> {
            try {
                writeLongestSequenceToFile();
            } catch (Exception e) {
                log.error("Error writing file", e);
            }
        });
    }

    private void writeLongestSequenceToFile() throws IOException {

        SequenceEntity s = sequenceRepository.retrieveFirstLongestSequence();
        if (s.getValues().size() > valuesSizeOnFile()) {
            updateFileWithSequence(s);
        }
    }

    private void updateFileWithSequence(SequenceEntity sequence) throws IOException {

        File file = Paths.get(FILENAME).toFile();
        try (FileWriter fileWriter = new FileWriter(file);
             SequencePrintWriter writer = new SequencePrintWriter(fileWriter)) {
            writer.writeSequence(sequence);
            log.info("Longest sequence file written [file={},sequenceSize={}]",
                    file.getCanonicalPath(),
                    sequence.getValues().size());
        }
    }

    private Integer valuesSizeOnFile() throws IOException {

        File file = Paths.get(FILENAME).toFile();
        if (!file.exists()) {
            return 0;
        } else {
            try (InputStream inputStream = new FileInputStream(file);
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                 LineNumberReader lineNumberReader = new LineNumberReader(inputStreamReader)) {
                int result = 0;
                String line = lineNumberReader.readLine();
                while (line != null) {
                    line = lineNumberReader.readLine();
                    result++;
                }
                return result;
            }
        }
    }
}
