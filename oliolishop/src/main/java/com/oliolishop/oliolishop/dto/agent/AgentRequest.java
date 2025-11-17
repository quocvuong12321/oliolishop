package com.oliolishop.oliolishop.dto.agent;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AgentRequest {
    String message;
    @JsonProperty("user_id")  // Map sang snake_case
    String userId;
    @JsonProperty("session_id")  // Map sang snake_case
    String sessionId;
}
