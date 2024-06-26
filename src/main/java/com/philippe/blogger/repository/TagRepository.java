package com.philippe.blogger.repository;

import com.philippe.blogger.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByName(String name);
}
