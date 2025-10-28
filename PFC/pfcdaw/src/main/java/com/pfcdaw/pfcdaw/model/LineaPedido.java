package com.pfcdaw.pfcdaw.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "lineas_pedido")
public class LineaPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // moitas lineas poden tar nun pedido
    private PedidoEntity pedido;
    @ManyToOne // moitas lineas poden ter un mismo producto
    private ProductoEntity producto;
    
    @NotNull
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
    @NotNull
    @PositiveOrZero(message="El total no puede ser negativo")
    private Double pTotal;
}