package com.ecommerce.project.Security.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message="Username cannot be empty!")
    @Size(min = 3, max = 10, message="Min length 3 and Max 10!")
    private String username;

    @NotBlank(message="Password cannot be empty!")
    @Size(min = 4, max = 10, message="Min length 4 and Max 10!")
    private String password;

    @NotBlank(message="Email cannot be empty!")
    @Size(max = 50, message="Min length 5 and Max 50!")
    @Email
    private String email;

    private Set<String> roles;

    public Set<String> getRole(){
        return roles;
    }

    public void setRole(Set<String> roles){
        this.roles = roles;
    }

}
