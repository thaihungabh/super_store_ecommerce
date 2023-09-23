package com.shopee.clone.entity.address_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "address_data")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
//    private Integer code;
//    private String codename;
//    @JsonProperty("division_type")
//    private String divisionType;
//    @JsonProperty("phone_code")
//    private Integer phoneCode;
    @JsonProperty("districts")
    @OneToMany(mappedBy = "addressData",fetch = FetchType.EAGER)
    private List<DistrictEntity> districtEntities;
}