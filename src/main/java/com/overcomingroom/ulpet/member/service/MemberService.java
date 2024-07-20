package com.overcomingroom.ulpet.member.service;

import com.overcomingroom.ulpet.auth.domain.dto.JwtResponseDto;
import com.overcomingroom.ulpet.auth.service.JwtService;
import com.overcomingroom.ulpet.awsS3.AwsS3Service;
import com.overcomingroom.ulpet.exception.CustomException;
import com.overcomingroom.ulpet.exception.ErrorCode;
import com.overcomingroom.ulpet.member.domain.dto.LoginRequestDto;
import com.overcomingroom.ulpet.member.domain.dto.MemberInfoResponseDto;
import com.overcomingroom.ulpet.member.domain.dto.SignUpRequestDto;
import com.overcomingroom.ulpet.member.domain.dto.TempPasswordAndNickname;
import com.overcomingroom.ulpet.member.domain.dto.UpdateMemberRequestDto;
import com.overcomingroom.ulpet.member.domain.entity.Member;
import com.overcomingroom.ulpet.member.domain.entity.MemberEntity;
import com.overcomingroom.ulpet.member.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

  private final MemberRepository memberRepository;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final JavaMailSender javaMailSender;
  private final AwsS3Service awsS3Service;
  private final String MEMBER_PROFILE_PATH = "member-profile/";
  @Value("${spring.mail.username}")
  private String id;

  public void sendPasswordEmail(String username) {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    TempPasswordAndNickname tempPasswordAndNickname = findPassword(username);
    try {
      mimeMessage.addRecipients(MimeMessage.RecipientType.TO, username);
      mimeMessage.setSubject("[ULPET] 임시 비밀번호 발급");

      String message = "<!DOCTYPE html>" +
          "<html xmlns:th=\"http://www.thymeleaf.org\">" +
          "<body>" +
          "<div style=\"margin:100px;\">" +
          "<h1> 안녕하세요. " + tempPasswordAndNickname.nickname() + "님</h1>" +
          "<h1> 울산의 반려동물 동반 장소 인증 플랫폼 <span class=\"h1\" style=\"color: #0f7e89\">ULPET</span> 입니다.</h1>" +
          "<br>" +
          "<p> 임시 비밀번호를 발급드립니다. 아래 발급된 비밀번호로 로그인해주세요. </p>" +
          "<br>" +
          "<div align=\"center\" style=\"border:1px solid black; font-family:verdana;\">" +
          "<h3 style=\"color:blue\"> 임시 비밀번호 입니다. </h3>" +
          "<div style=\"font-size:130%\">" + tempPasswordAndNickname.tempPassword() + "</div>" +
          "<br>" +
          "</div>" +
          "<br/>" +
          "</div>" +
          "</body>" +
          "</html>";

      mimeMessage.setText(message, "utf-8", "html");
      mimeMessage.setFrom(new InternetAddress(id, "울펫_관리자"));
      javaMailSender.send(mimeMessage);
    } catch (MessagingException | UnsupportedEncodingException e) {
      throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
    }
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
  }

  public Member getAuthenticatedUser(Long memberId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var memberEntity = (MemberEntity) authentication.getPrincipal();
    var member = Member.from(memberEntity);
    if (!Objects.equals(memberId, memberEntity.getMemberId())) {
      throw new CustomException(ErrorCode.MEMBER_INVALID);
    }
    return member;
  }

  public Long signUp(SignUpRequestDto signUpRequestDto) {
    memberRepository
        .findByUsername(signUpRequestDto.username())
        .ifPresent(
            member -> {
              throw new CustomException(ErrorCode.MEMBER_ALREADY_EXIST);
            }
        );

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("lang", "ko");
    HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> responseEntity = restTemplate.postForEntity("https://www.rivestsoft.com/nickname/getRandomNickname.ajax", requestEntity, String.class);
    JSONObject jsonObject = new JSONObject(responseEntity.getBody());
    String nickname = jsonObject.get("data").toString();

    var memberEntity = memberRepository.save(
        MemberEntity.of(
            signUpRequestDto.username(),
            passwordEncoder.encode(signUpRequestDto.password()),
            nickname,
            "https://avatar.iran.liara.run/public/" + (new Random().nextInt(100) + 1))
    );
    return Member.from(memberEntity).memberId();
  }

  public JwtResponseDto login(LoginRequestDto loginRequestDto) {
    var member = loadUserByUsername(loginRequestDto.username());

    if (passwordEncoder.matches(loginRequestDto.password(), member.getPassword())) {
      return JwtResponseDto.builder()
          .accessToken(jwtService.generateAccessToken(member))
          .refreshToken(jwtService.generateRefreshToken(member))
          .build();
    } else {
      throw new CustomException(ErrorCode.LOGIN_ERROR);
    }
  }

  public Long getAuthenticatedUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var memberEntity = (MemberEntity) authentication.getPrincipal();
    return memberEntity.getMemberId();
  }

  public JwtResponseDto reissueToken(HttpServletRequest request) {
    var refreshToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
    var member = loadUserByUsername(jwtService.getUsername(refreshToken));
    return JwtResponseDto.builder()
        .accessToken(jwtService.generateAccessToken(member))
        .refreshToken(jwtService.generateRefreshToken(member))
        .build();
  }

    /**
     * 사용자의 프로필 수정 (PW, Nickname, profile)
     * @param memberId
     * @param updateMemberRequestDto
     * @param multipartFile
     * @return
     * @throws IOException
     */
  public Long updateMember(Long memberId, UpdateMemberRequestDto updateMemberRequestDto, MultipartFile multipartFile) throws IOException {
    var member = getAuthenticatedUser(memberId);

    // 이미지가 비어있는 경우
    if(multipartFile.isEmpty()){
      throw new CustomException(ErrorCode.NO_IMAGE);
    }
    // 이미지 파일이 비어있지 않고, 원래 이미지 url이 랜덤 이미지가 아니라면? S3에서 이미지를 삭제해야함.
    else if(!multipartFile.isEmpty() && !member.profileImage().contains("https://avatar.iran.liara.run/public")){
      awsS3Service.deleteFile(member.profileImage());
    }

    // s3에 이미지 업로드
    String profileImageUrl = awsS3Service.upload(multipartFile, MEMBER_PROFILE_PATH + member.memberId());

    var memberEntity = memberRepository.save(
        MemberEntity.of(
            memberId,
            member.username(),
            passwordEncoder.encode(updateMemberRequestDto.password()),
            updateMemberRequestDto.nickname(),
            profileImageUrl,
            member.familiarity()
        )
    );

    return Member.from(memberEntity).memberId();
  }

  public MemberInfoResponseDto getMemberInfo(Long memberId) {
    var member = getAuthenticatedUser(memberId);

    return MemberInfoResponseDto.builder()
        .memberId(member.memberId())
        .username(member.username())
        .nickname(member.nickname())
        .profileImage(member.profileImage())
        .familiarity(member.familiarity())
        .build();
  }

  public void withdrawalMember(Long memberId,String password) throws UnsupportedEncodingException {
    var member = getAuthenticatedUser(memberId);

    // 비밀번호 검증 로직
    if(!passwordEncoder.matches(password,member.password())){
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }


    String filename = member.profileImage();
    // 사용자 삭제 시 S3에서 이미지 파일 삭제
    awsS3Service.deleteFile(filename);
    memberRepository.deleteById(member.memberId());
  }

  public TempPasswordAndNickname findPassword(String username) {
    String tempPassword = generateTempPassword();
    var member = memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    memberRepository.save(
        MemberEntity.of(
            member.getMemberId(),
            member.getUsername(),
            passwordEncoder.encode(tempPassword),
            member.getNickname(),
            member.getProfileImage(),
            member.getFamiliarity()
        )
    );
    return TempPasswordAndNickname.builder()
        .tempPassword(tempPassword)
        .nickname(member.getNickname())
        .build();
  }

  public static String generateTempPassword() {
    final String DIGITS = "0123456789";
    final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=<>?/{}~|";
    final String ALL_CHARACTERS = DIGITS + LETTERS + SPECIAL_CHARACTERS;
    final SecureRandom RANDOM = new SecureRandom();

    int length = RANDOM.nextInt(9) + 8; // Generates a length between 8 and 16
    StringBuilder password = new StringBuilder(length);

    password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
    password.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
    password.append(SPECIAL_CHARACTERS.charAt(RANDOM.nextInt(SPECIAL_CHARACTERS.length())));

    for (int i = 3; i < length; i++) {
      password.append(ALL_CHARACTERS.charAt(RANDOM.nextInt(ALL_CHARACTERS.length())));
    }

    List<Character> passwordChars = new ArrayList<>();
    for (char c : password.toString().toCharArray()) {
      passwordChars.add(c);
    }
    Collections.shuffle(passwordChars);

    StringBuilder shuffledPassword = new StringBuilder();
    for (char c : passwordChars) {
      shuffledPassword.append(c);
    }

    return shuffledPassword.toString();
  }

  public Boolean isEmailExist(String email) {
    return memberRepository.findByUsername(email).isPresent();
  }

  /**
   * 회원 접근 권한 확인, 확인 후 회원 정보 반환
   *
   * @param memberId
   * @param username
   * @return 회원 정보
   */
  public MemberEntity verifyMemberAccessAndRetrieve(Long memberId, String username) {
    MemberEntity member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    if (!memberRepository.findByUsername(username).get().equals(member)) {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }
    return member;
  }

}
