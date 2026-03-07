package com.campus.complaint.repository;

import com.campus.complaint.model.ComplaintComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintCommentRepository extends JpaRepository<ComplaintComment, Long> {
    List<ComplaintComment> findByComplaint_IdOrderByTimestampAsc(Long complaintId);

    List<ComplaintComment> findByUser_IdOrderByTimestampDesc(Long userId);
}
