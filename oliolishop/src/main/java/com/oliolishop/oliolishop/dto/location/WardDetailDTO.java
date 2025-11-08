package com.oliolishop.oliolishop.dto.location;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
public class WardDetailDTO  extends WardDTO{
    DistrictDTO district;
    ProvinceDTO province;
}