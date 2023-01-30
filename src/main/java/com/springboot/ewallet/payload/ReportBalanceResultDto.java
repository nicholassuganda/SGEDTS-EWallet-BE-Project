package com.springboot.ewallet.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ReportBalanceResultDto {

    List<GetReportDto> reportBalanceChangeInPercentage;
}
