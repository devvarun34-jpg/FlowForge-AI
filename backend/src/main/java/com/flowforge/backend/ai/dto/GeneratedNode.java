
package com.flowforge.backend.ai.dto;

import tools.jackson.databind.JsonNode;
import com.flowforge.backend.workflow.entity.NodeType;

public record GeneratedNode(
        NodeType type,
        JsonNode configuration,
        Double positionX,
        Double positionY
) {
}
