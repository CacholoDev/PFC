package com.pfcdaw.pfcdaw.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "clientes")
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Column(unique = true) // para que non se repitan emails na BD
    private String email;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener 9 dígitos")
    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    @JsonIgnore  // Non devolve password en respostas JSON
    private String password;

    @Enumerated(EnumType.STRING) // Garda Enum como texto na BDD
    @Builder.Default // "USER" por defecto
    private LoginRoleEnum role = LoginRoleEnum.USER;

    // Relación con pedidos: cascade ALL (garda/actualiza/elimina automáticamente) + orphanRemoval (borra pedidos vacios)
    @OneToMany(mappedBy = "cliente", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Evita loops infinitos, consulta pedidos por separado
    private List<PedidoEntity> pedidos;

}
