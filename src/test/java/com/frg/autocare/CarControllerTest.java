package com.frg.autocare;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frg.autocare.controllers.CarController;
import com.frg.autocare.dto.CarDTO;
import com.frg.autocare.services.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "johndoe", roles = {"USER"})
@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<CarDTO> sampleCars;

    @BeforeEach
    void setUp() {
        sampleCars = Arrays.asList(
                new CarDTO(1L, "Camry", "Toyota", "John Doe", "Service Center A", new ArrayList<>()),
                new CarDTO(2L, "Civic", "Honda", "Jane Smith", "Service Center B", new ArrayList<>()),
                new CarDTO(3L, "Corolla", "Toyota", "Bob Johnson", "Service Center A", new ArrayList<>())
        );
    }


    @Test
    void getAllCars_WithoutFilters_ShouldReturnAllCars() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<CarDTO> expectedPage = new PageImpl<>(sampleCars, pageable, sampleCars.size());

        when(carService.getAllCars(null, null, null, null, pageable))
                .thenReturn(expectedPage);

        // When & Then
        mockMvc.perform(get("/api/v1/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.content[0].make").value("Toyota"))
                .andExpect(jsonPath("$.content[1].make").value("Honda"));

        verify(carService).getAllCars(null, null, null, null, pageable);
    }

    @Test
    void getAllCars_WithMultipleFilters_ShouldReturnFilteredCars() throws Exception {
        // Given
        List<CarDTO> filteredCars = Arrays.asList(sampleCars.get(0), sampleCars.get(2));
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<CarDTO> expectedPage = new PageImpl<>(filteredCars, pageable, filteredCars.size());

        when(carService.getAllCars("Toyota", null, null, "Service Center A", pageable))
                .thenReturn(expectedPage);

        // When & Then
        mockMvc.perform(get("/api/v1/cars")
                        .param("make", "Toyota")
                        .param("maintainer", "Service Center A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].make").value("Toyota"))
                .andExpect(jsonPath("$.content[0].maintainerName").value("Service Center A"))
                .andExpect(jsonPath("$.content[1].make").value("Toyota"))
                .andExpect(jsonPath("$.content[1].maintainerName").value("Service Center A"));

        verify(carService).getAllCars("Toyota", null, null, "Service Center A", pageable);
    }

    @Test
    void getAllCars_WithAllFilters_ShouldReturnFilteredCars() throws Exception {
        // Given
        List<CarDTO> filteredCars = Arrays.asList(sampleCars.get(0));
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<CarDTO> expectedPage = new PageImpl<>(filteredCars, pageable, filteredCars.size());

        when(carService.getAllCars("Toyota", "Camry", "John Doe", "Service Center A", pageable))
                .thenReturn(expectedPage);

        // When & Then
        mockMvc.perform(get("/api/v1/cars")
                        .param("make", "Toyota")
                        .param("model", "Camry")
                        .param("owner", "John Doe")
                        .param("maintainer", "Service Center A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].make").value("Toyota"))
                .andExpect(jsonPath("$.content[0].model").value("Camry"))
                .andExpect(jsonPath("$.content[0].clientName").value("John Doe"))
                .andExpect(jsonPath("$.content[0].maintainerName").value("Service Center A"));

        verify(carService).getAllCars("Toyota", "Camry", "John Doe", "Service Center A", pageable);
    }

    @Test
    void getAllCars_WithCustomPagination_ShouldReturnCorrectPageAndSize() throws Exception {
        // Given
        List<CarDTO> pagedCars = Arrays.asList(sampleCars.get(1)); // Second page with size 1
        Pageable pageable = PageRequest.of(1, 1, Sort.by(Sort.Direction.ASC, "id"));
        Page<CarDTO> expectedPage = new PageImpl<>(pagedCars, pageable, sampleCars.size());

        when(carService.getAllCars(null, null, null, null, pageable))
                .thenReturn(expectedPage);

        // When & Then
        mockMvc.perform(get("/api/v1/cars")
                        .param("page", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.content[0].make").value("Honda"));

        verify(carService).getAllCars(null, null, null, null, pageable);
    }

    @Test
    void getAllCars_WithSortingByMakeDesc_ShouldReturnSortedCars() throws Exception {
        // Given
        List<CarDTO> sortedCars = Arrays.asList(sampleCars.get(0), sampleCars.get(2), sampleCars.get(1));
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "make"));
        Page<CarDTO> expectedPage = new PageImpl<>(sortedCars, pageable, sortedCars.size());

        when(carService.getAllCars(null, null, null, null, pageable))
                .thenReturn(expectedPage);

        // When & Then
        mockMvc.perform(get("/api/v1/cars")
                        .param("sortBy", "make")
                        .param("sortDir", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.sort.sorted").value(true))
                .andExpect(jsonPath("$.content[0].make").value("Toyota"))
                .andExpect(jsonPath("$.content[2].make").value("Honda"));

        verify(carService).getAllCars(null, null, null, null, pageable);
    }

    @Test
    void getAllCars_WithSortingByOwnerAsc_ShouldReturnSortedCars() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "ownerName"));
        Page<CarDTO> expectedPage = new PageImpl<>(sampleCars, pageable, sampleCars.size());

        when(carService.getAllCars(null, null, null, null, pageable))
                .thenReturn(expectedPage);

        // When & Then
        mockMvc.perform(get("/api/v1/cars")
                        .param("sortBy", "ownerName")
                        .param("sortDir", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.sort.sorted").value(true));

        verify(carService).getAllCars(null, null, null, null, pageable);
    }

    @Test
    void getAllCars_WithCombinedFiltersAndPaginationAndSorting_ShouldWorkCorrectly() throws Exception {
        // Given
        List<CarDTO> filteredCars = Arrays.asList(sampleCars.get(0));
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "model"));
        Page<CarDTO> expectedPage = new PageImpl<>(filteredCars, pageable, filteredCars.size());

        when(carService.getAllCars("Toyota", null, "John", null, pageable))
                .thenReturn(expectedPage);

        // When & Then
        mockMvc.perform(get("/api/v1/cars")
                        .param("make", "Toyota")
                        .param("owner", "John")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "model")
                        .param("sortDir", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.sort.sorted").value(true))
                .andExpect(jsonPath("$.content[0].make").value("Toyota"))
                .andExpect(jsonPath("$.content[0].clientName").value("John Doe"));

        verify(carService).getAllCars("Toyota", null, "John", null, pageable);
    }
    @Test
    void getAllCars_WithInvalidSortDirection_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/cars")
                        .param("sortBy", "make")
                        .param("sortDir", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid sort direction: INVALID. Use 'ASC' or 'DESC'."))
                .andExpect(jsonPath("$.path").value("/api/v1/cars"));
    }

}