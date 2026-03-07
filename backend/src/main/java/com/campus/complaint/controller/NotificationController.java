package com.campus.complaint.controller;

import com.campus.complaint.model.Notification;
import com.campus.complaint.security.JwtTokenUtil;
import com.campus.complaint.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notification>> getUserNotifications(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notification>> getUnreadNotifications(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> getUnreadCount(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        Long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(notification);
    }

    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> markAllAsRead(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(Map.of("message", "Notification deleted successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification created = notificationService.createNotification(notification);
        return ResponseEntity.ok(created);
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtTokenUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("Unable to get user ID from token");
    }
}
