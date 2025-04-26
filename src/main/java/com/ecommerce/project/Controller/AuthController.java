package com.ecommerce.project.Controller;

import com.ecommerce.project.Model.AppRole;
import com.ecommerce.project.Model.Role;
import com.ecommerce.project.Model.User;
import com.ecommerce.project.Model.UserInfoResponse;
import com.ecommerce.project.Repository.RoleRepository;
import com.ecommerce.project.Repository.UserRepository;
import com.ecommerce.project.Security.JWT.JwtUtils;
import com.ecommerce.project.Security.Request.LoginRequest;
import com.ecommerce.project.Security.Request.SignupRequest;
import com.ecommerce.project.Security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try{
            authentication= authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        }catch(AuthenticationException e){
            Map<String, Object> authException= new HashMap<>();
            authException.put("message", "Bad credentials");
            authException.put("status", false);
            return new ResponseEntity<>(authException, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails= (UserDetailsImpl)authentication.getPrincipal();

        //Generating cookie from server side and sending it to user
        ResponseCookie cookieToken= jwtUtils.generateJwtCookie(userDetails);

        List<String> roles= userDetails.getAuthorities().stream().
                map( role -> role.getAuthority()).
                toList();

        UserInfoResponse response= new UserInfoResponse(
                userDetails.getId(),
                roles,
                loginRequest.getUsername()
        );

        //Setting cookie when returning the response
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookieToken.toString()).body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signUp){

        //Check if username and email are already taken
       if(userRepository.existsByUserName(signUp.getUsername())){
            return new ResponseEntity<>("Username already exists", HttpStatus.CONFLICT);
       }

       if(userRepository.existsByEmail(signUp.getEmail())){
           return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
       }

       //Create user obj
        User user= new User(
                signUp.getUsername(),
                signUp.getEmail(),
                passwordEncoder.encode(signUp.getPassword())
                );

       Set<String> strRoles= signUp.getRoles();
       Set<Role> roles= new HashSet<>();

       if(strRoles == null){
           //Default role
           Role role= roleRepository.findByRoleName(AppRole.ROLE_USER).
                   orElseThrow(() -> new RuntimeException("Role not found"));
           roles.add(role);
       }else{
           strRoles.forEach( role -> {
               switch(role){
                   case "admin":
                       Role roleAdmin= roleRepository.findByRoleName(AppRole.ROLE_ADMIN).
                               orElseThrow(() -> new RuntimeException("Role not found"));
                       roles.add(roleAdmin);
                       break;

                       case "seller":
                           Role sellerRole= roleRepository.findByRoleName(AppRole.ROLE_SELLER).
                                   orElseThrow(() -> new RuntimeException("Role not found"));
                           roles.add(sellerRole);
                           break;

                           default:
                               Role userRole= roleRepository.findByRoleName(AppRole.ROLE_USER).
                                       orElseThrow(() -> new RuntimeException("Role not found"));
                               roles.add(userRole);
               }
           });
       }

       user.setRoles(roles);
       userRepository.save(user);

       return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);

    }


    @GetMapping("/username")
    public String getUsername(Authentication authentication){
        if(authentication != null){
            return authentication.getName();
        }else{
            return "NULL";
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){

        UserDetailsImpl userDetails= (UserDetailsImpl)authentication.getPrincipal();

        List<String> roles= userDetails.getAuthorities().stream().
                map(role -> role.getAuthority()).toList();

        UserInfoResponse response= new UserInfoResponse(
                userDetails.getId(),
                roles,
                userDetails.getUsername()
        );

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signout(){
        ResponseCookie cookie= jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ResponseEntity<>("Signed out successfully", HttpStatus.OK));
    }

}
