package com.overcomingroom.ulpet.certification.controller;

import com.overcomingroom.ulpet.certification.domain.dto.CertificationRequestDto;
import com.overcomingroom.ulpet.certification.service.CertificationService;
import com.overcomingroom.ulpet.response.ResResult;
import com.overcomingroom.ulpet.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping(value = "/place/certification", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "장소 인증", description = "사용자가 사진을 이용한 장소 인증을 합니다.")
    public ResponseEntity<ResResult> certification(
            @RequestPart(value = "multipartFile") MultipartFile multipartFile,
            @RequestPart(value = "certificationRequestDto") CertificationRequestDto certificationRequestDto,
            @Parameter(hidden = true) Authentication auth
    ) throws IOException {

        String username = auth.getName();
        ResponseCode resultCode = ResponseCode.CERTIFICATION_REGISTRATION_SUCCESSFUL;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(resultCode)
                        .code(resultCode.getCode())
                        .message(resultCode.getMessage())
                        .data(certificationService.certification(username, multipartFile, certificationRequestDto))
                        .build()
        );
    }

    @GetMapping(value = "/certification/{certificationId}")
    @Operation(summary = "인증 상세", description = "인증 상세 보기")
    public ResponseEntity<ResResult> certificationDetail(
            @PathVariable(value = "certificationId") Long certificationId,
            @Parameter(hidden = true) Authentication auth
    ) {

        String username = auth.getName();
        ResponseCode resultCode = ResponseCode.CERTIFICATION_DETAIL_READ_SUCCESSFUL;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(resultCode)
                        .code(resultCode.getCode())
                        .message(resultCode.getMessage())
                        .data(certificationService.certificationDetail(certificationId, username))
                        .build()
        );
    }

    @GetMapping(value = "/places/{placeId}/certifications")
    @Operation(summary = "장소에 대한 인증 리스트", description = "장소에 대한 인증 리스트 보기")
    public ResponseEntity<ResResult> certificationListForPlace(
            @PathVariable(value = "placeId") Long placeId
    ) {

        ResponseCode resultCode = ResponseCode.CERTIFICATION_LIST_FOR_PLACE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(resultCode)
                        .code(resultCode.getCode())
                        .message(resultCode.getMessage())
                        .data(certificationService.certificationListForPlace(placeId))
                        .build()
        );
    }


    @GetMapping(value = "/users/{userId}/certification")
    @Operation(summary = "유저의 인증 리스트", description = "유저의 인증 리스트")
    public ResponseEntity<ResResult> userCertificationList(
            @PathVariable(value = "userId") Long userId,
            @Parameter(hidden = true) Authentication auth
    ) {

        String username = auth.getName();
        ResponseCode resultCode = ResponseCode.USER_CERTIFICATION_LIST;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(resultCode)
                        .code(resultCode.getCode())
                        .message(resultCode.getMessage())
                        .data(certificationService.userCertificationList(userId, username))
                        .build()
        );
    }


    @DeleteMapping(value = "/certification/{certificationId}")
    @Operation(summary = "인증 삭제", description = "인증 삭제")
    public ResponseEntity<ResResult> certificationDelete(
            @PathVariable(value = "certificationId") Long certificationId,
            @Parameter(hidden = true) Authentication auth
    ) throws UnsupportedEncodingException {

        String username = auth.getName();
        certificationService.certificationDelete(certificationId, username, true);
        ResponseCode resultCode = ResponseCode.CERTIFICATION_DELETE_SUCCESSFUL;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(resultCode)
                        .code(resultCode.getCode())
                        .message(resultCode.getMessage())
                        .build()
        );
    }

}
