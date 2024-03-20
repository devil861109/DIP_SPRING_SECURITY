package edu.unam.springsecurity.auth.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "sec_role")
public class UserInfoRole {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "usr_id", columnDefinition = "bigint", nullable = false, updatable = false)
    private Long usrId;
    @Column(name = "usr_role_name", columnDefinition = "varchar(30)", length = 30, nullable = false)
    private String usrRoleName;
    @Column(name = "usr_id_status", columnDefinition = "integer", nullable = false)
    private Integer usrIdStatus;
    @Column(name = "usr_created_by", columnDefinition = "bigint", updatable = false, nullable = false)
    private Long usrCreatedBy;
    @CreationTimestamp
    @Column(name = "usr_created_date", columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP", updatable = false, nullable = false,
            insertable = false)
    private LocalDateTime usrCreatedDate;
    @Column(name = "usr_modified_by", columnDefinition = "bigint", nullable = false)
    private Long usrModifiedBy;
    @UpdateTimestamp
    @Column(name = "usr_modified_date", columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP", nullable = false, insertable = false)
    private LocalDateTime usrModifiedDate;
}
