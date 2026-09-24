package com.distributedorderplatform.order_service.entity;

public enum OrderStatus {

    CREATED,
    STOCK_PENDING,
    STOCK_RESERVED,
    STOCK_REJECTED,
    CONFIRMED,
    CANCELLED
}