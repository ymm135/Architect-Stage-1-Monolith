package com.imooc.pojo.vo;

import java.util.List;

public class CategoryItemsVO {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRootCatName() {
        return rootCatName;
    }

    public void setRootCatName(String rootCatName) {
        this.rootCatName = rootCatName;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getCatImage() {
        return catImage;
    }

    public void setCatImage(String catImage) {
        this.catImage = catImage;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public List<ItemsVO> getSimpleItemList() {
        return simpleItemList;
    }

    public void setSimpleItemList(List<ItemsVO> simpleItemList) {
        this.simpleItemList = simpleItemList;
    }

    private String rootCatName;
    private String slogan;
    private String catImage;
    private String bgColor;
    private int id;

    List<ItemsVO> simpleItemList;
}
