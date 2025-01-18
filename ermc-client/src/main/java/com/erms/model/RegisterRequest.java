package com.erms.model;

import lombok.*;

import java.util.Objects;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  private String userId;

  private String password;

}
