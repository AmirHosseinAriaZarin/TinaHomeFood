package org.aria.tina.material.service;

import org.aria.tina.common.exception.BusinessException;
import org.aria.tina.common.exception.ErrorCodes;
import org.aria.tina.common.response.PageResponse;
import org.aria.tina.material.dto.MaterialDTO;
import org.aria.tina.material.model.Material;
import org.aria.tina.material.repository.MaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialServiceImplTest {

    @Mock
    private MaterialRepository repository;

    @InjectMocks
    private MaterialServiceImpl service;

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 999L;
    private static final String MATERIAL_NAME = "wheat";
    private static final String NEW_NAME = "rice";
    private static final int CURRENT_STOCK = 100;
    private static final int MINIMUM_STOCK = 20;

    private Material material;

    @BeforeEach
    void setUp() {
        material = new Material();
        material.setId(VALID_ID);
        material.setName(MATERIAL_NAME);
        material.setCurrentStock(CURRENT_STOCK);
        material.setMinimumStock(MINIMUM_STOCK);
    }

    @Test
    void create_ShouldReturnSavedMaterialDTO() {
        MaterialDTO inputDto = new MaterialDTO(null, NEW_NAME, null, null);
        Material newMaterial = new Material();
        newMaterial.setName(NEW_NAME);
        newMaterial.setCurrentStock(0);
        newMaterial.setMinimumStock(0);

        Material savedMaterial = new Material();
        savedMaterial.setId(2L);
        savedMaterial.setName(NEW_NAME);
        savedMaterial.setCurrentStock(0);
        savedMaterial.setMinimumStock(0);

        when(repository.save(any(Material.class))).thenReturn(savedMaterial);

        MaterialDTO result = service.create(inputDto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.name()).isEqualTo(NEW_NAME);
        assertThat(result.currentStock()).isZero();
        assertThat(result.minimumStock()).isZero();

        verify(repository, times(1)).save(any(Material.class));
    }

    @Test
    void update_ShouldReturnUpdatedMaterialDTO_WhenMaterialExists() throws BusinessException {
        MaterialDTO updateDto = new MaterialDTO(VALID_ID, NEW_NAME, 150, 30);
        Material updatedMaterial = new Material();
        updatedMaterial.setId(VALID_ID);
        updatedMaterial.setName(NEW_NAME);
        updatedMaterial.setCurrentStock(150);
        updatedMaterial.setMinimumStock(30);

        when(repository.findById(VALID_ID)).thenReturn(Optional.of(material));
        when(repository.save(any(Material.class))).thenReturn(updatedMaterial);

        MaterialDTO result = service.update(VALID_ID, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(VALID_ID);
        assertThat(result.name()).isEqualTo(NEW_NAME);
        assertThat(result.currentStock()).isEqualTo(150);
        assertThat(result.minimumStock()).isEqualTo(30);

        verify(repository, times(1)).findById(VALID_ID);
        verify(repository, times(1)).save(any(Material.class));
    }

    @Test
    void update_ShouldThrowBusinessException_WhenMaterialNotFound() {
        MaterialDTO updateDto = new MaterialDTO(INVALID_ID, NEW_NAME, 150, 30);
        when(repository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(INVALID_ID, updateDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCodes.MATERIAL_NOT_FOUND);

        verify(repository, times(1)).findById(INVALID_ID);
        verify(repository, never()).save(any(Material.class));
    }

    @Test
    void delete_ShouldCallRepositoryDeleteById() {
        service.delete(VALID_ID);

        verify(repository, times(1)).deleteById(VALID_ID);
    }

    @Test
    void findById_ShouldReturnMaterialDTO_WhenMaterialExists() throws BusinessException {
        when(repository.findById(VALID_ID)).thenReturn(Optional.of(material));

        MaterialDTO result = service.findById(VALID_ID);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(VALID_ID);
        assertThat(result.name()).isEqualTo(MATERIAL_NAME);
        assertThat(result.currentStock()).isEqualTo(CURRENT_STOCK);
        assertThat(result.minimumStock()).isEqualTo(MINIMUM_STOCK);

        verify(repository, times(1)).findById(VALID_ID);
    }

    @Test
    void findById_ShouldThrowBusinessException_WhenMaterialNotFound() {
        when(repository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(INVALID_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCodes.MATERIAL_NOT_FOUND);

        verify(repository, times(1)).findById(INVALID_ID);
    }

    @Test
    void findAll_ShouldReturnPageResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Material> page = new PageImpl<>(List.of(material), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);

        PageResponse<MaterialDTO> result = service.findAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getPageNumber()).isZero();
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();

        MaterialDTO firstItem = result.getItems().getFirst();
        assertThat(firstItem.id()).isEqualTo(VALID_ID);
        assertThat(firstItem.name()).isEqualTo(MATERIAL_NAME);

        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void findAll_ShouldReturnEmptyPageResponse_WhenNoData() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Material> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(repository.findAll(pageable)).thenReturn(emptyPage);

        PageResponse<MaterialDTO> result = service.findAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getTotalPages()).isZero();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();

        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void searchByName_ShouldReturnPageResponse_WithMatchingResults() {
        String searchName = "wheat";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Material> page = new PageImpl<>(List.of(material), pageable, 1);

        when(repository.findByNameContaining(searchName, pageable)).thenReturn(page);

        PageResponse<MaterialDTO> result = service.searchByName(searchName, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);

        MaterialDTO firstItem = result.getItems().getFirst();
        assertThat(firstItem.name()).contains(searchName);

        verify(repository, times(1)).findByNameContaining(searchName, pageable);
    }

    @Test
    void searchByName_ShouldReturnEmptyPageResponse_WhenNoMatchingResults() {
        String searchName = "nonexistent";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Material> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(repository.findByNameContaining(searchName, pageable)).thenReturn(emptyPage);

        PageResponse<MaterialDTO> result = service.searchByName(searchName, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(repository, times(1)).findByNameContaining(searchName, pageable);
    }

    @Test
    void create_ShouldSetDefaultStockValuesToZero() {
        MaterialDTO inputDto = new MaterialDTO(null, NEW_NAME, null, null);
        Material savedMaterial = new Material();
        savedMaterial.setId(2L);
        savedMaterial.setName(NEW_NAME);
        savedMaterial.setCurrentStock(0);
        savedMaterial.setMinimumStock(0);

        when(repository.save(any(Material.class))).thenReturn(savedMaterial);

        MaterialDTO result = service.create(inputDto);

        assertThat(result.currentStock()).isZero();
        assertThat(result.minimumStock()).isZero();

        verify(repository, times(1)).save(any(Material.class));
    }

    @Test
    void update_ShouldPreserveStockValues_WhenNotChanged() throws BusinessException {
        MaterialDTO updateDto = new MaterialDTO(VALID_ID, NEW_NAME, null, null);
        Material updatedMaterial = new Material();
        updatedMaterial.setId(VALID_ID);
        updatedMaterial.setName(NEW_NAME);
        updatedMaterial.setCurrentStock(CURRENT_STOCK);
        updatedMaterial.setMinimumStock(MINIMUM_STOCK);

        when(repository.findById(VALID_ID)).thenReturn(Optional.of(material));
        when(repository.save(any(Material.class))).thenReturn(updatedMaterial);

        MaterialDTO result = service.update(VALID_ID, updateDto);

        assertThat(result.currentStock()).isEqualTo(CURRENT_STOCK);
        assertThat(result.minimumStock()).isEqualTo(MINIMUM_STOCK);

        verify(repository, times(1)).findById(VALID_ID);
        verify(repository, times(1)).save(any(Material.class));
    }
}