package com.olegtoropoff.petcareappointment.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "veterinarian_id")
public class Veterinarian extends User{
    private long id;
    private String specialization;

    @OneToOne(mappedBy = "veterinarian", cascade =  CascadeType.ALL, orphanRemoval = true)
    private VetBiography vetBiography ;
}
