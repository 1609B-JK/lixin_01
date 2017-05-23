package com.jk.controller.brand;

import com.jk.pojo.brand.Brand;
import com.jk.service.brand.BrandService;
import common.util.FTPUtil;
import common.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by nihao on 2017/5/20.
 */
@Controller
public class BrandController {

    @Autowired
    private BrandService brandService;

    @RequestMapping("toBrandListPage")
    public String toBrandListPage(){
        return "brand/brand_list";
    }

    @RequestMapping("findBrandJson")
    @ResponseBody
    public Map<String,Object> findBrandJson(Brand brand,int offset, int limit){

        if (1 > limit) {
            limit = 3;
        }
        brand.setStartPos(offset);
        brand.setPageSize(limit);
        //��ѯ������
        int totalCount = brandService.findBrandCount(brand);
        brand.setTotalCount(totalCount);
        //�����ҳ��ϸ
        brand.countInfo();
        List<Brand> bList = brandService.findBrandList(brand);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("total", totalCount);
        map.put("rows", bList);
        return map;
    }

    @RequestMapping("uploadBrandMainImage")
    @ResponseBody
    public Brand uploadBrandMainImage(@RequestParam MultipartFile brandMainImage,HttpServletRequest request){
        Brand brand = null;
        //�ж��ļ��Ƿ�Ϊ��
        if (null != brandMainImage){
            brand = new Brand();
            //�ж��ļ�ָ��
            try {
                //ftp����ķ�ʽ
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String format = "imgs/" + sdf.format(new Date()) + "/";
                //��ȡԭʼ����
                String originalFilename = brandMainImage.getOriginalFilename();
                //��ԭʼ�����н�ȡ��׺��
                String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
                //�����µ��ļ���
                String fileName = UUID.randomUUID().toString() + fileSuffix;
                //���ñ���
                boolean boo = FTPUtil.uploadFile(brandMainImage.getInputStream(), fileName, format);
                if (boo) {
                    //�����ļ��ı���·��
                    brand.setBrandImgUrl("/" + format + fileName);
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return brand;
    }

    @RequestMapping("insertBrand")
    @ResponseBody
    public String insertBrand(Brand brand){
        brandService.insertBrand(brand);
        return "{}";
    }

}
