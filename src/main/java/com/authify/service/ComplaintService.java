package com.authify.service;

import com.authify.entity.Complaint;
import com.authify.entity.ComplaintStatus;
import com.authify.entity.PriorityLevel;
import com.authify.entity.UserEntity;
import com.authify.io.ComplaintResponse;
import com.authify.repository.ComplaintRepository;
import com.authify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public Complaint saveComplaint(String name, String title, String description,
                                   String address, String locality,
                                   Double latitude, Double longitude,
                                   MultipartFile image) {

        byte[] imageData = null;
        try {
            if (image != null && !image.isEmpty()) {
                imageData = image.getBytes();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image file", e);
        }

        Complaint complaint = Complaint.builder()
                .name(name)
                .title(title)
                .description(description)
                .address(address)
                .locality(locality)
                .latitude(latitude)
                .longitude(longitude)
                .imageData(imageData)
                .complaintStatus(ComplaintStatus.FILED)
                .priorityLevel(PriorityLevel.MEDIUM)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        return complaintRepository.save(complaint);
    }

    public List<ComplaintResponse> getAllComplaintResponses() {
        return complaintRepository.findAll().stream().map(complaint -> ComplaintResponse.builder()
                .id(complaint.getId())
                .name(complaint.getName())
                .title(complaint.getTitle())
                .description(complaint.getDescription())
                .address(complaint.getAddress())
                .locality(complaint.getLocality())
                .latitude(complaint.getLatitude())
                .longitude(complaint.getLongitude())
                .image(complaint.getImageData() != null
                        ? Base64.getEncoder().encodeToString(complaint.getImageData())
                        : null)
                .complaintStatus(complaint.getComplaintStatus())
                .priorityLevel(complaint.getPriorityLevel())
                .status(complaint.getComplaintStatus().name()) // ✅ this ensures frontend gets correct value
                .build()
        ).collect(Collectors.toList());
    }


    public ComplaintResponse getComplaintStatus(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));

        return ComplaintResponse.builder()
                .id(complaint.getId())
                .name(complaint.getName())
                .title(complaint.getTitle())
                .description(complaint.getDescription())
                .address(complaint.getAddress())
                .locality(complaint.getLocality())
                .latitude(complaint.getLatitude())
                .longitude(complaint.getLongitude())
                .image(complaint.getImageData() != null
                        ? Base64.getEncoder().encodeToString(complaint.getImageData())
                        : null)
                .complaintStatus(complaint.getComplaintStatus())
                .priorityLevel(complaint.getPriorityLevel())
                .status(complaint.getComplaintStatus().name()) // ✅ add this line
                .build();
    }

    public void updateComplaintStatus(Long complaintId, ComplaintStatus newStatus) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + complaintId));

        complaint.setComplaintStatus(newStatus);
        complaintRepository.save(complaint);

        if (newStatus == ComplaintStatus.RESOLVED) {
            String name = complaint.getName();
            List<UserEntity> users = userRepository.findAllByName(name); // ✅ updated method

            if (!users.isEmpty()) {
                String email = users.get(0).getEmail(); // you can customize logic here
                if (email != null && !email.isEmpty()) {
                    emailService.complaintIssueResolved(email, complaint.getTitle());
                }
            }
        }
    }





}
