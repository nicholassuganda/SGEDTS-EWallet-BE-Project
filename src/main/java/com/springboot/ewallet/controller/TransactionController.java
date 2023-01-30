package com.springboot.ewallet.controller;

import com.springboot.ewallet.entity.User;
import com.springboot.ewallet.payload.*;
import com.springboot.ewallet.repository.TransactionRepository;
import com.springboot.ewallet.repository.UserRepository;
import com.springboot.ewallet.service.TransactionService;
import com.springboot.ewallet.utils.AppConstants;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/topup")
    public ResponseEntity<String> authenticateUser(@RequestBody TopUpDto topUpDto) {
        User user = userRepository.findByUsername(topUpDto.getUsername());
        int passwordattempt = user.getPassword_attempt();

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found!");
        }

        if (!user.getPassword().equals(topUpDto.getPassword())) {
            user.setPassword_attempt(passwordattempt+1);
            userRepository.save(user);

            if (user.getPassword_attempt() >= 3){
                user.setBan_status(true);
                userRepository.save(user);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User is banned. Contact admin for help!");
            }

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Wrong Password!");
        }

        if(topUpDto.getAmount() > AppConstants.MAX_TOPUP){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Max top up reached. Maximum top up is 10.000.000!");
        }

        if(user.isBan_status()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"This user is banned. Contact customer service for help!");
        }

        if(user.getBalance()+topUpDto.getAmount() > AppConstants.MAX_BALANCE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Max balance reached. Maximum balance is 10.000.000!");
        }

        String topupresult = transactionService.topup(topUpDto);

        return ResponseEntity.ok().body(topupresult);

    }

    @PostMapping("/create")
    public ResponseEntity<CreateTransactionResultDto> authenticateUser(@RequestBody CreateTransactionDto createTransactionDto) {
        User user = userRepository.findByUsername(createTransactionDto.getUsername());
        User userrecipient = userRepository.findByUsername(createTransactionDto.getDestinationUsername());
        int passwordattempt = user.getPassword_attempt();

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Sender not found!");
        }
        if (userrecipient.getUsername().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Recipient not found!");
        }

        if (!user.getPassword().equals(createTransactionDto.getPassword())) {
            user.setPassword_attempt(passwordattempt + 1);
            userRepository.save(user);

            if (user.getPassword_attempt() >= 3) {
                user.setBan_status(true);
                userRepository.save(user);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is banned. Contact admin for help!");
            }

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong Password!");
        }

        if (createTransactionDto.getAmount() > user.getTransactionLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max transaction amount reached!");
        }
        if (createTransactionDto.getAmount() < AppConstants.MIN_TRANSACTION_AMOUNT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Min transaction amount is 10.000!");
        }
        if (createTransactionDto.getAmount() > user.getBalance()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance!");
        }

        if (user.isBan_status()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user is banned. Contact customer service for help!");
        }

        CreateTransactionResultDto transactionresult = transactionService.createtransaction(createTransactionDto);

        return ResponseEntity.ok().body(transactionresult);
    }

    @GetMapping("/getreport/{transactiondate}")
    public ResponseEntity<Object> getreport(@PathVariable LocalDate transactiondate){
        var bebas = transactionService.getreport(transactiondate);
//        Set<Transaction> transaction = transactionService.getreport(transactiondate);
//        Set <GetReportDto> listgetReportDto = new HashSet<>();
//        for(Transaction trx: transaction){
//            GetReportDto getReportDto = new GetReportDto();
//            getReportDto.setUsername(trx.getOrigin_Username());
//
//            getReportDto.setTransactiondate(trx.getTransactiondate());
//            listgetReportDto.add(getReportDto);
//        }
        return ResponseEntity.ok().body(bebas);
    }
}
