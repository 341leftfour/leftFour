package vo;

import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 包装对象
 * 组合对象
 */
public class SpecificationVo implements Serializable{

    //规格对象
    private Specification specification;
    //规格选项结果集对象
    private List<SpecificationOption> specificationOptionList;

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public List<SpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<SpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecificationVo that = (SpecificationVo) o;
        return Objects.equals(specification, that.specification) &&
                Objects.equals(specificationOptionList, that.specificationOptionList);
    }

    @Override
    public int hashCode() {

        return Objects.hash(specification, specificationOptionList);
    }
}
