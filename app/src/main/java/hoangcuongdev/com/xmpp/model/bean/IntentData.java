package hoangcuongdev.com.xmpp.model.bean;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class IntentData implements Serializable{
    private int type;
    private String chatname;
    private String nickname;

//    public int getType() {
//        return type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//
//    public String getChatname() {
//        return chatname;
//    }
//
//    public void setChatname(String chatname) {
//        this.chatname = chatname;
//    }
//
//    public String getNickname() {
//        return nickname;
//    }
//
//    public void setNickname(String nickname) {
//        this.nickname = nickname;
//    }
}
