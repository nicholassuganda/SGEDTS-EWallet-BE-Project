package com.springboot.ewallet.payload;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TopUpDto {

    private String username;
    private String password;
    private Long amount;

}
