package com.sunyoungeom.booktalk;

import com.sunyoungeom.booktalk.repository.MemoryReviewRepository;
import com.sunyoungeom.booktalk.repository.ReviewRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ReviewRepository reviewRepository() {
        return new MemoryReviewRepository();
    }
}
