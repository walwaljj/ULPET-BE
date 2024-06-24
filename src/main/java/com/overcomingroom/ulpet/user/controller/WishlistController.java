package com.overcomingroom.ulpet.user.controller;

import com.overcomingroom.ulpet.response.ResResult;
import com.overcomingroom.ulpet.response.ResponseCode;
import com.overcomingroom.ulpet.user.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/places/{placeId}/wishlist")
    @Operation(summary = "장소를 위시 리스트에 추가 ", description = "장소를 위시 리스트에 추가")
    public ResponseEntity<ResResult> placeAddedToWishlist(
            @PathVariable("placeId") Long placeId,
            @RequestParam(value = "userId", required = false) Long userId
    ) {

        ResponseCode resultCode = ResponseCode.PLACE_ADDED_TO_WISHLIST;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(resultCode)
                        .code(resultCode.getCode())
                        .message(resultCode.getMessage())
                        .data(wishlistService.placeAddedToWishlist(placeId, userId))
                        .build()
        );
    }

    @DeleteMapping("/places/{placeId}/wishlist")
    @Operation(summary = "장소를 위시 리스트에서 삭제 ", description = "장소를 위시 리스트에서 삭제")
    public ResponseEntity<ResResult> placeRemovedFromWishlist(
            @PathVariable("placeId") Long placeId,
            @RequestParam(value = "userId", required = false) Long userId
    ) {

        wishlistService.placeRemovedFromWishlist(placeId, userId);

        ResponseCode resultCode = ResponseCode.PLACE_REMOVED_FROM_WISHLIST;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(resultCode)
                        .code(resultCode.getCode())
                        .message(resultCode.getMessage())
                        .build()
        );
    }

    @GetMapping("/users/{userId}/wishlist")
    @Operation(summary = "위시리스트 목록", description = "유저의 위시리스트를 보여줍니다")
    public ResponseEntity<ResResult> wishlist(
            @PathVariable(value = "userId", required = false) Long userId
    ) {

        ResponseCode resultCode = ResponseCode.WISHLIST_READ_SUCCESSFUL;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(resultCode)
                        .code(resultCode.getCode())
                        .message(resultCode.getMessage())
                        .data(wishlistService.wishlist(userId))
                        .build()
        );
    }
}
