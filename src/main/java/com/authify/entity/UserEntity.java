package com.authify.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name="tbl_users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userId;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String verifyOtp;

    private Boolean isAccountVerified;

    private Long verifyOtpExpireAt;

    private String resetOtp;

    private Long resetOtpExpireAt;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(updatable = false)
    private Timestamp updatedAt;
}
