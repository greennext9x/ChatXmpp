package hoangcuongdev.com.xmpp.ui.view;

import java.util.List;

import hoangcuongdev.com.xmpp.base.IBaseView;
import hoangcuongdev.com.xmpp.model.bean.ChatItem;

/**
 * Created by GreenLove on 2017
 */

public interface ChatView extends IBaseView{
    void onNext(List<ChatItem> itemList);
    void onSendFail();
    void onRefreshNext(List<ChatItem> itemList);
}
