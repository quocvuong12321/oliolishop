package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.rating.RatingRequest;
import com.oliolishop.oliolishop.dto.rating.RatingResponse;
import com.oliolishop.oliolishop.entity.Rating;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface RatingMapper {

    Rating toRating(RatingRequest request);

    RatingResponse toResponse (Rating rating);

}
