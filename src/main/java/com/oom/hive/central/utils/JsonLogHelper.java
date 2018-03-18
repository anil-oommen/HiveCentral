package com.oom.hive.central.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
@Component
public class JsonLogHelper {

    /*HttpMessageConverter mappingJackson2HttpMessageConverter;
    public JsonLogHelper(){

    }

    protected String json(Object o) throws IOException {

        HttpOutputMessage

        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, );
        return mockHttpOutputMessage.getBodyAsString();
    }

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

    }*/


    /*public String toJSONString(Object o){
        HttpOutputMessage httpHttpOutputMessage = new HttpOutputMessage();
        this.jsonConverter().write(
                o, MediaType.APPLICATION_JSON, );
        return mockHttpOutputMessage.getBodyAsString();
    }

    @Bean
    public MappingJackson2HttpMessageConverter jsonConverter() {
        MappingJackson2HttpMessageConverter jacksonConverter = new
                MappingJackson2HttpMessageConverter();
        jacksonConverter.setSupportedMediaTypes(Arrays.asList(MediaType.valueOf("application/json")));
        jacksonConverter.setObjectMapper(jacksonObjectMapper());
        return jacksonConverter;
    }*/

    public String toJSONString(Object o)  {
        String beutifulJson = null;
        try {
            beutifulJson = jacksonObjectMapper().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return beutifulJson;
    }

    @Bean
    public ObjectMapper jacksonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }
}
