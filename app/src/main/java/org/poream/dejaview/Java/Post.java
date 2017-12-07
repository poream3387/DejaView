package org.poream.dejaview.Java;

/**
 * Created by 이승호 on 2017-11-14.
 */

public class Post {
    private String image, title, content, username, Uid;



    public Post() {

    }


    public Post(String image, String title, String content, String username, String Uid) {
        this.image = image;
        this.title = title;
        this.content = content;
        this.username = username;
        this.Uid = Uid;
    }

    public String getImage() {return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return Uid;
    }
    public void setUid(String uid) {
        Uid = uid;
    }

}
