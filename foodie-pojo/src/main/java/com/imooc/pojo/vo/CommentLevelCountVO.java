package com.imooc.pojo.vo;

/**
 * 商品评价分类
 * {"totalCounts":1,"goodCounts":0,"normalCounts":1,"badCounts":0}
 */
public class CommentLevelCountVO {

    public int getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(int totalCounts) {
        this.totalCounts = totalCounts;
    }

    public int getGoodCounts() {
        return goodCounts;
    }

    public void setGoodCounts(int goodCounts) {
        this.goodCounts = goodCounts;
    }

    public int getNormalCounts() {
        return normalCounts;
    }

    public void setNormalCounts(int normalCounts) {
        this.normalCounts = normalCounts;
    }

    public int getBadCounts() {
        return badCounts;
    }

    public void setBadCounts(int badCounts) {
        this.badCounts = badCounts;
    }

    private int totalCounts;
    private int goodCounts;
    private int normalCounts;
    private int badCounts;
}
