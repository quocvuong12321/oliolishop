package com.oliolishop.oliolishop.dto.agent;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatResponse {

    String session_id;
    String user_message;
    String assistant_message;
    LocalDateTime time_stamp;
}
