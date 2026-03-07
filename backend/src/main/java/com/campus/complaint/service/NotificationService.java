package com.campus.complaint.service;

import com.campus.complaint.model.Notification;
import com.campus.complaint.model.NotificationType;
import com.campus.complaint.model.Role;
import com.campus.complaint.model.User;
import com.campus.complaint.repository.NotificationRepository;
import com.campus.complaint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false);
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    @Transactional
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false);
        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public Notification createNotification(Notification notification) {
        if (notification.getIsRead() == null) {
            notification.setIsRead(false);
        }
        return notificationRepository.save(notification);
    }

    // ==================== Auto Notification Helpers ====================

    /**
     * Notify all admins about a new complaint
     */
    @Transactional
    public void notifyAdminsNewComplaint(Long complaintId, String complaintTitle, String studentName) {
        List<User> admins = userRepository.findByRole(Role.ADMIN);
        for (User admin : admins) {
            Notification notification = Notification.builder()
                    .userId(admin.getId())
                    .title("New Complaint Submitted")
                    .message("New complaint \"" + complaintTitle + "\" submitted by " + studentName)
                    .type(NotificationType.COMPLAINT)
                    .relatedEntityId(complaintId)
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
        }
    }

    /**
     * Notify technician about new assignment
     */
    @Transactional
    public void notifyTechnicianAssignment(Long technicianId, Long complaintId, String complaintTitle) {
        Notification notification = Notification.builder()
                .userId(technicianId)
                .title("New Complaint Assigned")
                .message("You have been assigned to complaint: " + complaintTitle)
                .type(NotificationType.ASSIGNMENT)
                .relatedEntityId(complaintId)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    /**
     * Notify student that technician has been assigned
     */
    @Transactional
    public void notifyStudentTechnicianAssigned(Long studentId, Long complaintId, String complaintTitle, String technicianName) {
        Notification notification = Notification.builder()
                .userId(studentId)
                .title("Technician Assigned")
                .message("Technician " + technicianName + " has been assigned to your complaint: " + complaintTitle)
                .type(NotificationType.ASSIGNMENT)
                .relatedEntityId(complaintId)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    /**
     * Notify student about status change
     */
    @Transactional
    public void notifyStudentStatusChange(Long studentId, Long complaintId, String complaintTitle, String newStatus) {
        NotificationType type = "RESOLVED".equals(newStatus) ? NotificationType.RESOLVED : NotificationType.COMPLAINT;
        Notification notification = Notification.builder()
                .userId(studentId)
                .title("Complaint Status Updated")
                .message("Your complaint \"" + complaintTitle + "\" status changed to: " + newStatus)
                .type(type)
                .relatedEntityId(complaintId)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    /**
     * Notify technician about status change (if they made it, skip)
     */
    @Transactional
    public void notifyTechnicianStatusChange(Long technicianId, Long complaintId, String complaintTitle, String newStatus) {
        Notification notification = Notification.builder()
                .userId(technicianId)
                .title("Complaint Status Updated")
                .message("Complaint \"" + complaintTitle + "\" status changed to: " + newStatus)
                .type(NotificationType.COMPLAINT)
                .relatedEntityId(complaintId)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    /**
     * Notify user about new comment (excluding the commenter)
     */
    @Transactional
    public void notifyNewComment(Long userId, Long complaintId, String complaintTitle, String commenterName) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title("New Comment")
                .message(commenterName + " commented on complaint: " + complaintTitle)
                .type(NotificationType.COMMENT)
                .relatedEntityId(complaintId)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }
}
