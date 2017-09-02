package hoangcuongdev.com.xmpp.model.bean;

import com.chad.library.adapter.base.entity.SectionEntity;



public class MyContacts extends SectionEntity<Friend>{

    public MyContacts(boolean isHeader, String header) {
        super( isHeader, header );
    }

    public MyContacts(Friend friend) {
        super( friend );
    }

}
