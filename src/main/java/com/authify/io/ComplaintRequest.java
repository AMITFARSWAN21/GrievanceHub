package com.authify.io;

import com.authify.entity.PriorityLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "locality is required")
    private String locality;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Latitude is required")
    private Double latitude;


    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imageData;


    @Builder.Default
    private final PriorityLevel priorityLevel = PriorityLevel.MEDIUM;

}
