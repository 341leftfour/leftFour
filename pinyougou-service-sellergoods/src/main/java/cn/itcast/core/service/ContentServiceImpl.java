package cn.itcast.core.service;

import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {
	
	@Autowired
	private ContentDao contentDao;

	@Override
	public List<Content> findAll() {
		List<Content> list = contentDao.selectByExample(null);
		return list;
	}

	@Override
	public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<Content> page = (Page<Content>)contentDao.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void add(Content content) {
		contentDao.insertSelective(content);
	}



	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public void edit(Content content) {
		// Map content = new HashMap
		// 只删除轮播图  content.delete(catogeryId)


		//1:判断当前广告对象中的广告类型ID 是不是已经修改了
		//根据当前广告对象的ID 查询数据库中广告类型 跟当前页面传递过来的广告中的类型比对
		Content c = contentDao.selectByPrimaryKey(content.getId());
		if(!c.getCategoryId().equals(content.getCategoryId())){
			//不一样  删除当前广告类型ID对应的缓存的同时再删除之前广告类型Id的对应的缓存
			redisTemplate.boundHashOps("content").delete(c.getCategoryId());
		}
		//不管一样还是不一样 现在的必须删除
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());//2
		//2:再修改数据库
		contentDao.updateByPrimaryKeySelective(content);
	}

	@Override
	public Content findOne(Long id) {
		Content content = contentDao.selectByPrimaryKey(id);
		return content;
	}

	@Override
	public void delAll(Long[] ids) {
		if(ids != null){
			for(Long id : ids){
				contentDao.deleteByPrimaryKey(id);
			}
		}
	}

	//根据广告类型ID 查询所有广告结果集
    @Override
    public List<Content> findByCategoryId(Long categoryId) {
		//1:查询缓存
		List<Content> contentList = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);

		//2:没有 查询数据库 保存缓存一份 （存活时间）
		if(null == contentList || contentList.size() == 0){
			ContentQuery contentQuery = new ContentQuery();
			contentQuery.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
			//排序
			contentQuery.setOrderByClause("sort_order desc");

			//查询结果集
			contentList = contentDao.selectByExample(contentQuery);
			//保存一份到缓存中
			redisTemplate.boundHashOps("content").put(categoryId,contentList);
			//时间
			redisTemplate.boundHashOps("content").expire(4, TimeUnit.HOURS);

		}
		Long c = redisTemplate.boundHashOps("content").getExpire();
		System.out.println(c);
		//3: 直接广告结果集
		return contentList;

    }

}
