package br.com.neainformatica.infrastructure.tools;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class JsonConverterDateTime {

    private static final SimpleDateFormat df = new SimpleDateFormat();

    public static class serialize extends JsonSerializer<Date> {

        @Override
        public void serialize(Date objeto, JsonGenerator gen, SerializerProvider arg2) throws IOException, JsonProcessingException {

            if ((((Date) objeto).getTime() + TimeZone.getDefault().getRawOffset()) % 86400000 == 0) {
                df.applyPattern("yyyy-MM-dd");
            } else {
                df.applyPattern("yyyy-MM-dd'T'HH:mm:ss");
            }

            gen.writeString(df.format((Date) objeto));

        }

    }

    public static class deserialize extends JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException, JsonProcessingException {

            try {
                String dateAsString = jsonparser.getText();

                if (dateAsString.length() == 10) {
                    df.applyPattern("yyyy-MM-dd");
                } else {
                    df.applyPattern("yyyy-MM-dd'T'HH:mm:ss");
                }

                return df.parse(dateAsString);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
                return null;

            }

        }

    }

}