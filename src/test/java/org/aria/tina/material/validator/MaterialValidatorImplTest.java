package org.aria.tina.material.validator;

import org.aria.tina.common.exception.BusinessException;
import org.aria.tina.common.exception.ErrorCodes;
import org.aria.tina.material.dto.MaterialDTO;
import org.aria.tina.material.model.Material;
import org.aria.tina.material.repository.MaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaterialValidatorImplTest {

    @Mock
    private MaterialRepository repository;

    @InjectMocks
    private MaterialValidatorImpl validator;

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 999L;
    private static final String UNIQUE_NAME = "unique";
    private static final String DUPLICATE_NAME = "duplicate";

    private Material existingMaterial;

    @BeforeEach
    void setUp() {
        existingMaterial = new Material();
        existingMaterial.setId(VALID_ID);
        existingMaterial.setName(DUPLICATE_NAME);
    }

    @Test
    void validateForInsert_ShouldPass_WhenNameIsUnique() {
        MaterialDTO dto = new MaterialDTO(null, UNIQUE_NAME, null, null);
        when(repository.findByName(UNIQUE_NAME)).thenReturn(List.of());

        assertDoesNotThrow(() -> validator.validateForInsert(dto));
    }

    @Test
    void validateForInsert_ShouldThrowBusinessException_WhenNameIsDuplicate() {
        MaterialDTO dto = new MaterialDTO(null, DUPLICATE_NAME, null, null);
        when(repository.findByName(DUPLICATE_NAME)).thenReturn(List.of(existingMaterial));

        assertThatThrownBy(() -> validator.validateForInsert(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCodes.MATERIAL_ALREADY_EXISTS);
    }

    @Test
    void validateForUpdate_ShouldPass_WhenMaterialExistsAndNameUnchanged() {
        MaterialDTO dto = new MaterialDTO(null, DUPLICATE_NAME, null, null);
        when(repository.findById(VALID_ID)).thenReturn(Optional.of(existingMaterial));

        assertDoesNotThrow(() -> validator.validateForUpdate(VALID_ID, dto));
    }

    @Test
    void validateForUpdate_ShouldPass_WhenMaterialExistsAndNameChangedIsUnique() {
        MaterialDTO dto = new MaterialDTO(null, UNIQUE_NAME, null, null);
        when(repository.findById(VALID_ID)).thenReturn(Optional.of(existingMaterial));
        when(repository.findByName(UNIQUE_NAME)).thenReturn(List.of());

        assertDoesNotThrow(() -> validator.validateForUpdate(VALID_ID, dto));
    }

    @Test
    void validateForUpdate_ShouldThrowBusinessException_WhenMaterialNotFound() {
        MaterialDTO dto = new MaterialDTO(null, UNIQUE_NAME, null, null);
        when(repository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> validator.validateForUpdate(INVALID_ID, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCodes.MATERIAL_NOT_FOUND);
    }

    @Test
    void validateForUpdate_ShouldThrowBusinessException_WhenNameChangedAndDuplicate() {
        Material anotherMaterial = new Material();
        anotherMaterial.setId(2L);
        anotherMaterial.setName(DUPLICATE_NAME);

        when(repository.findById(VALID_ID)).thenReturn(Optional.of(existingMaterial));

        existingMaterial.setName("oldName");
        MaterialDTO dto = new MaterialDTO(null, DUPLICATE_NAME, null, null);
        when(repository.findById(VALID_ID)).thenReturn(Optional.of(existingMaterial));
        when(repository.findByName(DUPLICATE_NAME)).thenReturn(List.of(anotherMaterial));

        assertThatThrownBy(() -> validator.validateForUpdate(VALID_ID, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCodes.MATERIAL_ALREADY_EXISTS);
    }


    @Test
    void validateForUpdate_ShouldPass_WhenNameChangedAndNoDuplicateFound() {
        existingMaterial.setName("oldName");
        MaterialDTO dto = new MaterialDTO(null, UNIQUE_NAME, null, null);
        when(repository.findById(VALID_ID)).thenReturn(Optional.of(existingMaterial));
        when(repository.findByName(UNIQUE_NAME)).thenReturn(List.of());

        assertDoesNotThrow(() -> validator.validateForUpdate(VALID_ID, dto));
    }
}