package com.geekq.miaosha.service;

import com.geekq.miaosha.common.SnowflakeIdWorker;
import com.geekq.miaosha.common.enums.MessageStatus;
import com.geekq.miaosha.dao.GoodsDao;
import com.geekq.miaosha.dao.MiaoShaMessageDao;
import com.geekq.miaosha.domain.MiaoShaMessageInfo;
import com.geekq.miaosha.domain.MiaoShaMessageUser;
import com.geekq.miaosha.domain.OrderInfo;
import com.geekq.miaosha.rabbitmq.MiaoshaMessage;
import com.geekq.miaosha.utils.DateTimeUtils;
import com.geekq.miaosha.utils.UUIDUtil;
import com.geekq.miaosha.vo.GoodsVo;
import com.geekq.miaosha.vo.MiaoShaMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class MiaoShaMessageService {

    @Autowired
    private MiaoShaMessageDao messageDao;

    @Autowired
    private GoodsDao goodsDao;

    public List<MiaoShaMessageInfo> getmessageUserList( Long userId , Integer status ){
        return messageDao.listMiaoShaMessageByUserId(userId,status);
    }


    @Transactional(rollbackFor = Exception.class)
    public void insertMs(MiaoShaMessageVo miaoShaMessageVo){
        MiaoShaMessageUser mu = new MiaoShaMessageUser() ;
        mu.setUserId(miaoShaMessageVo.getUserId());
        mu.setMessageId(miaoShaMessageVo.getMessageId());
        messageDao.insertMiaoShaMessageUser(mu);
        MiaoShaMessageInfo miaoshaMessage = new MiaoShaMessageInfo();
        miaoshaMessage.setContent(miaoShaMessageVo.getContent());
        miaoshaMessage.setCreateTime(new Date());
        miaoshaMessage.setStatus(miaoShaMessageVo.getStatus());
        miaoshaMessage.setMessageType(miaoShaMessageVo.getMessageType());
        miaoshaMessage.setSendType(miaoShaMessageVo.getSendType());
        miaoshaMessage.setMessageId(miaoShaMessageVo.getMessageId());
        miaoshaMessage.setCreateTime(new Date());
        messageDao.insertMiaoShaMessage(miaoshaMessage);
    }

    /**
     * 秒殺消息
     * @param mm
     * @param info
     * @param vo
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertMiaosha(MiaoshaMessage mm, OrderInfo info,MiaoShaMessageVo vo){
        MiaoShaMessageUser mu = new MiaoShaMessageUser() ;
        mu.setUserId(mm.getUser().getId());
        long messageId = SnowflakeIdWorker.getMessageId(0, 0);
        mu.setMessageId(messageId);
        mu.setGoodId(mm.getGoodsId());
        mu.setOrderId(info.getId());
        long insertResult = messageDao.insertMiaoShaMessageUser(mu);
        MiaoShaMessageInfo miaoshaMessage = new MiaoShaMessageInfo();
        miaoshaMessage.setContent(vo.getContent());
        miaoshaMessage.setCreateTime(new Date());
        miaoshaMessage.setStatus(vo.getStatus());
        miaoshaMessage.setOverTime(new Date());
        // 查询秒杀商品名称
        GoodsVo goodInfo = goodsDao.getGoodsVoByGoodsId(mm.getGoodsId());
        // 秒杀商品
        miaoshaMessage.setGoodName(goodInfo.getGoodsName());
        // 秒杀用户
        miaoshaMessage.setUserId(Long.parseLong(mm.getUser().getNickname()));

        miaoshaMessage.setMessageType(vo.getMessageType());
        miaoshaMessage.setSendType(vo.getSendType());
        miaoshaMessage.setMessageId(messageId);
        miaoshaMessage.setCreateTime(new Date());
        miaoshaMessage.setMessageName("秒杀成功");

        long result = messageDao.insertMiaoShaMessage(miaoshaMessage);
    }
}
