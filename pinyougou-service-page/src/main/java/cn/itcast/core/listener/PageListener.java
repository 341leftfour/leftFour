package cn.itcast.core.listener;

import cn.itcast.core.service.StaticPageService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 消息处理类
 */
public class PageListener implements MessageListener {

    @Autowired
    private StaticPageService staticPageService;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage atm = (ActiveMQTextMessage) message;
        try {
            String id = atm.getText();
            System.out.println("静态化项目接收到的商品ID:" + id);
            //3: 将商品进行静态化处理   静态化页面 磁盘上
            staticPageService.index(Long.parseLong(id));

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
