package com.pfcdaw.pfcdaw.dto;

import com.pfcdaw.pfcdaw.model.EstadoPedidoEnum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CambiarEstadoDto {

    @NotNull(message = "El estado es obligatorio")
    private EstadoPedidoEnum estado;

}
