package org.sjob.crud.usermanagement.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private UUID id;
    @Column(name = "name")
    private String name;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "email", unique = true, nullable = false)
    @Email
    private String email;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserPhone> userPhones;

    // Audit
    @Column(name = "created", nullable = false)
    private String created;
    @Column(name = "modified")
    private String modified;
    @Column(name = "last_login")
    private String lastLogin;
    @Column(name = "token")
    private String token;
    @Column(name = "isactive", nullable = false)
    private boolean isActive;
}