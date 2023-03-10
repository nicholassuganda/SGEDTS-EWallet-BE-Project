package com.springboot.ewallet.service;

import com.springboot.ewallet.entity.User;
import com.springboot.ewallet.exception.APIException;
import com.springboot.ewallet.payload.AddKtpDto;
import com.springboot.ewallet.payload.ChangePasswordDto;
import com.springboot.ewallet.payload.RegistrationDto;
import com.springboot.ewallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {
    private final UserRepository userRepository;

    public String register(RegistrationDto registerDto) {

        User user = new User();
        user.setName(registerDto.getName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(registerDto.getPassword());
        user.setPassword_attempt(0);
        userRepository.save(user);

        return "User registered successfully!";
    }

    public User changepassword(ChangePasswordDto changePasswordDto) {
        User user = userRepository.findByUsername(changePasswordDto.getUsername());
        String oldPassword = user.getPassword();
        if (!oldPassword.equals(changePasswordDto.getOldPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password Salah");
        }
        else {
            user.setPassword(changePasswordDto.getNewPassword());
            return userRepository.save(user);
        }
    }

    public User getinfo(String username){
        if (userRepository.findByUsername(username)==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User not found");
        }else {
            return userRepository.findByUsername(username);
        }
    }

    public User getbalance(String username){
        if (userRepository.findByUsername(username)==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User not found");
        }else {
            return userRepository.findByUsername(username);
        }
    }

    public User addktp(String username, AddKtpDto addKtpDto) {
        if (userRepository.findByUsername(username)==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User not found");
        } else {
            return userRepository.findByUsername(username);
        }
    }

    public User unban(String username) {
        if (userRepository.findByUsername(username)==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User not found");
        } else {
            return userRepository.findByUsername(username);
        }
    }

}
