package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BannerRepository extends JpaRepository<Banner,String>, JpaSpecificationExecutor<Banner> {
}
