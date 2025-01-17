package com.erms.back.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "pdf")
public class PdfConfigProperties {
    @NotNull
    String directory;
    @NotNull
    String fileName;
    String companyLogo;
}
