package com.spring.util.yml;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
// yml 파일에서 가져올 변수 이름을 명시해준다.
@ConfigurationProperties(prefix = "geocoding")
@Setter
@Getter
public class ApplicationGeocoding {
    private String key;
    private String keyid;
    private String clientid;
    private String clientsecret;
}