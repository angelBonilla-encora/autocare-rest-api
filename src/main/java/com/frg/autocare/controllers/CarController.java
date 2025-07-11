/**
 * AutoCare REST API - Car controller class.
 * Copyright (C) 2024  AutoCare REST API original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this application.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.frg.autocare.controllers;

import com.frg.autocare.dto.CarDTO;
import com.frg.autocare.exception.ErrorResponse;
import com.frg.autocare.services.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
@Tag(name = "Cars", description = "Car management APIs")
public class CarController {

  private final CarService carService;

  @GetMapping
  @Operation(
      summary = "Get all cars",
      description = "Retrieve a list of all cars with their details")
  @ApiResponse(responseCode = "200", description = "List of cars retrieved successfully")
  public ResponseEntity<?> getAllCars(
      @RequestParam(required = false) String make,
      @RequestParam(required = false) String model,
      @RequestParam(required = false) String owner,
      @RequestParam(required = false) String maintainer,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "ASC") String sortDir) {
    Sort.Direction direction;
    try {
      direction = Sort.Direction.fromString(sortDir.toUpperCase());
    } catch (IllegalArgumentException ex) {
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(new ErrorResponse(
                      400,
                      "Invalid sort direction: " + sortDir + ". Use 'ASC' or 'DESC'.",
                      "/api/v1/cars"));
    }
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

    return ResponseEntity.ok(carService.getAllCars(make, model, owner, maintainer, pageable));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get car by ID", description = "Retrieve a car by its ID")
  @ApiResponse(responseCode = "200", description = "Car retrieved successfully")
  @ApiResponse(responseCode = "404", description = "Car not found")
  public ResponseEntity<CarDTO> getCarById(@PathVariable Long id) {
    return ResponseEntity.ok(carService.getCarById(id));
  }
}
