package edu.unam.springsecurity.auth.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoDTO {
    private Long useId;
    private String useFirstName;
    private String useLastName;
    private String useEmail;
    private String usePasswd;
    private Integer useIdStatus;
    private Long useCreatedBy;
    private LocalDateTime useCreatedDate;
    private Long useModifiedBy;
    private LocalDateTime useModifiedDate;
    private Set<UserInfoRoleDTO> useInfoRoles = new HashSet<>();
}
