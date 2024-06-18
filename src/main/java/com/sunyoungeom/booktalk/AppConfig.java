package com.sunyoungeom.booktalk;

import com.sunyoungeom.booktalk.repository.MemoryReviewRepository;
import com.sunyoungeom.booktalk.repository.MemoryUserRepository;
import com.sunyoungeom.booktalk.repository.ReviewRepository;
import com.sunyoungeom.booktalk.repository.UserRepository;
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

    @Bean
    public UserRepository userRepository() {
        return new MemoryUserRepository();
    }
}
