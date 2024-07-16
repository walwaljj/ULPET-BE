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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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

    @PostMapping("/check-email")
    public ResponseEntity<ResResult> isEmailExist(@RequestParam String email) {
        var isEmailExist = memberService.isEmailExist(email);
        ResponseCode responseCode = ResponseCode.MEMBER_EMAIL_CHECK_SUCCESS;
        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(isEmailExist)
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

    @GetMapping("/userId")
    public ResponseEntity<ResResult> getUserId() {
        ResponseCode responseCode = ResponseCode.MEMBER_ID_GET_SUCCESS;
        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.getAuthenticatedUserId())
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
    public ResponseEntity<ResResult> getMemberInfo(@PathVariable("userId") Long userId) {
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
    public ResponseEntity<ResResult> withdrawalMember(@PathVariable("userId") Long userId) throws UnsupportedEncodingException {
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

    @PutMapping(value = "/{userId}/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResResult> updateMember(
            @PathVariable("userId") Long userId,
            @Valid @RequestPart(value = "updateMemberRequestDto") UpdateMemberRequestDto updateMemberRequestDto,
            @RequestPart(value = "profileImage", required = false) MultipartFile multipartFile
            ) throws IOException {
        ResponseCode responseCode = ResponseCode.MEMBER_UPDATE_SUCCESS;
        Long memberId = memberService.updateMember(userId, updateMemberRequestDto, multipartFile);
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
