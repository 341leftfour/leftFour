package cn.itcast.core.controller;

import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 模板管理
 */
//@SuppressWarnings("all")
@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    @RequestMapping("/findAll")
    public List<TypeTemplate> findAll(){
        return typeTemplateService.findAll();
    };

    //开始审核
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status){
        try {
            typeTemplateService.updateStatus(ids,status);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    //查询
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody TypeTemplate typeTemplate){
        return typeTemplateService.search(page,rows,typeTemplate);
    }
    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody TypeTemplate typeTemplate){
        try {
            typeTemplateService.add(typeTemplate);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    //添加
    @RequestMapping("/update")
    public Result update(@RequestBody TypeTemplate typeTemplate){
        try {
            typeTemplateService.update(typeTemplate);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    //查询一个模板对象
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }

    //删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){

        try {
            typeTemplateService.delete(ids);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }

}
