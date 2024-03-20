package edu.unam.springsecurity.system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CatContactTypeDTO {
    @JsonProperty("id")
    public Integer cctContactTypeId;
    @JsonProperty("name")
    public String cctName;
    @JsonProperty("status")
    public String cctStatus;
}
