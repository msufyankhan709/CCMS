package com.campus.complaint.controller;

import com.campus.complaint.model.ComplaintComment;
import com.campus.complaint.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<?> addComment(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            Long complaintId = Long.valueOf(request.get("complaintId").toString());
            String content = (String) request.get("content");

            ComplaintComment comment = commentService.addComment(complaintId, userId, content);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/complaint/{complaintId}")
    public ResponseEntity<?> getCommentsByComplaint(@PathVariable Long complaintId) {
        try {
            List<ComplaintComment> comments = commentService.getCommentsByComplaint(complaintId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
