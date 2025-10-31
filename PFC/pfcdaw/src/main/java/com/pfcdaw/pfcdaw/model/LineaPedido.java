package com.pfcdaw.pfcdaw.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @JoinColumn(name = "pedido_id", nullable = false) // NOT NULL na BD
    @NotNull(message = "El pedido es obligatorio")
    @JsonIgnore // Evita recursión infinita: Pedido -> Lineas -> Pedido -> Lineas...
    private PedidoEntity pedido;
    @ManyToOne // moitas lineas poden ter un mismo producto
    @JoinColumn(name = "producto_id", nullable = false) // NOT NULL na BD
    @NotNull(message = "El producto es obligatorio")
    private ProductoEntity producto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.0", message="El total no puede ser negativo")
    private BigDecimal pTotal;
}