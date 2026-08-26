package com.example.jingdongdemo.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.jingdongdemo.common.enums.OrderStatusEnum;
import com.example.jingdongdemo.dto.OrderPageRequest;
import com.example.jingdongdemo.dto.OrderRequest;
import com.example.jingdongdemo.entity.Address;
import com.example.jingdongdemo.entity.Order;
import com.example.jingdongdemo.entity.OrderItem;
import com.example.jingdongdemo.event.OrderCreatedEvent;
import com.example.jingdongdemo.mapper.*;
import com.example.jingdongdemo.service.OrderService;
import com.example.jingdongdemo.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationContext applicationContext;


    @Override
    public OrderConfirmVO OrderConfirm() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        OrderConfirmAddressVO orderConfirmAddressVO = orderMapper.getDefaultAddress(userId);
        List<OrderConfirmItemVO> orderConfirmItemVOList = orderMapper.getItems(userId);
//        List<OrderConfirmAvailableCouponsVO> orderConfirmAvailableCouponsVOList = orderMapper.getAvailableCoupons(userId);
        List<OrderConfirmAvailableCouponsVO> orderConfirmAvailableCouponsVOList = Collections.emptyList();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for(OrderConfirmItemVO itemVO : orderConfirmItemVOList){
            totalAmount=totalAmount.add(itemVO.getSubtotal());
        }

        OrderConfirmVO orderConfirmVO = new OrderConfirmVO();
        orderConfirmVO.setAddress(orderConfirmAddressVO);
        orderConfirmVO.setItems(orderConfirmItemVOList);
        orderConfirmVO.setAvailableCoupons(orderConfirmAvailableCouponsVOList);
        orderConfirmVO.setTotalAmount(totalAmount);
        orderConfirmVO.setFreight(BigDecimal.ZERO);
        orderConfirmVO.setPayAmount(totalAmount.add(BigDecimal.ZERO));
        return orderConfirmVO;
    }

    @Transactional
    @Override
    public OrderVO createOrder(OrderRequest orderRequest) {

        String orderNo = "OD"+ IdUtil.getSnowflakeNextId();

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<OrderConfirmItemVO>  orderConfirmItemVOList = orderMapper.getItems(userId);

        for (OrderConfirmItemVO item : orderConfirmItemVOList) {
            String lockKey = "lock:sku:"+item.getSkuId();
            //尝试获取锁
            Boolean locked = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, String.valueOf(userId), 5, TimeUnit.SECONDS);
            if (Boolean.FALSE.equals(locked)){
                throw new RuntimeException("系统繁忙，请稍后重试");
            }
            try {
                int stock = orderItemMapper.deductStock(item.getSkuId(), item.getQuantity());
                if (stock != 1) throw new RuntimeException("库存不足");
            }finally {
                //释放锁
                //redisTemplate.delete(lockKey);无法解决锁过期
                String script = "if redis.call('get',KEYS[1])==ARGV[1] then " +
                        "return redis.call('del',KEYS[1])" +
                        "else return 0 end";
                redisTemplate.execute(
                        new DefaultRedisScript<>(script,Long.class),
                        Collections.singletonList(lockKey),
                        String.valueOf(userId)
                );
            }
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for(OrderConfirmItemVO itemVO : orderConfirmItemVOList){totalAmount=totalAmount.add(itemVO.getSubtotal());}
        BigDecimal freight = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal payAmount = totalAmount.add(freight).subtract(discount);

        Address address = addressMapper.getByAddressId(orderRequest.getAddressId());
        String addressSnapshot = JSONUtil.toJsonStr(address);

        Order order = Order.builder().
                orderNo(orderNo).
                userId(userId).
                totalAmount(totalAmount).
                freight(freight).
                payAmount(payAmount).
                status(0).
                addressSnapshot(addressSnapshot).
                remark(orderRequest.getRemark()).
                build();
        orderMapper.createOrder(order);

        for(OrderConfirmItemVO itemVO : orderConfirmItemVOList){
            OrderItem oi = OrderItem.builder()
                    .orderId(order.getId())
                    .orderNo(orderNo)
                    .productId(itemVO.getProductId())
                    .skuId(itemVO.getSkuId())
                    .productName(itemVO.getProductName())
                    .skuSpec(itemVO.getSkuSpec())
                    .productImage(itemVO.getProductImage())
                    .price(itemVO.getPrice())
                    .quantity(itemVO.getQuantity())
                    .subtotal(itemVO.getSubtotal())
                    .build();
            orderItemMapper.insert(oi);
        }

        cartMapper.deleteChecked(userId);

        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(order.getId());
        BeanUtil.copyProperties(order,orderVO);

// 发布订单创建事件（异步解耦）
        applicationContext.publishEvent(new OrderCreatedEvent(orderNo, userId));

        return orderVO;
    }

    @Override
    public PageResultVO<OrderListVO> orderList(OrderPageRequest orderPageRequest) {
        int pageNum = orderPageRequest.getPageNum()!=null?orderPageRequest.getPageNum():1;
        int pageSize = orderPageRequest.getPageSize()!=null?orderPageRequest.getPageSize():10;
        PageHelper.startPage(pageNum,pageSize);
        List<OrderListVO> listOLVO = new ArrayList<>();

        Long userId = (Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Order> orderList = orderMapper.getOrderByUserIdAndStatus(userId,orderPageRequest.getStatus());
       for(Order order : orderList) {
           OrderListVO VO = new OrderListVO();
           VO.setOrderId(order.getId());
           VO.setOrderNo(order.getOrderNo());
           VO.setStatus(order.getStatus());
           VO.setStatusText(OrderStatusEnum.getText(order.getStatus()));
           VO.setTotalAmount(order.getTotalAmount());
           VO.setPayAmount(order.getPayAmount());

           JSONObject address = JSONUtil.parseObj(order.getAddressSnapshot());
           VO.setConsignee(address.getStr("consignee"));
           VO.setPhone(address.getStr("phone").substring(0, 3) + "****" + address.getStr("phone").substring(7));

           VO.setCreateTime(order.getCreateTime());

           listOLVO.add(VO);
       }

        /**
         * PageInfo 是 PageHelper 的信息类，包装了两个结果：
         * 里面包含的成员：
         *{
         * List<Order> list = orderMapper.list(userId, status);  // 查出来的当前页数据
         * PageInfo<Order> info = new PageInfo<>(list);
         *
         * info.getList();     // → 当前页的数据列表
         * info.getTotal();    // → 数据库总共多少条
         * info.getPages();    // → 总共多少页
         * info.getPageNum();  // → 当前第几页
         * info.getPageSize(); // → 每页多少条
         *}
         *  ！！Info获取total,需要传递刚从数据库拿到的类型
         * PageHelper.startPage() 截的是 MyBatis 的查询。它把 orderMapper.getOrderByUserIdAndStatus() 返回的 List 替换成 Page（Page 继承 ArrayList，额外带了 total 信息）。你用 new PageInfo<>(orderList) 才能拿到 total。
         * 你手写的 new ArrayList<OrderListVO>() 是普通 List，没有 total——PageInfo 只能拿到当前页的条数，拿不到数据库总条数。
         */
       PageInfo<Order> pageInfo = new PageInfo<>(orderList);

       PageResultVO<OrderListVO> result = new PageResultVO<>();
       result.setList(listOLVO);
       result.setTotal(pageInfo.getTotal());
       result.setPages(pageInfo.getPages());
       result.setPageNum(pageNum);
       result.setPageSize(pageSize);
        return result;
    }

    @Override
    public OrderDetailVO orderDetail(String orderNo) {
        Order order =  orderMapper.getByNo(orderNo);
        OrderConfirmAddressVO address = JSONUtil.toBean(order.getAddressSnapshot(), OrderConfirmAddressVO.class);
        List<OrderItem> items = orderItemMapper.getByNo(orderNo);
        List<OrderDetailItemVO> itemsVO = BeanUtil.copyToList(items, OrderDetailItemVO.class);

        OrderDetailVO detailVO = new OrderDetailVO();
        BeanUtil.copyProperties(order, detailVO);
        detailVO.setAddress(address);
        detailVO.setItems(itemsVO);
        detailVO.setOrderId(order.getId());
        detailVO.setStatusText(OrderStatusEnum.getText(order.getStatus()));
        return detailVO;
    }

    @Transactional
    @Override
    public void cancelOrder(String orderNo) {
        Order order =  orderMapper.getByNo(orderNo);
        if(order.getStatus().equals(0)){
            orderMapper.cancelOrder(orderNo);
            List<OrderItem> itemsList = orderItemMapper.getReturnItem(orderNo);
            for (OrderItem orderItem : itemsList) {
                productMapper.returnSku(orderItem);
            }
            //释放优惠券couponMapper......
        }
        else throw new RuntimeException("无法取消");
    }

    @Override
    public void receiveOrder(String orderNo) {
        Order order =  orderMapper.getByNo(orderNo);
        if(order.getStatus().equals(2)){
            orderMapper.receiveOrder(orderNo);
        }
        else throw new RuntimeException("无法收货");
    }
    /**
     * admin
     */
    @Override
    public PageResultVO<OrderListVO> adminOrderList(Integer pageNum, Integer pageSize, Integer status, String orderNo) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> list = orderMapper.adminList(status, orderNo);
        PageInfo<Order> info = new PageInfo<>(list);
        List<OrderListVO> voList = new ArrayList<>();
        for (Order order : list) {
            OrderListVO vo = new OrderListVO();
            vo.setOrderId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setStatus(order.getStatus());
            vo.setStatusText(OrderStatusEnum.getText(order.getStatus()));
            vo.setTotalAmount(order.getTotalAmount());
            vo.setPayAmount(order.getPayAmount());
            JSONObject addr = JSONUtil.parseObj(order.getAddressSnapshot());
            vo.setConsignee(addr.getStr("consignee"));
            String phone = addr.getStr("phone");
            vo.setPhone(phone != null ? phone.substring(0, 3) + "****" + phone.substring(7) : "");
            vo.setCreateTime(order.getCreateTime());
            voList.add(vo);
        }
        PageResultVO<OrderListVO> result = new PageResultVO<>();
        result.setList(voList);
        result.setTotal(info.getTotal());
        result.setPages(info.getPages());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override @Transactional
    public void shipOrder(String orderNo, String logisticsNo, String logisticsCompany) {
        Order order = orderMapper.getByNo(orderNo);
        if (order == null || order.getStatus() != 1) throw new RuntimeException("订单状态不支持发货");
        orderMapper.ship(orderNo);
    }
}
