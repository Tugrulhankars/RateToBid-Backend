package org.racetobid.racetobid.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "mails")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Mail extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String to;
    private String subject;
    private String body;
    private String templateName;
    private List<String> attachmentPaths;
}
