package com.sinka.platform.constant;

public final class RabbitConstants {

    private RabbitConstants() {
    }

    // 延迟链路
    public static final String ORDER_DELAY_EXCHANGE = "order.delay.exchange";
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    public static final String ORDER_DELAY_ROUTING_KEY = "order.delay";

    // 到期后的关闭链路
    public static final String ORDER_CLOSE_EXCHANGE = "order.close.exchange";
    public static final String ORDER_CLOSE_QUEUE = "order.close.queue";
    public static final String ORDER_CLOSE_ROUTING_KEY = "order.close";

    // 15 分钟，单位毫秒
    public static final int ORDER_TIMEOUT_MILLIS = 15 * 60 * 1000;
    public static final String ORDER_REMIND_DELAY_QUEUE = "order.remind.delay.queue";
    public static final String ORDER_REMIND_DELAY_KEY = "order.remind.delay";
    public static final String ORDER_REMIND_EXCHANGE = "order.remind.exchange";
    public static final String ORDER_REMIND_QUEUE = "order.remind.queue";
    public static final String ORDER_REMIND_KEY = "order.remind";
    public static final int ORDER_REMIND_MILLIS = 10 * 60 * 1000;

    //失败链路
    public static final String ORDER_ERROR_EXCHANGE = "order.error.exchange";
    public static final String ORDER_ERROR_QUEUE = "order.error.queue";
    public static final String ORDER_ERROR_ROUTING_KEY = "order.error";
}
