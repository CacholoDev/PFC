package com.pfcdaw.pfcdaw.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalExceptionHandlerDTO {

    @NotBlank
    private String timeStamp;

    @Min(100)
    @Max(599)
    private int status;

    @NotBlank
    private String error;

    @NotBlank
    private String message;

    @NotBlank
    private String path;
    
    

}
