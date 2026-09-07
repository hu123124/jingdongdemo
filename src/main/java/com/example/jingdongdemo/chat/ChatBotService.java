package com.example.jingdongdemo.chat;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 智能客服 - 关键词规则自动回复。
 * answer 返回 ChatAnswer：content=回复话术；needHuman=true 表示该问题机器人答不了、
 * 或用户明确要求人工，需要转接人工客服。
 */
@Service
public class ChatBotService {

    /** 一条规则：命中任一关键词 → 返回该回答；列表越靠前优先级越高 */
    private record Rule(List<String> keywords, String answer, boolean needHuman) {}

    /** 机器人回答结果 */
    public record ChatAnswer(String content, boolean needHuman) {}

    private static final List<Rule> RULES = List.of(
            new Rule(List.of("物流", "快递", "发货", "到哪", "多久到"),
                    "您的订单一般在付款后 24 小时内发货，省内 1-2 天、省外 2-4 天送达，可在「我的订单」查看物流单号实时跟踪。", false),
            new Rule(List.of("退货", "退款", "换货", "退换"),
                    "支持 7 天无理由退货：我的订单 → 申请售后 → 审核通过寄回，退款 1-3 个工作日原路退回。", false),
            new Rule(List.of("优惠券", "优惠", "折扣", "活动", "满减"),
                    "首页和领券中心可领取优惠券，下单时自动匹配最优满减；大促期间还有限时折扣，留意活动页哦。", false),
            new Rule(List.of("密码", "忘记密码", "登录", "账号"),
                    "登录页点「忘记密码」，按短信验证码即可重置；账号异常可转人工客服处理。", false),
            new Rule(List.of("人工", "转人工", "客服", "投诉"),
                    "好的，已为您转接人工客服，正在为您排队…", true)   // ← 这条命中 = 要转人工
    );

    private static final String DEFAULT_ANSWER =
            "抱歉，我还没学会回答这个问题~ 您可以试试问我：物流 / 退货 / 优惠券 / 密码相关问题，或输入「转人工」联系人工客服。";

    public ChatAnswer answer(String content) {
        if (content == null || content.trim().isEmpty()) {
            return new ChatAnswer(DEFAULT_ANSWER, false);
        }
        String text = content.trim();
        for (Rule rule : RULES) {
            for (String kw : rule.keywords()) {
                if (text.contains(kw)) {
                    return new ChatAnswer(rule.answer(), rule.needHuman());
                }
            }
        }
        return new ChatAnswer(DEFAULT_ANSWER, false);
    }
}