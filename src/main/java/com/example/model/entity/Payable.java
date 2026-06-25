package com.example.model.entity;

public interface Payable {
    boolean isPaid();
    void markAsPaid();
}