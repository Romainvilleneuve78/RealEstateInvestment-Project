package com.propertystake.controller;

import com.propertystake.model.Property;
import com.propertystake.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    @Autowired
    private PropertyService propertyService;

    @GetMapping
    public List<Property> getAllProperties() {
        return propertyService.getAllProperties();
    }

    @GetMapping("/{id}")
    public Property getPropertyById(@PathVariable Long id) {
        return propertyService.getPropertyById(id);
    }

    @PostMapping
    public Property createProperty(@RequestBody Property property) {
        System.out.println("Received property: " + property);
        return propertyService.createProperty(property);
    }

    @GetMapping("/{propertyId}/remaining-funding")
    public BigDecimal getRemainingFunding(@PathVariable Long propertyId) {
        Property property = propertyService.getPropertyById(propertyId);
        return property.getRemainingAmount();
    }

    @DeleteMapping("/{id}")
    public void deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
    }


}
