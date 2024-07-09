package com.overcomingroom.ulpet.base;

import com.overcomingroom.ulpet.member.domain.entity.MemberEntity;
import com.overcomingroom.ulpet.member.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@Profile({"dev", "test"})
public class InitData {

    @Bean
    CommandLineRunner init(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {

        String encodePassword = passwordEncoder.encode("abc1234!");

        return args -> {

            Optional<MemberEntity> optionalTest1 = memberRepository.findByUsername("test1@ulpet.com");
            Optional<MemberEntity> optionalTest2 = memberRepository.findByUsername("test2@ulpet.com");

            if (optionalTest1.isEmpty()) {
                MemberEntity test1 = MemberEntity.builder()
                        .username("test1@ulpet.com")
                        .password(encodePassword)
                        .nickname("test1")
                        .familiarity(0.0f)
                        .profileImage("https://avatar.iran.liara.run/public/1")
                        .build();
                memberRepository.save(test1);
            }

            if (optionalTest2.isEmpty()) {
                MemberEntity test2 = MemberEntity.builder()
                        .username("test2@ulpet.com")
                        .password(encodePassword)
                        .nickname("test2")
                        .familiarity(0.0f)
                        .profileImage("https://avatar.iran.liara.run/public/2")
                        .build();
                memberRepository.save(test2);
            }
        };
    }
}