package cn.itcast.core.listener;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.List;

/**
 * 自定义消息处理类
 */
public class ItemDeleteListener implements MessageListener {

    @Autowired
    private SolrTemplate solrTemplate;

    //处理消息的方法  此方法入参就是接收到的消息
    @Override
    public void onMessage(Message message) {
        //接口  实现类
        //有接口 获取此接口的实现类
        //向下强转
        ActiveMQTextMessage atm = (ActiveMQTextMessage) message;

        try {
            //获取消息
            String id = atm.getText();
            System.out.println("搜索项目(删除时)中获取到的商品ID:" + id);
            //2:删除索引库
            SolrDataQuery solrDataQuery = new SimpleQuery("item_goodsid:" + id);
            solrTemplate.delete(solrDataQuery);
            solrTemplate.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
