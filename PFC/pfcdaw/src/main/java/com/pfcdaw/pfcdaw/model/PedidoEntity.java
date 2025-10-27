package com.pfcdaw.pfcdaw.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "pedidos")
public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp  // Hibernate garda na BD cando facemos un .save
    @Builder.Default    // o crear o objeto garda un valor por defecto que sera localdate.now
    private LocalDateTime fechaPedido = LocalDateTime.now();

    @NotNull
    @Size(min = 1, message = "El pedido debe tener al menos un producto")
    @ManyToMany //un pedido podede ter 1+ productos e un producto pode tar en varios pedidos
    @JoinTable(name = "pedido_productos",
               joinColumns = @JoinColumn(name = "pedido_id"),
               inverseJoinColumns = @JoinColumn(name = "producto_id"))
    private List<ProductoEntity> productos;

    @PositiveOrZero(message = "El total debe ser un valor positivo o cero")
    private double total;

    @NotNull
    @Enumerated(EnumType.STRING)  // Garda o Enum como texto na BDD
    @Builder.Default // Pendiente por default
    private EstadoPedidoEnum estado = EstadoPedidoEnum.PENDIENTE;

    @ManyToOne() 
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;
    
}

