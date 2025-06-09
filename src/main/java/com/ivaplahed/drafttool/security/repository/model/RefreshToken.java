package com.ivaplahed.drafttool.security.repository.model;

import com.ivaplahed.drafttool.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "REFRESH_TOKENS")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "TOKEN")
    private UUID token;

    @OneToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;

    @Column(name ="EXPIRY_DATE", nullable = false)
    private Instant expiryDate;

    @Column(name = "CREATED_DATE", nullable = false)
    private Instant createdDate;
}
