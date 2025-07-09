package com.authify.io;

import com.authify.entity.ComplaintStatus;
import com.authify.entity.PriorityLevel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComplaintResponse {
    private Long id;
    private String name;
    private String title;
    private String description;
    private String address;
    private String locality;
    private Double latitude;
    private Double longitude;
    private String image; // base64 string
    private ComplaintStatus complaintStatus;
    private PriorityLevel priorityLevel;
    private String status;
}
