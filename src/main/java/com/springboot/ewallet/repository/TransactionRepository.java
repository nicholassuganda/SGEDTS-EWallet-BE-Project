package com.springboot.ewallet.repository;

import com.springboot.ewallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;



@Repository
public interface TransactionRepository extends JpaRepository<Transaction, LocalDate> {

}
