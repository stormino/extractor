package io.thinkin.exercise.extractor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SequenceFilter {

    private Integer sensorId;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "s")
    private Date dateFrom;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "s")
    private Date dateTo;
}
