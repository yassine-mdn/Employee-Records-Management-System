package com.erms.model;

import com.erms.model.enums.Role;
import lombok.*;

import java.util.Objects;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

  private String id;
  private Role role;
  private String accessToken;
  private String refreshToken;

}
