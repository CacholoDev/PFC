package com.pfcdaw.pfcdaw.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoCreateDto {

    @NotNull
    private Long clienteId;

    @NotNull
    @Size(min = 1, message = "El pedido debe tener al menos un producto")
    private List<Long> productoIds;

}
