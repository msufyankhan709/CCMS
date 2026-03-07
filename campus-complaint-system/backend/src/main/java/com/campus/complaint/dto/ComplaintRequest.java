package com.campus.complaint.dto;

import com.campus.complaint.model.Priority;
import com.campus.complaint.model.Visibility;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String category;
    
    private String location;

    @NotBlank(message = "Description is required")
    private String description;

    private Priority priority = Priority.MEDIUM;

    private String attachmentUrl;

    private Visibility visibility = Visibility.PUBLIC;
}
