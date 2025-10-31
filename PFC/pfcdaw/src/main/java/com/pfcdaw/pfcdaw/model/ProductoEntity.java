package com.pfcdaw.pfcdaw.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "productos")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;
    @NotNull(message = "El precio del producto es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio debe ser un valor positivo o cero")
    private BigDecimal precio;

    private String descripcion;

    @NotNull(message = "El stock del producto es obligatorio")
    @Min(value = 0, message = "El stock debe ser un valor positivo o cero")
    private int stock;

    @OneToMany(mappedBy = "producto", cascade = jakarta.persistence.CascadeType.ALL) 
    @JsonIgnore // evita loops infinitos na serialización
    private List<LineaPedido> lineasPedido;
}
