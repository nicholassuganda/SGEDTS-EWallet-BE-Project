package com.springboot.ewallet.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

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