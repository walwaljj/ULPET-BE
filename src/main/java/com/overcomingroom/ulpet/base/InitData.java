package com.overcomingroom.ulpet.base;

import com.overcomingroom.ulpet.member.domain.entity.MemberEntity;
import com.overcomingroom.ulpet.member.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "test"})
public class InitData {

    @Bean
    CommandLineRunner init(MemberRepository memberRepository) {
        return args -> {
            MemberEntity test1 = MemberEntity.builder()
                    .email("test1@ulpet.com")
                    .password("1234")
                    .nickname("test1")
                    .build();

            MemberEntity test2 = MemberEntity.builder()
                    .email("test2@ulpet.com")
                    .password("1234")
                    .nickname("test2")
                    .build();

            memberRepository.save(test1);
            memberRepository.save(test2);
        };
    }
}
