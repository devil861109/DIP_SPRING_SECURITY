package edu.unam.springsecurity.system.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@Table(name = "cat_contact_type")
@AllArgsConstructor
@NoArgsConstructor
public class CatContactType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cct_contact_type_id", columnDefinition = "int", nullable = false)
    public Integer cctContactTypeId;
    @Column(name = "cct_name", columnDefinition = "varchar(50)", length = 50, nullable = false)
    public String cctName;
    @Column(name = "cct_status", columnDefinition = "varchar(50)", length = 50, nullable = false)
    public String cctStatus;
}
