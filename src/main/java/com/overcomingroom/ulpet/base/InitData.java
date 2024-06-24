package com.overcomingroom.ulpet.base;

import com.overcomingroom.ulpet.user.domain.entity.Users;
import com.overcomingroom.ulpet.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "test"})
public class InitData {

    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            Users test1 = Users.builder()
                    .email("test1@ulpet.com")
                    .password("1234")
                    .nickname("test1")
                    .build();

            Users test2 = Users.builder()
                    .email("test2@ulpet.com")
                    .password("1234")
                    .nickname("test2")
                    .build();

            userRepository.save(test1);
            userRepository.save(test2);
        };
    }
}
