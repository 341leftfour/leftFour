package cn.itcast.core.controller;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.service.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 网站前台之广告管理
 */
@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    //根据广告类型Id 查询所有广告
    @RequestMapping("/findByCategoryId")
    public List<Content> findByCategoryId(Long categoryId){
        return contentService.findByCategoryId(categoryId);
    }

}
