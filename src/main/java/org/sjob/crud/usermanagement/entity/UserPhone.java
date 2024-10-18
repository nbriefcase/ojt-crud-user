package org.sjob.crud.usermanagement.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Builder
@Table(name = "user_phone")
@NoArgsConstructor
@AllArgsConstructor
public class UserPhone {
    @Id
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "number", nullable = false)
    private String number;
    @Column(name = "citycode")
    private String cityCode;
    @Column(name = "countrycode")
    private String countryCode;
    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false, insertable = false)
    private User user;
}
