package com.springboot.ewallet.payload;

import lombok.Data;

@Data
public class GetBalanceDto {

    private String balance;

    private String transactionLimit;
}
