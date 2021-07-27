package com.ezcoins.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 入参保留x位小数
 */
public class SerializerBigDecimal extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if(Objects.isNull(value)) {
            gen.writeNull();
        } else {
            gen.writeNumber(value.setScale(8, RoundingMode.FLOOR));
        }
    }
}