package com.example.demo.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordForm {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
