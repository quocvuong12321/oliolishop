package com.oliolishop.oliolishop.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.oliolishop.oliolishop.dto.rating.RatingRequest;
import com.oliolishop.oliolishop.dto.rating.RatingResponse;
import com.oliolishop.oliolishop.entity.*;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.RatingMapper;
import com.oliolishop.oliolishop.repository.*;
import com.oliolishop.oliolishop.util.AppUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@AllArgsConstructor
public class RatingService {
    RatingRepository ratingRepository;
    private final RatingLikeRepository ratingLikeRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;
    private final RatingMapper ratingMapper;
    private final ProductSpuRepository productSpuRepository;

    public List<RatingResponse> getRatingForDetailProduct(String productSpuId,int page,int size)  {
        String customerId  = AppUtils.getOptionalCustomerId();

        Pageable pageable = PageRequest.of(page,size);

        List<Rating> ratings = ratingRepository.findByProductSpu_IdOrderByCreateDateDesc(productSpuId,pageable).getContent();

        Set<String> likedRatingIds = new HashSet<>();

        if(customerId != null){
            List<String> ratingIds = ratings.stream().map(Rating::getId).toList();

            List<String> ratingLikedIds = ratingLikeRepository.findLikedRatingIdsByCustomerIdAndRatingIds(customerId,ratingIds);

            likedRatingIds.addAll(ratingLikedIds);
        }


        List<RatingResponse> responses = new ArrayList<>();

        ratings.forEach(r ->{
            Customer  c = r.getCustomer();
            String[] attachedImages;
            try {
                attachedImages = AppUtils.parseStringToArray(r.getImages());
            } catch (JsonProcessingException e) {
                attachedImages = new String[0];
            }
            RatingResponse response = RatingResponse.builder()
                    .id(r.getId())
                    .star(r.getStar())
                    .customerAvatarUrl(c.getImage())
                    .customerName(c.getName())
                    .likeCount(r.getLikesCount())
                    .isLiked(likedRatingIds.contains(r.getId()))
                    .shopReply(r.getShopReply())
                    .comment(r.getComment())
                    .createDate(r.getCreateDate())
                    .replyDate(r.getReplyDate())
                    .attachedImageUrls(attachedImages)
                    .build();
            responses.add(response);
        });

        return responses;
    }

    @Transactional
    public RatingResponse createRating(RatingRequest request, String imageDir, String folderName,List<MultipartFile> files) throws IOException {

        String customerId = AppUtils.getCustomerIdByJwt();

        if(ratingRepository.existsByCustomer_IdAndOrderItem_Id(customerId, request.getOrderItemId())){
            throw new AppException(ErrorCode.RATED);
        }

        Customer customer = customerRepository.findById(customerId).orElseThrow(()->new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));
        OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId()).orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_EXISTED));
        ProductSpu spu = productSpuRepository.findById(request.getProductSpuId()).orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_EXIST));

        if (!orderItem.getOrder().getCustomer().getId().equals(customerId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        List<String> imageUrls= new ArrayList<>();
        if(!files.isEmpty()){
            List<MultipartFile> validFiles = files.stream()
                    .filter(file -> !file.isEmpty())
                    .toList();

            if (!validFiles.isEmpty()) {
                imageUrls = saveRatingImages(orderItem.getId(),imageDir,folderName,validFiles);
            }
        }

        Rating rating = ratingMapper.toRating(request);
        rating.setId(UUID.randomUUID().toString());
        rating.setCustomer(customer);
        rating.setImages(AppUtils.arrayToPythonList(imageUrls.toArray(String[]::new)));
        rating.setLikes(new HashSet<>());
        rating.setOrderItem(orderItem);
        rating.setProductSpu(spu);
        rating.setLikesCount(0);
        rating.setIsHidden(false);

        Rating saved = ratingRepository.save(rating);
        RatingResponse response = ratingMapper.toResponse(saved);
        response.setCustomerName(customer.getName());
        response.setIsLiked(false);
        response.setAttachedImageUrls(AppUtils.parseStringToArray(saved.getImages()));
        response.setLikeCount(saved.getLikesCount());
        response.setCustomerAvatarUrl(customer.getImage());

        return response;
    }

    private List<String> saveRatingImages(String orderItemId, String imageDir, String folderName,List<MultipartFile> files) throws IOException {
        List<String> mediaUrls = new ArrayList<>();
        int index = 0;

        for (MultipartFile file : files) {
            // Tên file cơ sở: OrderItemId + Timestamp + Index
            // Sử dụng UUID cho file name base là cách mạnh mẽ hơn nữa để đảm bảo duy nhất
            String fileNameBase = orderItemId + "_" + System.currentTimeMillis() + "_" + index++;
            // AppUtils.saveImage sẽ lo resize, định dạng, và tạo thư mục
            String url = AppUtils.saveImage(file, imageDir, folderName, fileNameBase);
            mediaUrls.add(url);
        }
        return mediaUrls;
    }

    public void likeRating(String ratingId){

        String customerId = AppUtils.getCustomerIdByJwt();

        if(ratingLikeRepository.existsByCustomer_IdAndRating_Id(customerId,ratingId))
            throw new AppException(ErrorCode.RATED);

        RatingLike ratingLike = RatingLike.builder()
                .customer(Customer.builder().id(customerId).build())
                .rating(Rating.builder().id(ratingId).build())
                .build();

        ratingLikeRepository.save(ratingLike);
    }

    public void cancelLikeRating(String ratingId){

        String customerId = AppUtils.getCustomerIdByJwt();

        RatingLike ratingLike = ratingLikeRepository.findByCustomer_IdAndRating_Id(customerId,ratingId);

        ratingLikeRepository.delete(ratingLike);
    }
}
