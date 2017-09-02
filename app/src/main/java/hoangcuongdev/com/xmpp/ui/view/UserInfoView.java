package hoangcuongdev.com.xmpp.ui.view;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import hoangcuongdev.com.xmpp.base.IBaseView;

/**
 * Created by GreenLove on 2017
 */

public interface UserInfoView extends IBaseView{
    void onNext(VCard vCard);
}
