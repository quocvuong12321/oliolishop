package com.oliolishop.oliolishop.dto.agent;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatResponse {

    @JsonProperty("session_id")
    String sessionId; // Nên đổi thành camelCase chuẩn Java

    @JsonProperty("user_message")
    String userMessage; // Nên đổi thành camelCase chuẩn Java

    @JsonProperty("assistant_message")
    String assistantMessage; // Nên đổi thành camelCase chuẩn Java

    // Khắc phục lỗi mapping: ánh xạ JSON key "timestamp" vào biến Java timeStamp
    @JsonProperty("timestamp")
    Instant timestamp;
}
