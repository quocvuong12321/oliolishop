package com.oliolishop.oliolishop.dto.agent;

import com.oliolishop.oliolishop.entity.HistoryChat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class HistoryChatResponse {

    String id;
    String sessionId;
    String message;
    HistoryChat.RoleChat role;
    LocalDateTime createdAt;

}
