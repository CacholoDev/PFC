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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
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
@Table(name = "pedidos")
public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp  // Hibernate garda na BD cando facemos un .save
    @Builder.Default    // o crear o objeto garda un valor por defecto que sera localdate.now
    private LocalDateTime fechaPedido = LocalDateTime.now();

    @OneToMany(mappedBy = "pedido", cascade=jakarta.persistence.CascadeType.ALL,orphanRemoval = true) // cascade para gardar lineas automaticamente cando se .save un pedido # orphanRemoval para eliminar liñas borradas na BD e que non queden colgadas para cando fagamos un editar pedido ou cancelarpedido
    private List<LineaPedido> lineasPedido;

    @PositiveOrZero(message = "El total debe ser un valor positivo o cero")
    private double total;

    @Enumerated(EnumType.STRING)  // Garda o Enum como texto na BDD
    @Builder.Default // "Pendiente" do enum por default
    private EstadoPedidoEnum estado = EstadoPedidoEnum.PENDIENTE;

    @ManyToOne() 
    @JoinColumn(name = "cliente_id", nullable = false) // NOT NULL na BD
    @NotNull(message = "El cliente es obligatorio")
    private ClienteEntity cliente;
    
    // Recalcula o total automaticamente antes de gardar ou actualizar
    // son lifecycle callbacks, é dicir, métodos que se executan automaticamente en certos momentos, por exemplo:
    // esto ven porque quero manter o cascade all para cando se borra un producto se borren as liñas q conteñen ese producto pero,
    // o facer eso o total sigue sin auto actualizarse,con prepersist e preupdate de springboot conseguimos que se autocalcule o total
    @PrePersist
    @PreUpdate
    public void recalcularTotal() {
        if (lineasPedido != null && !lineasPedido.isEmpty()) {
            this.total = lineasPedido.stream()
                .mapToDouble(LineaPedido::getPTotal)
                .sum();
        } else {
            this.total = 0.0;
        }
    }
    
}

