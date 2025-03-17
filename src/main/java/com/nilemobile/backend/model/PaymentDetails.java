package com.nilemobile.backend.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class PaymentDetails {
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String paymentId;
    private String VNPAYPaymentUrl;
    private String VNPAYTransactionRef;
    private String VNPAYTransactionNo;

    public PaymentDetails() {
        this.status = PaymentStatus.PENDING; // Gán giá trị mặc định
        this.paymentMethod = null;
    }

    public PaymentDetails(PaymentMethod paymentMethod, PaymentStatus status, String paymentId, String VNPAYPaymentUrl, String VNPAYTransactionRef, String VNPAYTransactionNo) {
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paymentId = paymentId;
        this.VNPAYPaymentUrl = VNPAYPaymentUrl;
        this.VNPAYTransactionRef = VNPAYTransactionRef;
        this.VNPAYTransactionNo = VNPAYTransactionNo;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getVNPAYPaymentUrl() {
        return VNPAYPaymentUrl;
    }

    public void setVNPAYPaymentUrl(String VNPAYPaymentUrl) {
        this.VNPAYPaymentUrl = VNPAYPaymentUrl;
    }

    public String getVNPAYTransactionRef() {
        return VNPAYTransactionRef;
    }

    public void setVNPAYTransactionRef(String VNPAYTransactionRef) {
        this.VNPAYTransactionRef = VNPAYTransactionRef;
    }

    public String getVNPAYTransactionNo() {
        return VNPAYTransactionNo;
    }

    public void setVNPAYTransactionNo(String VNPAYTransactionNo) {
        this.VNPAYTransactionNo = VNPAYTransactionNo;
    }
}
