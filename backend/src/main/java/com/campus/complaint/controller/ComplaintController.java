package com.campus.complaint.controller;

import com.campus.complaint.dto.ComplaintRequest;
import com.campus.complaint.model.Complaint;
import com.campus.complaint.model.ComplaintStatus;
import com.campus.complaint.model.Priority;
import com.campus.complaint.model.Visibility;
import com.campus.complaint.service.ComplaintService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" })
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY')")
    public ResponseEntity<?> createComplaint(
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("location") String location,
            @RequestParam("description") String description,
            @RequestParam("priority") String priority,
            @RequestParam(value = "visibility", defaultValue = "PUBLIC") String visibility,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
            HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            
            ComplaintRequest request = new ComplaintRequest();
            request.setTitle(title);
            request.setCategory(category);
            request.setLocation(location);
            request.setDescription(description);
            request.setPriority(Priority.valueOf(priority));
            request.setVisibility(Visibility.valueOf(visibility));
            
            Complaint complaint = complaintService.createComplaint(request, userId, attachment);
            return ResponseEntity.ok(complaint);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Complaint>> getRecentComplaints(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = null;
        if (request.getAttribute("userId") != null) {
            // User is authenticated, get their role from security context
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getAuthorities() != null) {
                role = auth.getAuthorities().stream()
                        .map(a -> a.getAuthority().replace("ROLE_", ""))
                        .findFirst().orElse(null);
            }
        }
        List<Complaint> complaints = complaintService.getRecentComplaints(userId, role);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY')")
    public ResponseEntity<?> getMyComplaints(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            List<Complaint> complaints = complaintService.getMyComplaints(userId);
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/technician")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<?> getTechnicianComplaints(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            List<Complaint> complaints = complaintService.getTechnicianComplaints(userId);
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getComplaintById(@PathVariable Long id) {
        try {
            Complaint complaint = complaintService.getComplaintById(id);
            return ResponseEntity.ok(complaint);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignTechnician(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        try {
            Long technicianId = request.get("technicianId");
            Complaint complaint = complaintService.assignTechnician(id, technicianId);
            return ResponseEntity.ok(complaint);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            ComplaintStatus status = ComplaintStatus.valueOf(request.get("status"));
            Complaint complaint = complaintService.updateStatus(id, status);
            return ResponseEntity.ok(complaint);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteComplaint(@PathVariable Long id, HttpServletRequest request) {
        try {
            // Determine caller role from security context
            String role = null;
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getAuthorities() != null) {
                role = auth.getAuthorities().stream()
                        .map(a -> a.getAuthority().replace("ROLE_", ""))
                        .findFirst().orElse(null);
            }

            if ("ADMIN".equals(role)) {
                // Admins can delete any complaint
                complaintService.deleteComplaint(id);
            } else {
                // Students/Faculty can delete only their own unassigned complaints
                Long userId = (Long) request.getAttribute("userId");
                if (userId == null) {
                    throw new RuntimeException("User information not found in request");
                }
                complaintService.deleteComplaintByOwner(id, userId);
            }

            return ResponseEntity.ok().body(Map.of("message", "Complaint deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
