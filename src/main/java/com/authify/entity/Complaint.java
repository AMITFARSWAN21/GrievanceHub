package com.authify.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;
    private String title;
    private String description;
    private String address;
    private String locality;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imageData;


    private double longitude;
    private double latitude;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus complaintStatus = ComplaintStatus.FILED;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel = PriorityLevel.MEDIUM;


}
