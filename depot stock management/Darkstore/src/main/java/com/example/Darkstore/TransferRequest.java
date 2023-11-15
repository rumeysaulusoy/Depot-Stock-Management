package com.example.Darkstore;


public class TransferRequest {
    private Long sourceDepotId;
    private Long destinationDepotId;
    private Long productId;
    private int quantity;

    public Long getSourceDepotId() {
        return sourceDepotId;
    }

    public void setSourceDepotId(Long sourceDepotId) {
        this.sourceDepotId = sourceDepotId;
    }

    public Long getDestinationDepotId() {
        return destinationDepotId;
    }

    public void setDestinationDepotId(Long destinationDepotId) {
        this.destinationDepotId = destinationDepotId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
