package edu.unam.springsecurity.auth.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoRoleDTO {
    private Long usrId;
    private String usrRoleName;
    private Integer usrIdStatus;
    private Long usrCreatedBy;
    private LocalDateTime usrCreatedDate;
    private Long usrModifiedBy;
    private LocalDateTime usrModifiedDate;
}
