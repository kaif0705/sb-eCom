package com.ecommerce.project.Util;

import com.ecommerce.project.Model.User;
import com.ecommerce.project.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    @Autowired
    private UserRepository userRepository;

    public String loggedInEmail(){
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        User userFromDB= userRepository.findByUserName(auth.getName()).orElseThrow(
                () ->  new UsernameNotFoundException("User Not Found: " + auth.getName())
        );
        return userFromDB.getEmail();
    }

    public User loggedInUser(){
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        User user= userRepository.findByUserName(auth.getName()).orElseThrow(
                () -> new UsernameNotFoundException("User Not Found: " + auth.getName())
        );

        return user;
    }

    public Long loggedInUserID(){
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
       User user= userRepository.findByUserName(auth.getName()).orElseThrow(
               () -> new UsernameNotFoundException("User Not Found: " + auth.getName())
       );

       return user.getUserId();
    }

}
