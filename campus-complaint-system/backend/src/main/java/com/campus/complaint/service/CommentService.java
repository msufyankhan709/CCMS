package com.campus.complaint.service;

import com.campus.complaint.model.Complaint;
import com.campus.complaint.model.ComplaintComment;
import com.campus.complaint.model.Role;
import com.campus.complaint.model.User;
import com.campus.complaint.repository.ComplaintCommentRepository;
import com.campus.complaint.repository.ComplaintRepository;
import com.campus.complaint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private ComplaintCommentRepository commentRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public ComplaintComment addComment(Long complaintId, Long userId, String content) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ComplaintComment comment = ComplaintComment.builder()
                .content(content)
                .complaint(complaint)
                .user(user)
                .build();

        ComplaintComment saved = commentRepository.save(comment);

        // Send notifications to relevant parties (excluding the commenter)
        sendCommentNotifications(complaint, user);

        return saved;
    }

    private void sendCommentNotifications(Complaint complaint, User commenter) {
        String complaintTitle = complaint.getTitle();
        Long complaintId = complaint.getId();
        String commenterName = commenter.getFullName();

        // Notify student (if commenter is not the student)
        if (complaint.getStudent() != null && !complaint.getStudent().getId().equals(commenter.getId())) {
            notificationService.notifyNewComment(
                    complaint.getStudent().getId(),
                    complaintId,
                    complaintTitle,
                    commenterName
            );
        }

        // Notify technician (if assigned and commenter is not the technician)
        if (complaint.getTechnician() != null && !complaint.getTechnician().getId().equals(commenter.getId())) {
            notificationService.notifyNewComment(
                    complaint.getTechnician().getId(),
                    complaintId,
                    complaintTitle,
                    commenterName
            );
        }

        // Notify admins (if commenter is not an admin)
        if (commenter.getRole() != Role.ADMIN) {
            List<User> admins = userRepository.findByRole(Role.ADMIN);
            for (User admin : admins) {
                notificationService.notifyNewComment(
                        admin.getId(),
                        complaintId,
                        complaintTitle,
                        commenterName
                );
            }
        }
    }

    public List<ComplaintComment> getCommentsByComplaint(Long complaintId) {
        return commentRepository.findByComplaint_IdOrderByTimestampAsc(complaintId)
                .stream()
                .map(this::enrichComment)
                .collect(Collectors.toList());
    }

    // Helper method to populate transient fields
    private ComplaintComment enrichComment(ComplaintComment comment) {
        if (comment.getUser() != null) {
            comment.setUserId(comment.getUser().getId());
            comment.setUserName(comment.getUser().getFullName());
            comment.setIsUserActive(comment.getUser().getActive());
        }
        if (comment.getComplaint() != null) {
            comment.setComplaintId(comment.getComplaint().getId());
        }
        return comment;
    }
}
