package com.geekq.miaosha.rabbitmq;

import com.geekq.miaosha.common.SnowflakeIdWorker;
import com.geekq.miaosha.common.enums.MessageStatus;
import com.geekq.miaosha.domain.MiaoshaOrder;
import com.geekq.miaosha.domain.MiaoshaUser;
import com.geekq.miaosha.domain.OrderInfo;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miaosha.service.GoodsService;
import com.geekq.miaosha.service.MiaoShaMessageService;
import com.geekq.miaosha.service.MiaoshaService;
import com.geekq.miaosha.service.OrderService;
import com.geekq.miaosha.vo.GoodsVo;
import com.geekq.miaosha.vo.MiaoShaMessageVo;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    MiaoShaMessageService messageService;

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(Message message,Channel channel) throws IOException {
        String msg = new String(message.getBody(), "UTF-8");
        log.info("receive message:" + msg);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);

        MiaoshaMessage mm = RedisService.stringToBean(msg, MiaoshaMessage.class);
        MiaoshaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(Long.valueOf(user.getNickname()), goodsId);
        if (order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        OrderInfo miaosha = miaoshaService.miaosha(user, goods);


        // 消息模板
        MiaoShaMessageVo vo = new MiaoShaMessageVo();
        vo.setContent("尊敬的用户你好，你已经成功秒杀该商品！");
        vo.setCreateTime(new Date());
        vo.setMessageId(SnowflakeIdWorker.getOrderId(0,0));
        vo.setSendType(0);
        vo.setStatus(0);
        vo.setMessageType(MessageStatus.messageType.maiosha_message.ordinal());
        vo.setUserId(user.getId());
        vo.setMessageHead("秒杀成功");

        // message持久化
        messageService.insertMiaosha(mm,miaosha,vo);

    }


    @RabbitListener(queues = MQConfig.MIAOSHATEST)
    public void receiveMiaoShaMessage(Message message, Channel channel) throws IOException {
        log.info("接受到的消息为:{}", message);
        String messRegister = new String(message.getBody(), "UTF-8");
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        MiaoShaMessageVo msm = RedisService.stringToBean(messRegister, MiaoShaMessageVo.class);
        messageService.insertMs(msm);
    }
}
