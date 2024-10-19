package org.sjob.crud.usermanagement.dto.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sjob.crud.usermanagement.entity.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DtoUserResponse {
    private UUID id;
    private String name;
    private String email;
    @JsonProperty("phones")
    private Set<DtoUserPhoneResponse> dtoUserPhones;
    private String created;
    private String modified;
    @JsonProperty("last_login")
    private String lastLogin;
    private String token;
    @JsonProperty("isactive")
    private boolean isActive;

    public static DtoUserResponse fromEntity(User user) {
        Set<DtoUserPhoneResponse> dtoUserPhones = new HashSet<>();
        Optional.ofNullable(user.getUserPhones())
                .ifPresent(phones -> phones.forEach(phone -> dtoUserPhones.add(DtoUserPhoneResponse.fromEntity(phone))));
        return DtoUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .dtoUserPhones(dtoUserPhones)
                .created(user.getCreated())
                .modified(user.getModified())
                .lastLogin(user.getLastLogin())
                .isActive(user.isActive())
                .build();
    }
}
