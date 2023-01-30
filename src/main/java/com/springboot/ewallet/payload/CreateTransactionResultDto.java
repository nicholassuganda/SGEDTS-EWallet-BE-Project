package com.springboot.ewallet.payload;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CreateTransactionResultDto {
    private Integer trx_id;
    private String origin_Username;
    private String destination_Username;
    public Long amount;
    public String status;

}
