package hoangcuongdev.com.xmpp.ui.view;

import java.util.List;

import hoangcuongdev.com.xmpp.base.IBaseView;
import hoangcuongdev.com.xmpp.model.bean.Friend;

/**
 * Created by GreenLove on 2017
 */

public interface ContactsView extends IBaseView{
    void onNext(List<Friend> friendList);
}
