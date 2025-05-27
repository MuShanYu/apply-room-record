package top.mushanyu.web.config.converter;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public class Jackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    public Jackson2HttpMessageConverter() {
        setObjectMapper(new WebObjectMapper());
    }
}
