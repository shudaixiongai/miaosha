package com.geekq.miaosha.dao;

import com.geekq.miaosha.domain.MiaoShaMessageInfo;
import com.geekq.miaosha.domain.MiaoShaMessageUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MiaoShaMessageDao {

    @Select("select * from miaosha_message where messageid =  #{messageid}  ")
    public List<MiaoShaMessageInfo> listMiaoShaMessage(@Param("messageId") String messageId);

    @Select("<script>select * from miaosha_message_user where 1=1 <if test=\"messageId !=null \">and messageId = #{messageId} </if></script>")
    public List<MiaoShaMessageUser> listMiaoShaMessageUser(@Param("messageId") String messageId);

    @Insert("insert into miaosha_message (messageid,content, create_time,status,over_time,message_type,send_type, good_name, price,message_name,user_id)values(" +
            "#{messageId},#{content},#{createTime},#{status},#{overTime},#{messageType},#{sendType},#{goodName},#{price},#{messageName},#{userId} )")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    public long insertMiaoShaMessage(MiaoShaMessageInfo miaoShaMessage);

    @Insert("insert into miaosha_message_user (userid,messageid, goodid,orderid)values(" +
            "#{userId},#{messageId},#{goodId},#{orderId}) ")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    public long insertMiaoShaMessageUser(MiaoShaMessageUser miaoShaMessageUser);


    @Select(" select * from miaosha_message_user mmu , miaosha_message mm where " +
            " mmu.messageid = mm.messageid and  userid=#{userId}")
    public List<MiaoShaMessageInfo> listMiaoShaMessageByUserId(@Param("userId") long userId, @Param("status") Integer status);
}
