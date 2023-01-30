package com.springboot.ewallet.entity;

import com.springboot.ewallet.utils.AppConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Name should not be null")
    private String name;
    private String username;
    private String email;

    @NotNull(message = "password should not be null")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{10,}$")
    private String password;
    private boolean ban_status;
    private Integer password_attempt;
    private Long balance;
    private String ktp;
    @OneToMany(mappedBy = "user")
    List<Transaction>transactions;
    @Column(name = "transaction_limit")
    private Long transactionLimit = AppConstants.MAX_TRANSACTION_AMOUNT;

}
