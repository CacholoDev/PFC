package com.pfcdaw.pfcdaw.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@Table(name = "clientes")
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede tener más de 100 caracteres")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 255, message = "El email no puede tener más de 255 caracteres")
    @Column(unique = true) // para que non se repitan emails na BD
    private String email;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede tener más de 255 caracteres")
    private String direccion;

    @CreationTimestamp // Hibernate garda na BD cando facemos un .save
    @Builder.Default // o crear o objeto garda un valor por defecto que sera localdate.now
    private LocalDateTime fechaAlta = LocalDateTime.now();

    @NotBlank
    private String nombreEmpresa;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener 9 dígitos")
    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, max = 100, message = "La contraseña debe tener entre 4 y 100 caracteres")
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Non devolve password en get pero deixa post
    private String password;

    @Enumerated(EnumType.STRING) // Garda Enum como texto na BDD
    @Builder.Default // "USER" por defecto
    private LoginRoleEnum role = LoginRoleEnum.USER;

    // Relación con pedidos: cascade ALL (garda/actualiza/elimina automáticamente) +
    // orphanRemoval (borra pedidos vacios)
    @OneToMany(mappedBy = "cliente", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Evita loops infinitos, consulta pedidos por separado
    private List<PedidoEntity> pedidos;

}
