package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.discount_rule.DiscountRuleRequest;
import com.oliolishop.oliolishop.dto.discount_rule.DiscountRuleResponse;
import com.oliolishop.oliolishop.entity.DiscountRule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface DiscountRuleMapper {

    DiscountRule toDiscountRule(DiscountRuleRequest request);

    DiscountRuleResponse tResponse(DiscountRule discountRule);

}
