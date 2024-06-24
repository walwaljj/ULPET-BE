package com.overcomingroom.ulpet.member.controller;

import com.overcomingroom.ulpet.member.domain.dto.LoginRequestDto;
import com.overcomingroom.ulpet.member.domain.dto.MemberInfoResponseDto;
import com.overcomingroom.ulpet.member.domain.dto.SignUpRequestDto;
import com.overcomingroom.ulpet.member.domain.dto.UpdateMemberRequestDto;
import com.overcomingroom.ulpet.member.service.MemberService;
import com.overcomingroom.ulpet.response.ResResult;
import com.overcomingroom.ulpet.response.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @PostMapping("/signup")
  public ResponseEntity<ResResult> signup(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
    Long memberId = memberService.signUp(signUpRequestDto);
    ResponseCode responseCode = ResponseCode.MEMBER_SIGN_UP_SUCCESS;
    return ResponseEntity.ok(
        ResResult.builder()
            .responseCode(responseCode)
            .code(responseCode.getCode())
            .message(responseCode.getMessage())
            .data(memberId)
            .build()
    );
  }

  @PostMapping("/login")
  public ResponseEntity<ResResult> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
    ResponseCode responseCode = ResponseCode.LOGIN_SUCCESSFUL;
    var jwt = memberService.login(loginRequestDto);
    return ResponseEntity.ok(
        ResResult.builder()
            .responseCode(responseCode)
            .code(responseCode.getCode())
            .message(responseCode.getMessage())
            .data(jwt)
            .build()
    );
  }

  @PostMapping("/reissue")
  public ResponseEntity<ResResult> reissue(HttpServletRequest request) {
    ResponseCode responseCode = ResponseCode.TOKEN_REISSUE_SUCCESS;
    var jwt = memberService.reissueToken(request);
    return ResponseEntity.ok(
        ResResult.builder()
            .responseCode(responseCode)
            .code(responseCode.getCode())
            .message(responseCode.getMessage())
            .data(jwt)
            .build()
    );
  }

  @GetMapping("/{userId}")
  public ResponseEntity<ResResult> getMemberInfo(@PathVariable Long userId) {
    ResponseCode responseCode = ResponseCode.MEMBER_INFO_GET_SUCCESSFUL;
    MemberInfoResponseDto memberInfoResponseDto = memberService.getMemberInfo(userId);
    return ResponseEntity.ok(
        ResResult.builder()
            .responseCode(responseCode)
            .code(responseCode.getCode())
            .message(responseCode.getMessage())
            .data(memberInfoResponseDto)
            .build()
    );
  }

  @DeleteMapping("/{userId}/withdrawal")
  public ResponseEntity<ResResult> withdrawalMember(@PathVariable Long userId) {
    ResponseCode responseCode = ResponseCode.MEMBER_WITHDRAWAL_SUCCESS;
    memberService.withdrawalMember(userId);
    return ResponseEntity.ok(
        ResResult.builder()
            .responseCode(responseCode)
            .code(responseCode.getCode())
            .message(responseCode.getMessage())
            .build()
    );
  }

  @PutMapping("/{userId}/profile")
  public ResponseEntity<ResResult> updateMember(@PathVariable Long userId, @Valid @RequestBody
      UpdateMemberRequestDto updateMemberRequestDto) {
    ResponseCode responseCode = ResponseCode.MEMBER_UPDATE_SUCCESS;
    Long memberId = memberService.updateMember(userId, updateMemberRequestDto);
    return ResponseEntity.ok(
        ResResult.builder()
            .responseCode(responseCode)
            .code(responseCode.getCode())
            .message(responseCode.getMessage())
            .data(memberId)
            .build()
    );
  }

  @PostMapping("/password")
  public ResponseEntity<ResResult> sendPasswordMail(@RequestParam String username) {
    ResponseCode responseCode = ResponseCode.EMAIL_SEND_SUCCESS;
    memberService.sendPasswordEmail(username);
    return ResponseEntity.ok(
        ResResult.builder()
            .responseCode(responseCode)
            .code(responseCode.getCode())
            .message(responseCode.getMessage())
            .data(username)
            .build()
    );
  }
}
