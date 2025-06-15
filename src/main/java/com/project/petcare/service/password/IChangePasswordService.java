package com.project.petcare.service.password;

import com.project.petcare.request.ChangePasswordRequest;

public interface IChangePasswordService {
    void changePassword(Long userId, ChangePasswordRequest request);
}

