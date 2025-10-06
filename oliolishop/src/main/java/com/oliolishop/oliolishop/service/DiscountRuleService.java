package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.dto.discount_rule.DiscountRuleRequest;
import com.oliolishop.oliolishop.dto.discount_rule.DiscountRuleResponse;
import com.oliolishop.oliolishop.entity.DiscountRule;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.DiscountRuleMapper;
import com.oliolishop.oliolishop.repository.DiscountRuleRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class DiscountRuleService {
    DiscountRuleRepository discountRuleRepository;
    DiscountRuleMapper discountRuleMapper;

    public Page<DiscountRuleResponse> getDiscountRule(int page,int size){
        return discountRuleRepository.findAll(PageRequest.of(page, size)).map(discountRuleMapper::tResponse);
    }

    public DiscountRuleResponse createDiscountRule(DiscountRuleRequest request){

        DiscountRule rule = discountRuleMapper.toDiscountRule(request);
        return discountRuleMapper.tResponse(discountRuleRepository.save(rule));

    }

    public DiscountRuleResponse getDiscountRuleById(String id) {
        DiscountRule rule = discountRuleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_RULE_NOT_EXISTED));
        return discountRuleMapper.tResponse(rule);
    }


}
