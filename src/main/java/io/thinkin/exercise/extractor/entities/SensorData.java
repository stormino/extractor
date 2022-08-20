package io.thinkin.exercise.extractor.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SensorData {

    private Integer id;
    private Integer value;
    private Date timestamp;
}
