package com.philippe.blogger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Entity
@Table(name = "tag")
public class Tag {

    @Id
    @Getter
    @Setter
    @Column(name = "name")
    private String name;

}
