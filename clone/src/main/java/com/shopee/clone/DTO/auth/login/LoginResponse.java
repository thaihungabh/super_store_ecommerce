package com.shopee.clone.DTO.auth.login;

import com.shopee.clone.entity.ERole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Long id;
    private String userName;
    private String email;
    private List<SimpleGrantedAuthority> roles;
    private String accessToken;
    private String refreshToken;
}
