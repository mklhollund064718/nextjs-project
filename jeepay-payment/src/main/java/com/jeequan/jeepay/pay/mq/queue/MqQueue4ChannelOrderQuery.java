/*
 * Copyright (c) 2021-2031, 河北计全科技有限公司 (https://www.jeequan.com & jeequan@126.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jeequan.jeepay.pay.mq.queue;

import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.pay.mq.MqReceiveServiceImpl;
import com.jeequan.jeepay.pay.mq.queue.service.MqChannelOrderQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;
import javax.jms.TextMessage;

/*
 * 上游渠道订单轮询查单
 * 如：微信的条码支付，没有回调接口， 需要轮询查单完成交易结果通知。
 *
*
* @author terrfly
* @site https://www.jeepay.vip
* @date 2021/6/8 17:30
*/
@Slf4j
@Component
@Profile(CS.MQTYPE.ACTIVE_MQ)
public class MqQueue4ChannelOrderQuery extends MqChannelOrderQueryService {

    @Autowired private JmsTemplate jmsTemplate;
    @Autowired private MqReceiveServiceImpl mqReceiveServiceImpl;

    @Bean("activeChannelOrderQuery")
    public Queue mqQueue4ChannelOrderQuery(){
        return new ActiveMQQueue(CS.MQ.QUEUE_CHANNEL_ORDER_QUERY);
    }

    @Lazy
    @Autowired
    @Qualifier("activeChannelOrderQuery")
    private Queue mqQueue4ChannelOrderQuery;

    /** 发送MQ消息 **/
    @Override
    public void send(String msg) {
        this.jmsTemplate.convertAndSend(mqQueue4ChannelOrderQuery, msg);
    }

    /** 发送MQ消息 **/
    @Override
    public void send(String msg, long delay) {
        jmsTemplate.send(mqQueue4ChannelOrderQuery, session -> {
            TextMessage tm = session.createTextMessage(msg);
            tm.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
            tm.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, 1*1000);
            tm.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, 1);
            return tm;
        });
    }


    /** 接收 查单消息 **/
    @JmsListener(destination = CS.MQ.QUEUE_CHANNEL_ORDER_QUERY)
    public void receive(String msg) {
        mqReceiveServiceImpl.channelOrderQuery(msg);
    }


}
