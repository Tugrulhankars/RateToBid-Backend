package org.racetobid.racetobid.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
public class Auditable {

    @CreatedDate
    @Column(updatable = false)
    private Date createdDate;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    @Column(updatable = false)
    private Date lastModifiedDate;

    @LastModifiedBy
    private Date lastModifiedBy;
}
