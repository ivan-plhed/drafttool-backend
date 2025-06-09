package com.ivaplahed.drafttool.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TEAMS")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonIgnore
    @Column(name = "ID")
    private UUID id;

    @Column(name = "NAME", unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "CREATED_BY")
    private User createdBy;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_TOP")
    private Player playerTop;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_JUNGLE")
    private Player playerJungle;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MID")
    private Player playerMid;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_BOT")
    private Player playerBot;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_SUPPORT")
    private Player playerSupport;

}
