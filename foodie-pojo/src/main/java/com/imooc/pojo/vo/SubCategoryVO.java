package com.imooc.pojo.vo;

public class SubCategoryVO {
    private String subName;
    private int subId;

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public int getSubId() {
        return subId;
    }

    public void setSubId(int subId) {
        this.subId = subId;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    public int getSubFatherId() {
        return subFatherId;
    }

    public void setSubFatherId(int subFatherId) {
        this.subFatherId = subFatherId;
    }

    private int subType;
    private int subFatherId;
}
