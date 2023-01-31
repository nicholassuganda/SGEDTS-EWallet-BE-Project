package com.springboot.ewallet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Transaction{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer trx_id;

    LocalDate transactiondate;
    String origin_Username;
    String destination_Username;
    Long amount;
    Long balanceBefore;
    Long balanceAfter;
    String type;
    String status;

    @ManyToOne
    User user;

}