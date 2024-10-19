package org.sjob.crud.usermanagement.dto.request;

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
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DtoUserRequest {
    private String name;
    @NotEmpty(message = "Password must be set")
    private String password;
    @NotEmpty
    @Email(message = "Email is not valid.")
    private String email;
    @JsonProperty("phones")
    private Set<DtoUserPhoneRequest> dtoUserPhones;

    public User toEntity() {
        List<UserPhone> userPhones = new ArrayList<>();
        Optional.ofNullable(this.getDtoUserPhones())
                .ifPresent(phones -> phones.forEach(phone -> userPhones.add(phone.toEntity())));
        User userEntity = User.builder()
                .name(this.getName())
                .password(this.getPassword())
                .email(this.getEmail())
                .userPhones(userPhones)
                .build();
        userPhones.forEach(phone -> phone.setUser(userEntity));
        return userEntity;
    }
}
