package edu.unam.springsecurity.auth.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "sec_user")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "use_id", columnDefinition = "bigint", nullable = false, updatable = false)
    private Long useId;
    @Column(name = "use_first_name", columnDefinition = "varchar(20)", length = 20, nullable = false)
    private String useFirstName;
    @Column(name = "use_last_name", columnDefinition = "varchar(20)", length = 20, nullable = false)
    private String useLastName;
    @Column(name = "use_email", columnDefinition = "varchar(45)", length = 45, nullable = false, unique = true)
    private String useEmail;
    @Column(name = "use_passwd", columnDefinition = "varchar(64)", length = 64, nullable = false)
    private String usePasswd;
    @Column(name = "use_id_status", columnDefinition = "integer", nullable = false)
    private Integer useIdStatus;
    @Column(name = "use_created_by", columnDefinition = "bigint", updatable = false, nullable = false)
    private Long useCreatedBy;
    @CreationTimestamp
    @Column(name = "use_created_date", columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP", updatable = false, nullable = false, insertable = false)
    private LocalDateTime useCreatedDate;
    @Column(name = "use_modified_by", columnDefinition = "bigint", nullable = false)
    private Long useModifiedBy;
    @UpdateTimestamp
    @Column(name = "use_modified_date", columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP", nullable = false, insertable = false)
    private LocalDateTime useModifiedDate;
    //ROLES
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(schema = "user_adm",
            name = "sec_user_role_relation",
            joinColumns = @JoinColumn(name = "urr_id_user"),
            inverseJoinColumns = @JoinColumn(name = "urr_id_user_role")
    )
    @JsonManagedReference
    private Set<UserInfoRole> useInfoRoles = new HashSet<>();

    public String getFullName() {
        return this.useFirstName + ' ' + this.useLastName;
    }
}
