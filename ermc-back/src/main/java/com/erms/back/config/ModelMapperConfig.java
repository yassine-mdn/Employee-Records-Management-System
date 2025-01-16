package com.erms.back.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.record.RecordModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper().registerModule(new RecordModule());
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        return modelMapper;
    }
}
