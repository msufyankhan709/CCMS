package com.campus.complaint.repository;

import com.campus.complaint.model.Complaint;
import com.campus.complaint.model.ComplaintStatus;
import com.campus.complaint.model.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStudent_Id(Long studentId);

    List<Complaint> findByTechnician_Id(Long technicianId);

    List<Complaint> findByStatus(ComplaintStatus status);

    List<Complaint> findByOrderByCreatedAtDesc();

    List<Complaint> findByVisibilityOrderByCreatedAtDesc(Visibility visibility);

    long countByStatus(ComplaintStatus status);
}
