package com.springboot.ewallet.service;

import com.springboot.ewallet.entity.Transaction;
import com.springboot.ewallet.entity.User;
import com.springboot.ewallet.payload.*;
import com.springboot.ewallet.repository.TransactionRepository;
import com.springboot.ewallet.repository.UserRepository;
import com.springboot.ewallet.utils.AppConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor

public class TransactionService {
    private final TransactionRepository transactionRepository;
    private  final UserRepository userRepository;
    public String topup(@RequestBody TopUpDto topUpDto){
        User user = userRepository.findByUsername(topUpDto.getUsername());
        var balanceBefore = user.getBalance();
        var balanceAfter = balanceBefore+topUpDto.getAmount();
        user.setBalance(balanceAfter);
        user.setPassword_attempt(0);

        Transaction transaction = new Transaction();
        transaction.setDestination_Username(topUpDto.getUsername());
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setAmount(topUpDto.getAmount());
        transaction.setType("TOPUP");
        transaction.setStatus("SETTLED");
        transaction.setUser(user);
        transaction.setTransactiondate(LocalDate.now());

        transactionRepository.save(transaction);

        return "Top up successful!";
    }

    public CreateTransactionResultDto createtransaction(@RequestBody CreateTransactionDto createTransactionDto){
        User userorigin = userRepository.findByUsername(createTransactionDto.getUsername());
        User userdestination = userRepository.findByUsername(createTransactionDto.getDestinationUsername());
        userorigin.setPassword_attempt(0);

        var transferTax = (createTransactionDto.getAmount() * AppConstants.TRANSACTION_TAX);
        var originBalanceBefore = userorigin.getBalance();
        var originBalanceAfterTransfer = originBalanceBefore- createTransactionDto.getAmount();
        var originBalanceAfterTax = originBalanceAfterTransfer-transferTax;

        var destinationBalanceBefore = userdestination.getBalance();
        var destinationBalanceAfterTransfer = destinationBalanceBefore+createTransactionDto.getAmount();

        userorigin.setBalance(originBalanceAfterTransfer);

        Transaction transactionOrigin = new Transaction();
        transactionOrigin.setOrigin_Username(createTransactionDto.getUsername());
        transactionOrigin.setDestination_Username(createTransactionDto.getDestinationUsername());
        transactionOrigin.setAmount(createTransactionDto.getAmount());
        transactionOrigin.setBalanceBefore(originBalanceBefore);
        transactionOrigin.setBalanceAfter(originBalanceAfterTransfer);
        transactionOrigin.setStatus("SETTLED");
        transactionOrigin.setType("TRANSFER-OUT");
        transactionOrigin.setTransactiondate(LocalDate.now());
        transactionOrigin.setUser(userorigin);

        userorigin.setBalance((long) originBalanceAfterTax);

        Transaction transactionTax = new Transaction();
        transactionTax.setOrigin_Username(createTransactionDto.getUsername());
        transactionTax.setAmount((long) transferTax);
        transactionTax.setBalanceBefore(originBalanceAfterTransfer);
        transactionTax.setBalanceAfter((long) originBalanceAfterTax);
        transactionTax.setStatus("SETTLED");
        transactionTax.setType("TRANSFER-TAX");
        transactionTax.setTransactiondate(LocalDate.now());
        transactionTax.setUser(userorigin);

        Transaction transactionDestination = new Transaction();
        transactionDestination.setOrigin_Username(createTransactionDto.getUsername());
        transactionDestination.setDestination_Username(createTransactionDto.getDestinationUsername());
        transactionDestination.setAmount(createTransactionDto.getAmount());
        transactionDestination.setStatus("SETTLED");
        transactionDestination.setType("TRANSFER-IN");
        transactionDestination.setTransactiondate(LocalDate.now());
        transactionDestination.setBalanceBefore(destinationBalanceBefore);
        transactionDestination.setBalanceAfter((destinationBalanceAfterTransfer));
        userdestination.setBalance(destinationBalanceAfterTransfer);
        transactionDestination.setUser(userdestination);

        transactionRepository.saveAll(List.of(transactionOrigin,transactionTax,transactionDestination));

        return CreateTransactionResultDto.builder()
                .trx_id(transactionOrigin.getTrx_id())
                .origin_Username(transactionOrigin.getOrigin_Username())
                .destination_Username(transactionDestination.getDestination_Username())
                .amount(transactionDestination.getAmount())
                .status(transactionOrigin.getStatus())
                .build();
    }

    public ReportBalanceResultDto getreport(LocalDate transactiondate){
        List<GetReportDto> getReportDto = new ArrayList<>();

        String balanceChangeDate = transactiondate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        final NumberFormat percentage = NumberFormat.getPercentInstance();
        percentage.setMaximumFractionDigits(2);


        for (User user : userRepository.findAll()) {
            var transactions = user.getTransactions().stream()
                    .filter(transaction -> transaction.getTransactiondate().equals(transactiondate))
                    .toList();

            String changeInPercentage = "-";

            if (transactions.size() > 0){
                long balance_before=transactions.get(0).getBalanceBefore();

                if(balance_before>0){
                    long balance_after = transactions.get(transactions.size() - 1).getBalanceAfter();
                    var balanceDifference = balance_before - balance_after;
                    var fraction = 1.0 * balanceDifference / balance_before;

                    changeInPercentage = percentage.format(fraction);
                }
            }

            getReportDto.add(
                    new GetReportDto(
                            user.getUsername(),
                            changeInPercentage,
                            balanceChangeDate
                    )
            );
        }


        return new ReportBalanceResultDto(getReportDto);
//        if (transactionRepository.findByTransactiondate(transactiondate)==null){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Date not found");
//        }else {
//            return transactionRepository.findByTransactiondate(transactiondate);
//        }

    }
}
