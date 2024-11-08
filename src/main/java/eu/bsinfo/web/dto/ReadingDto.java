package eu.bsinfo.web.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.bsinfo.db.models.Reading;

public class ReadingDto {
    private Reading reading;

    @JsonCreator
    public ReadingDto(@JsonProperty("reading") Reading reading) {
        this.reading = reading;
    }

    public Reading getReading() {
        return reading;
    }
    public void setReading(Reading reading) {
        this.reading = reading;
    }
}
