package com.example.admin.vkclub;


/**
 * Created by admin on 7/23/2017.
 */

public class DataObject {
    private String Title;
    private String Content;
    private int Image;

    DataObject (int image, String title, String content){
        Title = title;
        Content = content;
        Image = image;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        this.Content = content;
    }

    public int getImage(){
        return Image;
    }

    public void setImage(int image){
        this.Image = image;
    }
}
