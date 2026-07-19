package com.stocktracker.stock_portfolio_tracker.user.controller;

import com.stocktracker.stock_portfolio_tracker.common.response.ApiResponse;
import com.stocktracker.stock_portfolio_tracker.user.dto.ChangePasswordRequest;
import com.stocktracker.stock_portfolio_tracker.user.dto.UpdateProfileRequest;
import com.stocktracker.stock_portfolio_tracker.user.dto.UserResponse;
import com.stocktracker.stock_portfolio_tracker.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse response = userService.getCurrentUser();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Current user retrieved successfully.",
                        response
                )
        );
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>>
    updateCurrentUserProfile(
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserResponse response =
                userService.updateCurrentUserProfile(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Profile updated successfully.",
                        response
                )
        );
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changeCurrentUserPassword(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Password changed successfully.",
                        null
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> response =
                userService.getAllUsers();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Users retrieved successfully.",
                        response
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable UUID id
    ) {
        UserResponse response =
                userService.getUserById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User retrieved successfully.",
                        response
                )
        );
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(
            @PathVariable UUID id
    ) {
        UserResponse response =
                userService.activateUser(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User activated successfully.",
                        response
                )
        );
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> deactivateUser(
            @PathVariable UUID id
    ) {
        UserResponse response =
                userService.deactivateUser(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User deactivated successfully.",
                        response
                )
        );
    }

}
