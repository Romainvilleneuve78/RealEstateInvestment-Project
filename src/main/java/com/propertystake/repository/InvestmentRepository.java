package com.propertystake.repository;

import com.propertystake.model.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    // 🔹 Récupérer tous les investissements d'un utilisateur
    List<Investment> findByUserId(Long userId);

    // 🔹 Récupérer tous les investissements pour une propriété spécifique
    List<Investment> findByPropertyId(Long propertyId);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Investment i WHERE i.property.id = :propertyId")
    BigDecimal findTotalInvestmentByPropertyId(@Param("propertyId") Long propertyId);

    // ✅ Vérifier si une propriété est entièrement financée
    @Query("SELECT CASE WHEN (COALESCE(SUM(i.amount), 0) >= p.price) THEN TRUE ELSE FALSE END " +
            "FROM Investment i JOIN i.property p WHERE p.id = :propertyId")
    boolean isPropertyFullyFunded(@Param("propertyId") Long propertyId);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Investment i WHERE i.property.id = :propertyId")
    BigDecimal sumInvestmentsByProperty(@Param("propertyId") Long propertyId);

    // ✅ Calculer la somme totale investie par un utilisateur
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Investment i WHERE i.user.id = :userId")
    BigDecimal sumInvestmentsByUser(@Param("userId") Long userId);
}
