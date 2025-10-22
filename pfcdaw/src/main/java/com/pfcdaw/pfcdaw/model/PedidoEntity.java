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
    @ManyToMany //un pedido podede ter varios productos e un producto pode tar en varios pedidos
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

    @ManyToOne // moitos pedidos poden pertencer a un cliente
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;
    
    /*
     ¿Qué hace mappedBy = "cliente"?

Le dice a JPA: "No crees otra columna, la relación YA está definida en PedidoEntity en el campo cliente"
Es como decirle: "Mira, la FK ya está en la tabla pedidos, no hagas nada en esta tabla"
Importante:

mappedBy siempre va en el lado @OneToMany
Debe apuntar exactamente al nombre del campo en la otra clase (en PedidoEntity es private ClienteEntity cliente;)
#####################################
🚨 IMPORTANTE - Problema de recursión infinita:
Con Lombok @Data, cuando serialices a JSON puede haber un loop infinito:

Solución RÁPIDA (sin cambiar mucho):
Añade @JsonIgnore en el lado que menos te importe:
Mi recomendación: Pon @JsonIgnore en ClienteEntity.pedidos, así cuando devuelvas un pedido, SÍ ves los datos del cliente, pero cuando devuelvas un cliente no ves todos sus pedidos (lo consultas aparte).
     */


}
