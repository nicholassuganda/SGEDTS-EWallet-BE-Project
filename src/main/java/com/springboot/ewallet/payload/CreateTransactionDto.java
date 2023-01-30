package com.springboot.ewallet.payload;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionDto {
    private String username;
    private String password;
    private String destinationUsername;
    public Long amount;

}
