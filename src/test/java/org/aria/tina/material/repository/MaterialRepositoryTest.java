package org.aria.tina.material.repository;

import org.aria.tina.material.model.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MaterialRepositoryTest {

    @Autowired
    private MaterialRepository repository;

    private Material material1;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        material1 = new Material(null, "wheat", 100, 20);
        Material material2 = new Material(null, "barley", 50, 10);
        Material material3 = new Material(null, "black wheat", 30, 5);

        repository.save(material1);
        repository.save(material2);
        repository.save(material3);
    }

    @Test
    void shouldSaveMaterial() {
        Material newMaterial = new Material(null, "rice", 200, 50);
        Material saved = repository.save(newMaterial);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("rice");
        assertThat(saved.getCurrentStock()).isEqualTo(200);
        assertThat(saved.getMinimumStock()).isEqualTo(50);
    }

    @Test
    void shouldFindById() {
        Material found = repository.findById(material1.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("wheat");
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        Material found = repository.findById(999L).orElse(null);
        assertThat(found).isNull();
    }

    @Test
    void shouldFindAll() {
        List<Material> all = repository.findAll();
        assertThat(all).hasSize(3);
        assertThat(all).extracting(Material::getName)
                .containsExactlyInAnyOrder("wheat", "barley", "black wheat");
    }

    @Test
    void shouldFindByName() {
        List<Material> found = repository.findByName("wheat");
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getName()).isEqualTo("wheat");
    }

    @Test
    void shouldReturnEmptyWhenNameNotFound() {
        List<Material> found = repository.findByName("noting");
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindByNameContainingWithPagination() {
        for (int i = 1; i <= 5; i++) {
            repository.save(new Material(null, "wheat_" + i, 10 + i, 2 + i));
        }

        Pageable pageable = PageRequest.of(0, 3);
        Page<Material> page = repository.findByNameContaining("wheat", pageable);

        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getTotalElements()).isEqualTo(7);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isZero();
        assertThat(page.getSize()).isEqualTo(3);

        List<String> names = page.getContent().stream()
                .map(Material::getName)
                .toList();
        assertThat(names).containsExactly("wheat", "black wheat", "wheat_1");
    }

    @Test
    void shouldReturnEmptyPageWhenNameNotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Material> page = repository.findByNameContaining("noting", pageable);
        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isZero();
    }

    @Test
    void shouldDeleteById() {
        repository.deleteById(material1.getId());
        List<Material> all = repository.findAll();
        assertThat(all).hasSize(2);
        assertThat(all).extracting(Material::getId)
                .doesNotContain(material1.getId());
    }
}