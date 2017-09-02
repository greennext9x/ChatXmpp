package hoangcuongdev.com.xmpp.model.bean;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MessageEvent {
    private String tag;
    private String message;

    public MessageEvent(String tag, String message) {
        this.tag = tag;
        this.message = message;
    }

//    public String getTag() {
//        return tag;
//    }
//
//    public String getMessage() {
//        return message;
//    }

}
