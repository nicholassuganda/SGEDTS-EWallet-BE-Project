package com.springboot.ewallet.payload;

import lombok.*;

import java.time.LocalDate;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetReportDto {
    private String username;
    private String changeInPercentage;
    private String balanceChangeDate;

}
