package org.sjob.crud.usermanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sjob.crud.usermanagement.entity.UserPhone;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DtoUserPhoneRequest {
    private UUID id;
    private String number;
    @JsonProperty(value = "citycode")
    private String cityCode;
    @JsonProperty(value = "countrycode")
    private String countryCode;

    public UserPhone toEntity() {
        return UserPhone.builder()
                .id(this.getId())
                .number(this.getNumber())
                .countryCode(this.getCountryCode())
                .cityCode(this.getCityCode())
                .build();
    }
}
