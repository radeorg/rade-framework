package org.dows.rade.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;


@Slf4j
public class TimeMillisToDateDeserializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        try {
            String text = jsonParser.getText();
            return new Date(Long.parseLong(text));
        } catch (IOException e) {
            log.error("[JsonDeserializer]TimeMillisToDate exception", e);
        }
        return null;
    }
}
