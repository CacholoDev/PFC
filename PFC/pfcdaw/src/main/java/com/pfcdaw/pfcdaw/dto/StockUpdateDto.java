package com.pfcdaw.pfcdaw.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockUpdateDto {
    @NotNull
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private int cantidad;
}
