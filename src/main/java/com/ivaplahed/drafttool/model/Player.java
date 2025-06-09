package com.ivaplahed.drafttool.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PLAYERS")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonIgnore
    @Column(name = "ID")
    private UUID id;

    @Column(name = "NAME", unique = true)
    private String name;

    @Column(name = "REAL_NAME")
    private String realName;

    @Enumerated(EnumType.STRING)
    @Column(name = "POSITION")
    private Position position;

    @Column(name = "COUNTRY")
    private String country;

    @ManyToOne
    @JoinColumn(name = "CREATED_BY")
    @JsonIgnore
    private User createdBy;

    @Column(name = "IMAGE")
    private String image;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "PLAYERS_CHAMPIONS", joinColumns = @JoinColumn(name = "ID_PLAYER"), inverseJoinColumns = @JoinColumn(name = "ID_CHAMPION"))
    private Set<Champion> champions;

}
