package com.jk.service.brand;

import com.jk.pojo.brand.Brand;

import java.util.List;

/**
 * Created by nihao on 2017/5/20.
 */
public interface BrandService {
    int findBrandCount(Brand brand);

    List<Brand> findBrandList(Brand brand);

    void insertBrand(Brand brand);
}
