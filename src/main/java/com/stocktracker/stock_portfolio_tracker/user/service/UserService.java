package com.stocktracker.stock_portfolio_tracker.user.service;

import com.stocktracker.stock_portfolio_tracker.common.enums.UserStatus;
import com.stocktracker.stock_portfolio_tracker.exception.InvalidDataException;
import com.stocktracker.stock_portfolio_tracker.exception.ResourceNotFoundException;
import com.stocktracker.stock_portfolio_tracker.security.CurrentUserProvider;
import com.stocktracker.stock_portfolio_tracker.user.dto.ChangePasswordRequest;
import com.stocktracker.stock_portfolio_tracker.user.dto.UpdateProfileRequest;
import com.stocktracker.stock_portfolio_tracker.user.dto.UserResponse;
import com.stocktracker.stock_portfolio_tracker.user.entity.User;
import com.stocktracker.stock_portfolio_tracker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        User user = getCurrentUserEntity();
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateCurrentUserProfile(
            UpdateProfileRequest request
    ) {
        User user = getCurrentUserEntity();

        user.setFullName(request.fullName().trim());

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    @Transactional
    public void changeCurrentUserPassword(
            ChangePasswordRequest request
    ) {
        User user = getCurrentUserEntity();

        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPassword()
        )) {
            throw new InvalidDataException(
                    "Current password is incorrect."
            );
        }

        if (passwordEncoder.matches(
                request.newPassword(),
                user.getPassword()
        )) {
            throw new InvalidDataException(
                    "New password must be different from the current password."
            );
        }

        user.setPassword(
                passwordEncoder.encode(request.newPassword())
        );

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        return mapToResponse(getUserEntityById(id));
    }

    @Transactional
    public UserResponse activateUser(UUID id) {
        User user = getUserEntityById(id);

        user.setStatus(UserStatus.ACTIVE);

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    @Transactional
    public UserResponse deactivateUser(UUID id) {
        User user = getUserEntityById(id);

        user.setStatus(UserStatus.INACTIVE);

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    private User getCurrentUserEntity() {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        return userRepository.findById(currentUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Current user could not be found."
                        )
                );
    }

    private User getUserEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }
}
