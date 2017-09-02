package hoangcuongdev.com.xmpp.model.bean;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class SoundData implements Serializable{
    private int duration;
    private String pathname;
    private String msg;

//    public int getDuration() {
//        return duration;
//    }
//
//    public void setDuration(int duration) {
//        this.duration = duration;
//    }
//
//    public String getPathname() {
//        return pathname;
//    }
//
//    public void setPathname(String pathname) {
//        this.pathname = pathname;
//    }
//
//    public String getMsg() {
//        return msg;
//    }
//
//    public void setMsg(String msg) {
//        this.msg = msg;
//    }

    @Override
    public String toString() {
        return "{" +
                "'duration':'" + duration +'\'' +
                ", 'pathname':'" + pathname + '\'' +
                ", 'msg':'" + msg + '\'' +
                '}';
    }
}
