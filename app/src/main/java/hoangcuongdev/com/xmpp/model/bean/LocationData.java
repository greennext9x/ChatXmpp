package hoangcuongdev.com.xmpp.model.bean;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LocationData implements Serializable {
    private String lat;
    private String lng;
    private String poi;
    private String imgUrl;


//    public String getLat() {
//        return lat;
//    }
//
//    public void setLat(String lat) {
//        this.lat = lat;
//    }
//
//    public String getLng() {
//        return lng;
//    }
//
//    public void setLng(String lng) {
//        this.lng = lng;
//    }
//
//    public String getPoi() {
//        return poi;
//    }
//
//    public void setPoi(String poi) {
//        this.poi = poi;
//    }
//
//    public String getImgUrl() {
//        return imgUrl;
//    }
//
//    public void setImgUrl(String imgUrl) {
//        this.imgUrl = imgUrl;
//    }

    @Override
    public String toString() {
        return "{" +
                "'lat':'" + lat + '\'' +
                ", 'lng':'" + lng + '\'' +
                ", 'poi':'" + poi + '\'' +
                ", 'imgUrl':'" + imgUrl + '\'' +
                '}';
    }
}

