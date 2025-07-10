package com.authify.controller;

import com.authify.entity.Complaint;
import com.authify.entity.ComplaintStatus;
import com.authify.io.ComplaintResponse;
import com.authify.service.ComplaintService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/complaint")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Complaint> saveComplaint(
            @RequestParam @NotBlank String name,
            @RequestParam @NotBlank String title,
            @RequestParam @NotBlank String description,
            @RequestParam @NotBlank String address,
            @RequestParam @NotBlank String locality,
            @RequestParam @NotNull Double latitude,
            @RequestParam @NotNull Double longitude,
            @RequestParam(required = false) MultipartFile image
    ) {
        Complaint complaint = complaintService.saveComplaint(
                name, title, description, address, locality, latitude, longitude, image
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(complaint);
    }

    @GetMapping
    public ResponseEntity<List<ComplaintResponse>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaintResponses());
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<ComplaintResponse> getComplaintStatus(@PathVariable Long id)
    {
        return ResponseEntity.ok(complaintService.getComplaintStatus(id));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<String> updateComplaintStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        complaintService.updateComplaintStatus(id, ComplaintStatus.valueOf(status));
        return ResponseEntity.ok("Status updated successfully");
    }

}
