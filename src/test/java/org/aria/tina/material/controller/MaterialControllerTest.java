package org.aria.tina.material.controller;

import org.aria.tina.common.exception.BusinessException;
import org.aria.tina.common.exception.ErrorCodes;
import org.aria.tina.common.response.PageResponse;
import org.aria.tina.material.dto.MaterialDTO;
import org.aria.tina.material.service.MaterialService;
import org.aria.tina.material.validator.MaterialValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MaterialController.class)
@AutoConfigureMockMvc(addFilters = false)
class MaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MaterialService service;

    @MockitoBean
    private MaterialValidator validator;

    private static final Long VALID_ID = 1L;
    private static final String MATERIAL_NAME = "wheat";
    private static final String NEW_NAME = "rice";
    private static final int CURRENT_STOCK = 100;
    private static final int MINIMUM_STOCK = 20;

    private MaterialDTO materialDTO;
    private MaterialDTO createDTO;

    @BeforeEach
    void setUp() {
        materialDTO = new MaterialDTO(VALID_ID, MATERIAL_NAME, CURRENT_STOCK, MINIMUM_STOCK);
        createDTO = new MaterialDTO(null, NEW_NAME, 0, 0);
    }

    @Test
    void create_ShouldReturnCreatedMaterial_WhenValid() throws Exception {
        doNothing().when(validator).validateForInsert(any(MaterialDTO.class));
        when(service.create(any(MaterialDTO.class))).thenReturn(materialDTO);

        mockMvc.perform(post("/api/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.resultData.id").value(VALID_ID))
                .andExpect(jsonPath("$.resultData.name").value(MATERIAL_NAME));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenValidationFails_En() throws Exception {
        doThrow(new BusinessException(ErrorCodes.MATERIAL_ALREADY_EXISTS))
                .when(validator).validateForInsert(any(MaterialDTO.class));

        mockMvc.perform(post("/api/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en")
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("Material already exists"));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenValidationFails_Fa() throws Exception {
        doThrow(new BusinessException(ErrorCodes.MATERIAL_ALREADY_EXISTS))
                .when(validator).validateForInsert(any(MaterialDTO.class));

        mockMvc.perform(post("/api/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "fa")
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("مواد اولیه قبلاً ثبت شده است"));
    }

    @Test
    void update_ShouldReturnUpdatedMaterial_WhenValid() throws Exception {
        doNothing().when(validator).validateForUpdate(eq(VALID_ID), any(MaterialDTO.class));
        when(service.update(eq(VALID_ID), any(MaterialDTO.class))).thenReturn(materialDTO);

        mockMvc.perform(put("/api/materials/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.resultData.id").value(VALID_ID));
    }

    @Test
    void update_ShouldReturnNotFound_WhenMaterialDoesNotExist_En() throws Exception {
        doThrow(new BusinessException(ErrorCodes.MATERIAL_NOT_FOUND))
                .when(validator).validateForUpdate(eq(VALID_ID), any(MaterialDTO.class));

        mockMvc.perform(put("/api/materials/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO))
                        .header("Accept-Language", "en"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Material not found"));
    }

    @Test
    void update_ShouldReturnNotFound_WhenMaterialDoesNotExist_Fa() throws Exception {
        doThrow(new BusinessException(ErrorCodes.MATERIAL_NOT_FOUND))
                .when(validator).validateForUpdate(eq(VALID_ID), any(MaterialDTO.class));

        mockMvc.perform(put("/api/materials/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO))
                        .header("Accept-Language", "fa"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("مواد اولیه ای یافت نشد"));
    }

    @Test
    void delete_ShouldReturnSuccess_WhenValidId() throws Exception {
        doNothing().when(service).delete(VALID_ID);

        mockMvc.perform(delete("/api/materials/{id}", VALID_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void findById_ShouldReturnMaterial_WhenExists() throws Exception {
        when(service.findById(VALID_ID)).thenReturn(materialDTO);

        mockMvc.perform(get("/api/materials/{id}", VALID_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.resultData.id").value(VALID_ID))
                .andExpect(jsonPath("$.resultData.name").value(MATERIAL_NAME));
    }

    @Test
    void findById_ShouldReturnNotFound_WhenMaterialDoesNotExist_En() throws Exception {
        when(service.findById(VALID_ID)).thenThrow(new BusinessException(ErrorCodes.MATERIAL_NOT_FOUND));

        mockMvc.perform(get("/api/materials/{id}", VALID_ID)
                        .header("Accept-Language", "en"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Material not found"));
    }

    @Test
    void findById_ShouldReturnNotFound_WhenMaterialDoesNotExist_Fa() throws Exception {
        when(service.findById(VALID_ID)).thenThrow(new BusinessException(ErrorCodes.MATERIAL_NOT_FOUND));

        mockMvc.perform(get("/api/materials/{id}", VALID_ID)
                        .header("Accept-Language", "fa"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("مواد اولیه ای یافت نشد"));
    }

    @Test
    void findAll_ShouldReturnPageResponse_WithData() throws Exception {
        PageResponse<MaterialDTO> pageResponse = new PageResponse<>(
                List.of(materialDTO),
                0,
                10,
                1,
                1,
                true,
                true
        );
        when(service.findAll(any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get("/api/materials")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.resultData.items").isArray())
                .andExpect(jsonPath("$.resultData.items.length()").value(1))
                .andExpect(jsonPath("$.resultData.items[0].id").value(VALID_ID));
    }

    @Test
    void findAll_ShouldReturnEmptyPageResponse_WhenNoData() throws Exception {
        PageResponse<MaterialDTO> emptyPageResponse = new PageResponse<>(
                List.of(),
                0,
                10,
                0,
                0,
                true,
                true
        );
        when(service.findAll(any(Pageable.class))).thenReturn(emptyPageResponse);

        mockMvc.perform(get("/api/materials")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.resultData.items").isArray())
                .andExpect(jsonPath("$.resultData.items.length()").value(0))
                .andExpect(jsonPath("$.resultData.totalElements").value(0));
    }

    @Test
    void searchByName_ShouldReturnPageResponse_WithMatchingResults() throws Exception {
        String searchName = "wheat";
        PageResponse<MaterialDTO> pageResponse = new PageResponse<>(
                List.of(materialDTO),
                0,
                10,
                1,
                1,
                true,
                true
        );
        when(service.searchByName(eq(searchName), any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get("/api/materials/search")
                        .param("name", searchName)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.resultData.items.length()").value(1))
                .andExpect(jsonPath("$.resultData.items[0].name").value(MATERIAL_NAME));
    }

    @Test
    void searchByName_ShouldReturnEmptyPageResponse_WhenNoMatchingResults() throws Exception {
        String searchName = "nonexistent";
        PageResponse<MaterialDTO> emptyPageResponse = new PageResponse<>(
                List.of(),
                0,
                10,
                0,
                0,
                true,
                true
        );
        when(service.searchByName(eq(searchName), any(Pageable.class))).thenReturn(emptyPageResponse);

        mockMvc.perform(get("/api/materials/search")
                        .param("name", searchName)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.resultData.items").isArray())
                .andExpect(jsonPath("$.resultData.items.length()").value(0));
    }
}