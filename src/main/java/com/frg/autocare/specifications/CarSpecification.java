package com.frg.autocare.specifications;

import com.frg.autocare.entities.Car;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class CarSpecification {
  public static Specification<Car> hasMake(String make) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(make)) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.like(
          criteriaBuilder.lower(root.get("make")), "%" + make.toLowerCase() + "%");
    };
  }

  public static Specification<Car> hasModel(String model) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(model)) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.like(
          criteriaBuilder.lower(root.get("model")), "%" + model.toLowerCase() + "%");
    };
  }

  public static Specification<Car> hasOwner(String owner) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(owner)) {
        return criteriaBuilder.conjunction();
      }
      var joinCustomer = root.join("customer");
      return criteriaBuilder.like(
              criteriaBuilder.lower(joinCustomer.get("name")), "%" + owner.toLowerCase() + "%");
    };
  }

  public static Specification<Car> hasMaintainer(String maintainer) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(maintainer)) {
        return criteriaBuilder.conjunction();
      }
      var joinMaintainer = root.join("maintainer");
      return criteriaBuilder.like(
              criteriaBuilder.lower(joinMaintainer.get("name")), "%" + maintainer.toLowerCase() + "%");
    };
  }


  public static Specification<Car> withFilters(
      String make, String model, String owner, String maintainer) {
    return Specification.where(hasMake(make))
        .and(hasModel(model))
        .and(hasOwner(owner))
        .and(hasMaintainer(maintainer));
  }
}
