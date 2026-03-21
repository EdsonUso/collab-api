package com.edsonuso.collabapi.content.repository;

import com.edsonuso.collabapi.content.entity.ContentCategories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentCategoryRepository extends JpaRepository<ContentCategories, Long> {
    List<ContentCategories> findAllByActiveTrue();
    List<ContentCategories> findAllByActiveOrderByDisplayOrder(boolean active);

}
