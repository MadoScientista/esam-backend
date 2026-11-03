package com.esam.esam_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "identidad_externa", uniqueConstraints = {
        @UniqueConstraint(name = "UK_identidad_externa_proveedor_subject",
                columnNames = { "proveedor", "subject" })
})
@Getter
@Setter
@NoArgsConstructor
public class IdentidadExterna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIdentidadExterna;

    @Column(nullable = false, length = 32)
    private String proveedor;

    @Column(nullable = false, length = 255)
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}
