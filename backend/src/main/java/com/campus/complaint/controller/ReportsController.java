package com.campus.complaint.controller;

import com.campus.complaint.model.Complaint;
import com.campus.complaint.model.ComplaintStatus;
import com.campus.complaint.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        List<Complaint> allComplaints = complaintRepository.findAll();
        
        long totalComplaints = allComplaints.size();
        long openComplaints = allComplaints.stream()
                .filter(c -> c.getStatus() == ComplaintStatus.OPEN)
                .count();
        
        // Resolved this month
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long resolvedThisMonth = allComplaints.stream()
                .filter(c -> c.getStatus() == ComplaintStatus.RESOLVED)
                .filter(c -> c.getUpdatedAt().isAfter(startOfMonth))
                .count();
        
        // Average resolution time
        double avgResolutionDays = allComplaints.stream()
                .filter(c -> c.getStatus() == ComplaintStatus.RESOLVED || c.getStatus() == ComplaintStatus.CLOSED)
                .mapToLong(c -> ChronoUnit.DAYS.between(c.getCreatedAt(), c.getUpdatedAt()))
                .average()
                .orElse(0.0);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalComplaints", totalComplaints);
        stats.put("openComplaints", openComplaints);
        stats.put("resolvedThisMonth", resolvedThisMonth);
        stats.put("avgResolutionDays", Math.round(avgResolutionDays * 10.0) / 10.0);
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/category-distribution")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getCategoryDistribution() {
        List<Complaint> allComplaints = complaintRepository.findAll();
        
        Map<String, Long> distribution = allComplaints.stream()
                .filter(c -> c.getCategory() != null && !c.getCategory().isEmpty())
                .collect(Collectors.groupingBy(
                        Complaint::getCategory,
                        Collectors.counting()
                ));
        
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/status-trend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getStatusTrend() {
        List<Complaint> allComplaints = complaintRepository.findAll();
        
        // Group by month for the last 6 months
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        
        Map<String, Map<ComplaintStatus, Long>> monthlyData = allComplaints.stream()
                .filter(c -> c.getCreatedAt().isAfter(sixMonthsAgo))
                .collect(Collectors.groupingBy(
                        c -> c.getCreatedAt().getMonth().toString(),
                        Collectors.groupingBy(
                                Complaint::getStatus,
                                Collectors.counting()
                        )
                ));
        
        List<Map<String, Object>> trend = new ArrayList<>();
        for (Map.Entry<String, Map<ComplaintStatus, Long>> entry : monthlyData.entrySet()) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", entry.getKey());
            monthData.put("open", entry.getValue().getOrDefault(ComplaintStatus.OPEN, 0L));
            monthData.put("resolved", entry.getValue().getOrDefault(ComplaintStatus.RESOLVED, 0L));
            monthData.put("closed", entry.getValue().getOrDefault(ComplaintStatus.CLOSED, 0L));
            trend.add(monthData);
        }
        
        return ResponseEntity.ok(trend);
    }
}
