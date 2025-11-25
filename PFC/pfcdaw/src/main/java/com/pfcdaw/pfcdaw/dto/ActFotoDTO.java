package com.pfcdaw.pfcdaw.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder  
public class ActFotoDTO {

    @NotBlank(message = "La URL de la imagen no puede estar vacía")
    private String imagenUrl;

}
