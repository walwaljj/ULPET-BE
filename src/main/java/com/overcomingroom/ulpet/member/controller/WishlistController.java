package com.overcomingroom.ulpet.member.controller;

import com.overcomingroom.ulpet.member.service.WishlistService;
import com.overcomingroom.ulpet.response.ResResult;
import com.overcomingroom.ulpet.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Slf4j
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/places/{placeId}/wishlist")
    @Operation(summary = "장소를 위시 리스트에 추가 ", description = "장소를 위시 리스트에 추가")
    public ResponseEntity<ResResult> placeAddedToWishlist(
            @PathVariable("placeId") Long placeId,
            @RequestParam(value = "memberId", required = false) Long memberId,
            @Parameter(hidden = true) Authentication auth
    ) {

        String name = auth.getName();

        ResponseCode resultCode = ResponseCode.PLACE_ADDED_TO_WISHLIST;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(resultCode)
                        .code(resultCode.getCode())
                        .message(resultCode.getMessage())
                        .data(wishlistService.placeAddedToWishlist(placeId, memberId, name))
                        .build()
        );
    }

    @DeleteMapping("/places/{placeId}/wishlist")
    @Operation(summary = "장소를 위시 리스트에서 삭제 ", description = "장소를 위시 리스트에서 삭제")
    public ResponseEntity<ResResult> placeRemovedFromWishlist(
            @PathVariable("placeId") Long placeId,
            @RequestParam(value = "memberId", required = false) Long memberId,
            @Parameter(hidden = true) Authentication auth
    ) {

        wishlistService.placeRemovedFromWishlist(placeId, memberId, auth.getName());

        ResponseCode resultCode = ResponseCode.PLACE_REMOVED_FROM_WISHLIST;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(resultCode)
                        .code(resultCode.getCode())
                        .message(resultCode.getMessage())
                        .build()
        );
    }

    @GetMapping("/members/{memberId}/wishlist")
    @Operation(summary = "위시리스트 목록", description = "유저의 위시리스트를 보여줍니다")
    public ResponseEntity<ResResult> wishlist(
            @PathVariable(value = "memberId", required = false) Long memberId,
            @Parameter(hidden = true) Authentication auth
    ) {

        ResponseCode resultCode = ResponseCode.WISHLIST_READ_SUCCESSFUL;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(resultCode)
                        .code(resultCode.getCode())
                        .message(resultCode.getMessage())
                        .data(wishlistService.wishlist(memberId, auth.getName()))
                        .build()
        );
    }
}
