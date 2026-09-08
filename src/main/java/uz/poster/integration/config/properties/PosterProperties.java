package uz.poster.integration.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "poster")
public record PosterProperties(
        String baseUrl,
        String token,        // shaxsiy token (Access -> Integrations) - agar OAuth ishlatmasangiz
        Integer spotId,
        Long applicationId,      // Poster for Developers -> ilovangiz ID'si
        String applicationSecret // MUHIM: webhook 'verify' hash'i shu secret bilan hisoblanadi (o'zingiz o'ylab topgan emas!)
) {
}
