package uz.poster.integration.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "playmobile")
@Getter
@Setter
public class PlayMobileProperties {
    private String originator;
    private String username;
    private String password;
    private String url;
}
