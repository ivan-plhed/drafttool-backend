package com.ivaplahed.drafttool.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ROLES")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "NAME")
    private Authority name;

}
