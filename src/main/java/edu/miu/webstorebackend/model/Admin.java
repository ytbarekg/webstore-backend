package edu.miu.webstorebackend.model;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class Admin {
    @Id
    private Long id;


}
