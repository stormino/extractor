package io.thinkin.exercise.extractor.service;

import io.thinkin.exercise.extractor.dto.SensorData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Slf4j
@Service
public class Logic {

    @Bean
    public Consumer<SensorData> log() {
        return item -> {
            log.info("Received: " + item);
        };
    }
}
