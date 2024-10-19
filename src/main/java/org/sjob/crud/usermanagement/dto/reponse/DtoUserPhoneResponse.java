package org.sjob.crud.usermanagement.dto.reponse;

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
public class DtoUserPhoneResponse {
    private UUID id;
    private String number;
    @JsonProperty(value = "citycode")
    private String cityCode;
    @JsonProperty(value = "countrycode")
    private String countryCode;

    public static DtoUserPhoneResponse fromEntity(UserPhone userPhone) {
        return DtoUserPhoneResponse.builder()
                .id(userPhone.getId())
                .number(userPhone.getNumber())
                .countryCode(userPhone.getCountryCode())
                .cityCode(userPhone.getCityCode())
                .build();
    }
}
