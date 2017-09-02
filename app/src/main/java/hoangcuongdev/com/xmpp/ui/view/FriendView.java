package hoangcuongdev.com.xmpp.ui.view;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import hoangcuongdev.com.xmpp.base.IBaseView;

/**
 * Created by GreenLove on 2017
 */

public interface FriendView extends IBaseView {
    void onNext(VCard vCard);
    void onAddNext();
    void onDeleteNext();
}
