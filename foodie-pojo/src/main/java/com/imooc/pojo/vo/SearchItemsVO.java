package com.imooc.pojo.vo;

/**
 *  "itemId":"nut-104",
 *  "itemName":"进口坚果大杂烩 好吃又回味",
 *  "sellCounts":5252,
 *  "price":5760,
 *  "imgUrl":"http://122.152.205.72:88/foodie/nut-104/img1.png"
 */
public class SearchItemsVO {
    private String itemId;
    private String itemName;
    private String imgUrl;
    private int price;
    private int sellCounts;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSellCounts() {
        return sellCounts;
    }

    public void setSellCounts(int sellCounts) {
        this.sellCounts = sellCounts;
    }
}
