package com.campus.complaint.service;

import com.campus.complaint.model.Role;
import com.campus.complaint.model.User;
import com.campus.complaint.repository.UserRepository;
import com.campus.complaint.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User updateUserRole(Long userId, Role newRole) {
        User user = getUserById(userId);
        user.setRole(newRole);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long userId, User updatedUser) {
        User existingUser = getUserById(userId);
        
        // Protect base admin account
        if ("admin".equals(existingUser.getUsername())) {
            throw new RuntimeException("Cannot modify the base admin account");
        }

        // Update allowed fields
        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setRole(updatedUser.getRole());
        existingUser.setActive(updatedUser.getActive());
        existingUser.setDepartment(updatedUser.getDepartment());

        // Keep technician type only for technician role
        if (updatedUser.getRole() == Role.TECHNICIAN) {
            existingUser.setTechnicianType(updatedUser.getTechnicianType());
        } else {
            existingUser.setTechnicianType(null);
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    public User createUser(com.campus.complaint.dto.SignupRequest request) {
        // Determine who is creating this user
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Not authenticated");
        }

        String creatorEmail = auth.getName();
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("Creator user not found"));

        if (creator.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admins can create users");
        }

        // Enforce creation rules:
        // - Students/Faculty sign up themselves (via /auth/signup)
        // - Base admin (username 'admin') can create ADMIN and TECHNICIAN users
        // - Other admins can create only TECHNICIAN users
        Role targetRole = request.getRole();
        boolean isBaseAdmin = "admin".equals(creator.getUsername());

        if (targetRole == Role.STUDENT || targetRole == Role.FACULTY) {
            throw new RuntimeException("Admins cannot create STUDENT or FACULTY accounts. They should sign up themselves.");
        }

        if (targetRole == Role.ADMIN && !isBaseAdmin) {
            throw new RuntimeException("Only base admin can create other ADMIN users.");
        }

        if (targetRole != Role.TECHNICIAN && targetRole != Role.ADMIN) {
            throw new RuntimeException("Invalid role for admin-created user.");
        }

        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(24);

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .department(request.getDepartment())
                .role(targetRole)
                .technicianType(targetRole == Role.TECHNICIAN ? request.getTechnicianType() : null)
                .active(true)
                .emailVerified(false)
                .verificationToken(verificationToken)
                .verificationTokenExpiry(tokenExpiry)
                .build();

        user = userRepository.save(user);

        // Send verification email
        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getFullName(), verificationToken);
        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
        }

        return user;
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = getUserById(userId);

        // Check if user has any non-CLOSED complaints
        List<com.campus.complaint.model.Complaint> studentComplaints = complaintRepository.findByStudent_Id(userId);
        List<com.campus.complaint.model.Complaint> technicianComplaints = complaintRepository.findByTechnician_Id(userId);

        boolean hasActiveComplaints = studentComplaints.stream()
                .anyMatch(c -> c.getStatus() != com.campus.complaint.model.ComplaintStatus.CLOSED);

        if (!hasActiveComplaints) {
            hasActiveComplaints = technicianComplaints.stream()
                    .anyMatch(c -> c.getStatus() != com.campus.complaint.model.ComplaintStatus.CLOSED);
        }

        if (hasActiveComplaints) {
            String studentMsg = studentComplaints.stream()
                    .map(c -> "#" + c.getId() + ":" + c.getStatus())
                    .collect(java.util.stream.Collectors.joining(", "));
            String techMsg = technicianComplaints.stream()
                    .map(c -> "#" + c.getId() + ":" + c.getStatus())
                    .collect(java.util.stream.Collectors.joining(", "));
            throw new RuntimeException("Cannot deactivate user with active complaints. Found Student: [" + studentMsg + "], Tech: [" + techMsg + "]");
        }

        user.setActive(false);
        userRepository.save(user);
    }
}
