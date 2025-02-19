package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.projections.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = """
               SELECT obj FROM Product obj JOIN FETCH obj.categories c
               WHERE (:categoryId = '0' OR  c.id IN :categoryList)
               AND (LOWER(obj.name) LIKE LOWER(CONCAT('%', :name, '%')))""",
            countQuery = """
               SELECT COUNT(obj) FROM Product obj JOIN obj.categories c
               WHERE (:categoryId = '0' OR c.id IN :categoryList)
               AND (LOWER(obj.name) LIKE LOWER(CONCAT('%', :name, '%')))""")
    Page<ProductProjection> searchAll(String name, List<Long> categoryList, String categoryId,
                                      Pageable pageable);


    @Query("SELECT obj FROM Product obj JOIN FETCH " +
           "obj.categories WHERE obj.id IN :productsId")
    List<Product> searchProductsWithCategories(List<Long> productsId, Sort sort);

}
