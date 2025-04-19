package com.ecommerce.project.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

import java.util.List;

@Data
@NoArgsConstructor
public class UserInfoResponse {

    private Long id;
    private List<String> roles;
    private String username;
    private String jwtToken;
    private ResponseCookie cookieToken;

    public UserInfoResponse(Long id, List<String> roles, String username, String jwtToken) {
        this.id = id;
        this.roles = roles;
        this.username = username;
        this.jwtToken = jwtToken;

    }

    public UserInfoResponse(Long id, List<String> roles, String username) {
        this.id = id;
        this.roles = roles;
        this.username = username;

    }

}
