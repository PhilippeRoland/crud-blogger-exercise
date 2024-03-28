package com.philippe.blogger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.Set;

@ToString
@Entity
@Table(name = "post", indexes = {
        @Index(columnList = "author"),
        @Index(columnList = "creationDate")})
@EntityListeners(AuditingEntityListener.class) //audits database changes, used to enable @CreatedDate on creationDate
public class Post {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    @ToString.Exclude
    private Long id;

    @Getter
    @Setter
    @Column(name = "title")
    private String title;

    @Getter
    @Setter
    @Column(name = "description")
    private String description;

    @Getter
    @Setter
    @Column(name = "author")
    private String author;

    //UTC timezone, see application.properties; no setter, as this will be automatically filled in when creating resource
    @Getter
    @CreatedDate
    @Column(name = "creationDate")
    private Date creationDate;

    /*
    Unidirectional, as tags don't need to know which post uses them. Still should have cascade behaviours set up
    Should deletion be added in the future
    */
    @Getter
    @Setter
    @ManyToMany(
            // cascade = {CascadeType.ALL} TODO See PostController Tag handling for details.
            // Maybe some automated behaviour exists for what I'm trying to do here but as is this causes merging issues
            )
    @JoinTable(name="Post_Tags",
            joinColumns={@JoinColumn(name="post_id")},
            inverseJoinColumns={@JoinColumn(name="tag_name")})
    private Set<Tag> tags;

    public boolean hasTag(String tagName) {
        return tags.stream().
                anyMatch(tag -> tagName.equals(tag.getName()));
    }

}
