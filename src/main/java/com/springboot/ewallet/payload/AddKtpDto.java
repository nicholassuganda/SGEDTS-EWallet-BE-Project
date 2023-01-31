package com.springboot.ewallet.payload;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddKtpDto {

    @Pattern(regexp = "^(?=.*?[0-9]).{16,}$")
    private String ktp;

}
