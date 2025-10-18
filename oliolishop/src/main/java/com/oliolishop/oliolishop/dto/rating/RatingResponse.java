package com.oliolishop.oliolishop.dto.rating;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatingResponse {
    String id;
    String customerAvatarUrl;
    String customerName;
    double star;
    String comment;
    int likeCount;
    String[] attachedImageUrls;
    Boolean isLiked;
    LocalDateTime createDate;
    String shopReply;
    LocalDateTime replyDate;
}
