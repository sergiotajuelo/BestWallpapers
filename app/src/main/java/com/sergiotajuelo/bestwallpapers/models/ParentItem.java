package com.sergiotajuelo.bestwallpapers.models;

import java.util.List;

public class ParentItem {

    private String ParentItemTitle;
    private List<WallpaperModel> ChildItemList;

    public ParentItem(String parentItemTitle, List<WallpaperModel> childItemList) {
        ParentItemTitle = parentItemTitle;
        ChildItemList = childItemList;
    }

    public String getParentItemTitle() {
        return ParentItemTitle;
    }

    public void setParentItemTitle(String parentItemTitle) {
        ParentItemTitle = parentItemTitle;
    }

    public List<WallpaperModel> getChildItemList() {
        return ChildItemList;
    }

    public void setChildItemList(List<WallpaperModel> childItemList) {
        ChildItemList = childItemList;
    }
}
