package com.oliolishop.oliolishop.service;

import com.oliolishop.oliolishop.dto.descriptionattr.DescriptionAttrRequest;
import com.oliolishop.oliolishop.dto.descriptionattr.DescriptionAttrResponse;
import com.oliolishop.oliolishop.entity.DescriptionAttr;
import com.oliolishop.oliolishop.entity.ProductSpu;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.DescriptionAttrMapper;
import com.oliolishop.oliolishop.repository.DescriptionAttrRepository;
import com.oliolishop.oliolishop.ultils.AppUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DescriptionAttrService {
    DescriptionAttrRepository descriptionAttrRepository;
    DescriptionAttrMapper descriptionAttrMapper;


    public DescriptionAttrResponse createDescriptionAttr(DescriptionAttrRequest request, ProductSpu spu) {

        boolean check = descriptionAttrRepository.existsByNameAndSpu_Id(request.getName(), spu.getId());
        if (check) {
            throw new AppException(ErrorCode.ATTRIBUTE_EXISTED);
        }

        long count = descriptionAttrRepository.count();
        String id = AppUtils.generateId(count);


        DescriptionAttr da = descriptionAttrMapper.toDescriptionAttr(request);
        da.setId(id);
        da.setSpu(spu);
        return descriptionAttrMapper.toResponse(descriptionAttrRepository.save(da));
    }

    public List<DescriptionAttrResponse> createDescriptionAttrs(List<DescriptionAttrRequest> requests, ProductSpu spu) {
        long count = descriptionAttrRepository.count();
        Set<String> namesInRequest = new HashSet<>();

        List<DescriptionAttr> entities = new ArrayList<>();

        for (DescriptionAttrRequest req : requests) {
            // Kiểm tra trùng trong cùng 1 list
            if (!namesInRequest.add(req.getName())) {
                throw new AppException(ErrorCode.ATTRIBUTE_EXISTED);
            }

            // Kiểm tra trùng với DB
            if (descriptionAttrRepository.existsByNameAndSpu_Id(req.getName(), spu.getId())) {
                throw new AppException(ErrorCode.ATTRIBUTE_EXISTED);
            }

            // Map request → entity
            DescriptionAttr da = descriptionAttrMapper.toDescriptionAttr(req);
            da.setId(AppUtils.generateId(count++));
            da.setSpu(spu);
            entities.add(da);
        }

        // Lưu batch
        List<DescriptionAttr> savedEntities = descriptionAttrRepository.saveAll(entities);

        // Map entity → response
        return savedEntities.stream()
                .map(descriptionAttrMapper::toResponse)
                .toList();
    }


}
