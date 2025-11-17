package com.oliolishop.oliolishop.dto.agent;


import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatRequest {

    String sessionId;
    String message;

}
