package eu.bsinfo.web.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.bsinfo.db.models.Reading;

import java.util.List;

public class ReadingsDto {
    private List<Reading> readings;

    @JsonCreator
    public ReadingsDto(@JsonProperty("readings")List<Reading> readings) {
        this.readings = readings;
    }

    public List<Reading> getReadings() {
        return readings;
    }
    public void setReadings(List<Reading> readings) {
        this.readings = readings;
    }
}
