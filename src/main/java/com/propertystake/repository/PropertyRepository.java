package com.propertystake.repository;

import com.propertystake.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    // 🔹 Récupère toutes les propriétés en fonction de leur statut
    List<Property> findByStatus(String status);

}
