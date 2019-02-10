package com.geekq.miaosha.controller;


import com.geekq.miaosha.common.resultbean.ResultObject;
import com.geekq.miaosha.rabbitmq.MQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/rabbit")
public class RabbitmqController {


    @Autowired
    MQSender mqSender;


    @RequestMapping("/testmq")
    @ResponseBody
    public ResultObject<Long> info() {
        ResultObject<Long> result = ResultObject.build();

        mqSender.sendMiaoshaMessagetest("test1");

        return result;
    }

}
