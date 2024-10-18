package org.sjob.crud.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sjob.crud.usermanagement.entity.User;
import org.sjob.crud.usermanagement.entity.UserPhone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DtoUser {
    private UUID id;
    private String name;
    @NotEmpty(message = "Password must be set")
    private String password;
    @NotEmpty
    @Email(message = "Email is not valid.")
    private String email;
    @JsonProperty("phones")
    private Set<DtoUserPhone> dtoUserPhones;
    private String created;
    private String modified;
    @JsonProperty("last_login")
    private String lastLogin;
    private String token;
    @JsonProperty("isactive")
    private boolean isActive;

    public static DtoUser fromEntity(User user) {

        Set<DtoUserPhone> dtoUserPhones = new HashSet<>();

        Optional.ofNullable(user.getUserPhones())
                .ifPresent(phones -> phones.forEach(phone -> dtoUserPhones.add(DtoUserPhone.fromEntity(phone))));

        return DtoUser.builder()
                .id(user.getId())
                .name(user.getName())
                .password(user.getPassword())
                .email(user.getEmail())
                .dtoUserPhones(dtoUserPhones)
                .created(user.getCreated())
                .modified(user.getModified())
                .lastLogin(user.getLastLogin())
                .isActive(user.isActive())
                .build();
    }

    public static User toEntity(DtoUser dtoUser) {
        List<UserPhone> userPhones = new ArrayList<>();
        Optional.ofNullable(dtoUser.getDtoUserPhones())
                .ifPresent(phones -> phones.forEach(phone -> userPhones.add(DtoUserPhone.toEntity(phone))));
        User userEntity = User.builder()
                .id(dtoUser.getId())
                .name(dtoUser.getName())
                .password(dtoUser.getPassword())
                .email(dtoUser.getEmail())
                .userPhones(userPhones)
                .build();
        userPhones.forEach(phone -> phone.setUser(userEntity));
        return userEntity;
    }
}
