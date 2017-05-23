package com.jk.service.brand.impl;

import com.jk.dao.brand.BrandDao;
import com.jk.pojo.brand.Brand;
import com.jk.service.brand.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by nihao on 2017/5/20.
 */
@Service
public class BrandServiceImpl implements BrandService{

    @Autowired
    private BrandDao brandDao;

    @Override
    public int findBrandCount(Brand brand) {
        return brandDao.findBrandCount(brand);
    }

    @Override
    public List<Brand> findBrandList(Brand brand) {
        return brandDao.findBrandList(brand);
    }

    @Override
    public void insertBrand(Brand brand) {
        brandDao.insertBrand(brand);
    }
}
