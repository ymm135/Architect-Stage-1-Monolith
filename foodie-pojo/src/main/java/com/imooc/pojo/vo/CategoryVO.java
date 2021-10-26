package com.imooc.pojo.vo;

import com.imooc.pojo.Category;

import java.security.PrivateKey;
import java.util.List;

public class CategoryVO {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFatherId() {
        return fatherId;
    }

    public void setFatherId(int fatherId) {
        this.fatherId = fatherId;
    }

    public List<SubCategoryVO> getSubCatList() {
        return subCatList;
    }

    public void setSubCatList(List<SubCategoryVO> subCatList) {
        this.subCatList = subCatList;
    }

    private String name;
    private int id;
    private int type;
    private int fatherId;
    private List<SubCategoryVO> subCatList;
}
