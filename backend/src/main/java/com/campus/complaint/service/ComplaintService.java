package com.campus.complaint.service;

import com.campus.complaint.dto.ComplaintRequest;
import com.campus.complaint.model.Complaint;
import com.campus.complaint.model.ComplaintStatus;
import com.campus.complaint.model.User;
import com.campus.complaint.model.Visibility;
import com.campus.complaint.repository.ComplaintRepository;
import com.campus.complaint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public Complaint createComplaint(ComplaintRequest request, Long studentId, org.springframework.web.multipart.MultipartFile attachment) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Store file if provided
        String attachmentUrl = null;
        if (attachment != null && !attachment.isEmpty()) {
            String fileName = fileStorageService.storeFile(attachment);
            attachmentUrl = "/api/files/" + fileName;
        }

        Complaint complaint = Complaint.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .location(request.getLocation())
                .description(request.getDescription())
                .priority(request.getPriority())
                .visibility(request.getVisibility() != null ? request.getVisibility() : Visibility.PUBLIC)
                .status(ComplaintStatus.OPEN)
                .attachmentUrl(attachmentUrl)
                .student(student)
                .build();

        Complaint saved = complaintRepository.save(complaint);

        // Notify admins about new complaint
        notificationService.notifyAdminsNewComplaint(saved.getId(), saved.getTitle(), student.getFullName());

        return saved;
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findByOrderByCreatedAtDesc()
                .stream()
                .map(this::enrichComplaint)
                .collect(Collectors.toList());
    }

    public List<Complaint> getRecentComplaints(Long userId, String role) {
        // Admins see all complaints
        if ("ADMIN".equals(role)) {
            return getAllComplaints();
        }
        // Non-admins see only PUBLIC complaints + their own
        List<Complaint> publicComplaints = complaintRepository.findByVisibilityOrderByCreatedAtDesc(Visibility.PUBLIC)
                .stream()
                .map(this::enrichComplaint)
                .collect(Collectors.toList());
        if (userId != null) {
            // Also include user's own private complaints
            List<Long> publicIds = publicComplaints.stream().map(Complaint::getId).collect(Collectors.toList());
            List<Complaint> myPrivate = complaintRepository.findByStudent_Id(userId)
                    .stream()
                    .filter(c -> c.getVisibility() == Visibility.PRIVATE)
                    .map(this::enrichComplaint)
                    .collect(Collectors.toList());
            publicComplaints.addAll(myPrivate);
            // Sort by createdAt desc
            publicComplaints.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        }
        return publicComplaints;
    }

    public List<Complaint> getMyComplaints(Long studentId) {
        return complaintRepository.findByStudent_Id(studentId)
                .stream()
                .map(this::enrichComplaint)
                .collect(Collectors.toList());
    }

    public List<Complaint> getTechnicianComplaints(Long technicianId) {
        return complaintRepository.findByTechnician_Id(technicianId)
                .stream()
                .map(this::enrichComplaint)
                .collect(Collectors.toList());
    }

    public Complaint getComplaintById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        return enrichComplaint(complaint);
    }

    @Transactional
    public Complaint assignTechnician(Long complaintId, Long technicianId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technician not found"));

        complaint.setTechnician(technician);
        complaint.setStatus(ComplaintStatus.IN_PROGRESS);

        Complaint saved = complaintRepository.save(complaint);

        // Notify technician about new assignment
        notificationService.notifyTechnicianAssignment(technicianId, complaintId, complaint.getTitle());

        // Notify student that technician was assigned
        if (complaint.getStudent() != null) {
            notificationService.notifyStudentTechnicianAssigned(
                    complaint.getStudent().getId(),
                    complaintId,
                    complaint.getTitle(),
                    technician.getFullName()
            );
        }

        return saved;
    }

    @Transactional
    public Complaint updateStatus(Long complaintId, ComplaintStatus status) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        ComplaintStatus oldStatus = complaint.getStatus();
        complaint.setStatus(status);
        Complaint saved = complaintRepository.save(complaint);

        // Notify student about status change
        if (complaint.getStudent() != null) {
            notificationService.notifyStudentStatusChange(
                    complaint.getStudent().getId(),
                    complaintId,
                    complaint.getTitle(),
                    status.name()
            );
        }

        // Notify technician about status change (if assigned)
        if (complaint.getTechnician() != null) {
            notificationService.notifyTechnicianStatusChange(
                    complaint.getTechnician().getId(),
                    complaintId,
                    complaint.getTitle(),
                    status.name()
            );
        }

        return saved;
    }

    @Transactional
    public void deleteComplaint(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        
        complaintRepository.delete(complaint);
    }

    /**
     * Delete complaint requested by its owner (student/faculty).
     * Only allowed when the complaint is not yet assigned to any technician.
     */
    @Transactional
    public void deleteComplaintByOwner(Long complaintId, Long userId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (complaint.getTechnician() != null) {
            throw new RuntimeException("Cannot delete complaint once it is assigned to a technician. Please request deletion from admin via comments.");
        }

        if (complaint.getStudent() == null || !complaint.getStudent().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own complaints.");
        }

        complaintRepository.delete(complaint);
    }
    // Helper method to populate transient fields
    private Complaint enrichComplaint(Complaint complaint) {
        if (complaint.getStudent() != null) {
            complaint.setStudentId(complaint.getStudent().getId());
            complaint.setStudentName(complaint.getStudent().getFullName());
        }
        if (complaint.getTechnician() != null) {
            complaint.setTechnicianId(complaint.getTechnician().getId());
            complaint.setTechnicianName(complaint.getTechnician().getFullName());
        }
        return complaint;
    }
}
