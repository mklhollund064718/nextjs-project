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
package com.jeequan.jeepay.mgr.ctrl.order;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jeequan.jeepay.core.constants.ApiCodeEnum;
import com.jeequan.jeepay.core.entity.RefundOrder;
import com.jeequan.jeepay.core.model.ApiRes;
import com.jeequan.jeepay.mgr.ctrl.CommonCtrl;
import com.jeequan.jeepay.service.impl.RefundOrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 退款订单类
 *
 * @author pangxiaoyu
 * @site https://www.jeepay.vip
 * @date 2021-06-07 07:15
 */
@RestController
@RequestMapping("/api/refundOrder")
public class RefundOrderController extends CommonCtrl {

    @Autowired private RefundOrderService refundOrderService;

    /**
     * @author: pangxiaoyu
     * @date: 2021/6/7 16:15
     * @describe: 退款订单信息列表
     */
    @PreAuthorize("hasAuthority('ENT_REFUND_LIST')")
    @RequestMapping(value="", method = RequestMethod.GET)
    public ApiRes list() {

        RefundOrder refundOrder = getObject(RefundOrder.class);
        JSONObject paramJSON = getReqParamJSON();
        LambdaQueryWrapper<RefundOrder> wrapper = RefundOrder.gw();
        if (StringUtils.isNotEmpty(refundOrder.getRefundOrderId())) wrapper.eq(RefundOrder::getRefundOrderId, refundOrder.getRefundOrderId());
        if (StringUtils.isNotEmpty(refundOrder.getPayOrderId())) wrapper.eq(RefundOrder::getPayOrderId, refundOrder.getPayOrderId());
        if (StringUtils.isNotEmpty(refundOrder.getChannelPayOrderNo())) wrapper.eq(RefundOrder::getChannelPayOrderNo, refundOrder.getChannelPayOrderNo());
        if (StringUtils.isNotEmpty(refundOrder.getMchNo())) wrapper.eq(RefundOrder::getMchNo, refundOrder.getMchNo());
        if (StringUtils.isNotEmpty(refundOrder.getIsvNo())) wrapper.eq(RefundOrder::getIsvNo, refundOrder.getIsvNo());
        if (refundOrder.getMchType() != null) wrapper.eq(RefundOrder::getMchType, refundOrder.getMchType());
        if (StringUtils.isNotEmpty(refundOrder.getMchRefundNo())) wrapper.eq(RefundOrder::getMchRefundNo, refundOrder.getMchRefundNo());
        if (refundOrder.getState() != null) wrapper.eq(RefundOrder::getState, refundOrder.getState());
        if (StringUtils.isNotEmpty(refundOrder.getChannelPayOrderNo())) wrapper.eq(RefundOrder::getChannelPayOrderNo, refundOrder.getChannelPayOrderNo());
        if (refundOrder.getResult() != null) wrapper.eq(RefundOrder::getResult, refundOrder.getResult());
        if (paramJSON != null) {
            if (StringUtils.isNotEmpty(paramJSON.getString("createdStart"))) wrapper.ge(RefundOrder::getCreatedAt, paramJSON.getString("createdStart"));
            if (StringUtils.isNotEmpty(paramJSON.getString("createdEnd"))) wrapper.le(RefundOrder::getCreatedAt, paramJSON.getString("createdEnd"));
        }
        wrapper.orderByDesc(RefundOrder::getCreatedAt);
        IPage<RefundOrder> pages = refundOrderService.page(getIPage(), wrapper);

        return ApiRes.page(pages);
    }

    /**
     * @author: pangxiaoyu
     * @date: 2021/6/7 16:15
     * @describe: 退款订单信息
     */
    @PreAuthorize("hasAuthority('ENT_REFUND_ORDER_VIEW')")
    @RequestMapping(value="/{refundOrderId}", method = RequestMethod.GET)
    public ApiRes detail(@PathVariable("refundOrderId") String refundOrderId) {
        RefundOrder refundOrder = refundOrderService.getById(refundOrderId);
        if (refundOrder == null) return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_SELETE);
        return ApiRes.ok(refundOrder);
    }
}
