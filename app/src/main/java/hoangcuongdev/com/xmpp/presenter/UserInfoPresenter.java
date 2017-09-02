package hoangcuongdev.com.xmpp.presenter;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import hoangcuongdev.com.xmpp.base.BasePresenter;
import hoangcuongdev.com.xmpp.model.bean.MessageEvent;
import hoangcuongdev.com.xmpp.scoket.XmppConnection;
import hoangcuongdev.com.xmpp.ui.view.UserInfoView;
import hoangcuongdev.com.xmpp.utils.RxUtils;
import rx.Observable;

/**
 * Created by GreenLove on 2017
 */

public class UserInfoPresenter extends BasePresenter {
    private UserInfoView mView;

    public UserInfoPresenter(Context mContext, UserInfoView mView) {
        super( mContext );
        this.mView = mView;
    }

    public void getUserInfo() {
        Observable.create( (Observable.OnSubscribe<VCard>) subscriber -> {
            VCard vcard = XmppConnection.getInstance().getUserInfo( null );
            if (vcard != null) {
                subscriber.onNext( vcard );
                subscriber.onCompleted();
            } else {
                subscriber.onError( new Throwable( "Không thể nhận được" ) );
            }
        } ).compose( RxUtils.applySchedulers( mView ) )
                .subscribe( vCard -> mView.onNext( vCard )
                        , throwable -> mView.onError( throwable ) );
    }

    public void setPortrait(VCard mvacrd, String type, String msg) {
        Observable.create( (Observable.OnSubscribe<VCard>) subscriber -> {
            mvacrd.setField( type, msg );
            subscriber.onNext( mvacrd );
            subscriber.onCompleted();
        } ).flatMap( vCard -> XmppConnection.getInstance().changeVcard( vCard ) )
                .compose( RxUtils.applySchedulers( mView ) )
                .subscribe( aBoolean -> {
                    EventBus.getDefault().post( new MessageEvent( "changeVcard", "" ) );
                }, throwable -> mView.onError( throwable ) );
    }

}
