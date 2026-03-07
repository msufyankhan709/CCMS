package com.campus.complaint.controller;

import com.campus.complaint.model.ComplaintStatus;
import com.campus.complaint.repository.ComplaintRepository;
import com.campus.complaint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" })
public class StatsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @GetMapping
    public ResponseEntity<Map<String, Long>> getStats() {
        long activeUsers = userRepository.countByActiveTrue();
        long resolvedComplaints = complaintRepository.countByStatus(ComplaintStatus.CLOSED);
        
        return ResponseEntity.ok(Map.of(
            "activeUsers", activeUsers,
            "resolvedComplaints", resolvedComplaints,
            "institutions", 7L
        ));
    }
}
