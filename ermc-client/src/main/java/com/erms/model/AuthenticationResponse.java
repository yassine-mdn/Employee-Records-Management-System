package com.erms.model;

import lombok.*;

import java.util.Objects;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

  private String id;
  private String role;
  private String accessToken;
  private String refreshToken;

}
