package uz.poster.integration.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String secret;
    //    private long accessTokenExpiry = 900000L;    // 15 minutes in ms
    private long accessTokenExpiry;    // dev da
    private long refreshTokenExpiry; // 7 days in ms
}
