package com.springboot.ewallet.controller;

import com.springboot.ewallet.entity.User;
import com.springboot.ewallet.payload.*;
import com.springboot.ewallet.repository.UserRepository;
import com.springboot.ewallet.service.UserServiceImpl;
import com.springboot.ewallet.utils.AppConstants;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl authService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto) {

        // add check for username exists in a DB
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        // add check for email exists in DB
        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        // create user object
        User user = new User();
        user.setName(signUpDto.getName());
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(signUpDto.getPassword());
        user.setBalance(0L);
        user.setTransactionLimit(AppConstants.MAX_TRANSACTION_AMOUNT);

        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);

    }

    @PostMapping("/changepassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto){
        authService.changepassword(changePasswordDto);
        return new ResponseEntity<>("Password Changed Successfully!", HttpStatus.OK);
    }

    @GetMapping("/{username}/getinfo")
    public ResponseEntity<GetInfoDto> getinfo(@PathVariable String username){
        User user = authService.getinfo(username);
        GetInfoDto getInfoDto = modelMapper.map(user,GetInfoDto.class);
        return ResponseEntity.ok().body(getInfoDto);
    }

    @GetMapping("/{username}/getbalance")
    public ResponseEntity<GetBalanceDto> getbalance(@PathVariable String username){
        User user = authService.getbalance(username);
        GetBalanceDto getBalanceDto = modelMapper.map(user,GetBalanceDto.class);
        return ResponseEntity.ok().body(getBalanceDto);
    }

    @PutMapping("/{username}/addktp")
    public ResponseEntity<?> addKtp(@PathVariable String username, @RequestBody AddKtpDto addKtpDto) {

        if (userRepository.existsByKtp(addKtpDto.getKtp())) {
            return new ResponseEntity<>("Ktp is already taken by other user!", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByUsername(username);
        user.setKtp(addKtpDto.getKtp());
        user.setTransactionLimit(AppConstants.MAX_TRANSACTION_AMOUNT_WITH_KTP);

        userRepository.save(user);
        return new ResponseEntity<>("User KTP registered successfully!", HttpStatus.OK);

    }

    @PutMapping("/{username}/unban")
    public void unban(@PathVariable String username) {

        var user = userRepository.findByUsername(username);
        if (userRepository.findByUsername(username).getUsername().equals(username)){
            user.setBan_status(false);
            user.setPassword_attempt(0);
            userRepository.save(user);
        }
    }
}