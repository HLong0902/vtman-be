package com.viettel.vtman.cms.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "webconfig")
@Data
public class ApplicationConfigurationProp {

    private String serverAddress;
    private String environmentUploadFile;
    private String pathUploadFileExtention;
    private String pathUpload;
    private String pathDownload;

}
