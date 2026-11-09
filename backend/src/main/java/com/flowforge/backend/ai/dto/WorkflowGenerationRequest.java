
package com.flowforge.backend.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WorkflowGenerationRequest(

        @NotBlank(message = "Prompt cannot be empty")
        @Size(max = 2000, message = "Prompt cannot exceed 2000 characters")
        String prompt

) {
}
