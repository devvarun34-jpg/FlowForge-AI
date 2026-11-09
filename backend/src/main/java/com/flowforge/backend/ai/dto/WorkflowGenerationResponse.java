
package com.flowforge.backend.ai.dto;

import java.util.List;

public record WorkflowGenerationResponse(
        String summary,
        List<GeneratedNode> nodes,
        List<GeneratedEdge> edges
) {
}
