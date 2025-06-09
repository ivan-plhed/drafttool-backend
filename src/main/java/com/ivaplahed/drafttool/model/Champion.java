package com.ivaplahed.drafttool.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CHAMPIONS")
public class Champion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private UUID id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "ROLE1")
    private String role1;

    @Column(name = "ROLE2")
    private String role2;

    @Transient
    @Nullable
    private String imageChampion;

    @Transient
    @Nullable
    private String imageSplash;

}
