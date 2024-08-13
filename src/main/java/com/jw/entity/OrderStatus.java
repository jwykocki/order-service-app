package com.jw.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderStatus {
    UNCOMPLETED("UNCOMPLETED"),
    RESERVED("RESERVED"),
    COMPLETED("COMPLETED");
    private final String status;
}
