package cn.itcast.core.controller;

import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.SpecificationVo;

import java.util.List;
import java.util.Map;

/**
 * 规格管理
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("/specification")
public class SpecificationController {


    @Reference
    private SpecificationService specificationService;

    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status){

        try {
            if (ids==null && ids.length==0){
                return null;
            }
            specificationService.updateStatus(     ids,  status);
            return new Result(true,"状态成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(true,"状态失败");
    }
    //查询分页 条件
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody Specification specification){
        return specificationService.search(page,rows,specification);
    }
    //规格保存
    @RequestMapping("/add")
    public Result add(@RequestBody SpecificationVo vo){

        try {

            specificationService.add(vo);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }

    }
    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody SpecificationVo vo){

        try {
            specificationService.update(vo);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }

    }
    //查询一个规格包装对象
    @RequestMapping("/findOne")
    public SpecificationVo findOne(Long id){
        return specificationService.findOne(id);
    }

    //查询所有规格 返回值List<Map>
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return specificationService.selectOptionList();
    }

    @RequestMapping("/importExcel")
    public Result importExcel(){
        try {
            specificationService.importExcel();
            return new Result(true,"导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"导入失败");
        }
    }

    @RequestMapping("/exportExcel")
    public Result exportExcel(){
        try {
            specificationService.exportExcel();
            return new Result(true,"导出成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"导出失败");
        }
    }
}
