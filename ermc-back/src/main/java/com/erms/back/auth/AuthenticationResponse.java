package com.erms.back.auth;

import com.erms.back.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String id;
    private Role role;
    private String accessToken;
    private String refreshToken;
}
