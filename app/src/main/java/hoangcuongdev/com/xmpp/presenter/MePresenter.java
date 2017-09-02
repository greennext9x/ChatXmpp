package hoangcuongdev.com.xmpp.presenter;

import android.content.Context;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import hoangcuongdev.com.xmpp.base.BasePresenter;
import hoangcuongdev.com.xmpp.base.MyApplication;
import hoangcuongdev.com.xmpp.model.bean.Constants;
import hoangcuongdev.com.xmpp.model.bean.Friend;
import hoangcuongdev.com.xmpp.scoket.XmppConnection;
import hoangcuongdev.com.xmpp.ui.view.MeView;
import hoangcuongdev.com.xmpp.utils.DataHelper;
import hoangcuongdev.com.xmpp.utils.RxUtils;
import rx.Observable;

/**
 * Created by GreenLove on 2017
 */

public class MePresenter extends BasePresenter {
    private MeView mView;

    public MePresenter(Context mContext, MeView meView) {
        super( mContext );
        this.mView = meView;
    }

    public void getUserInfo() {
        Friend userfriend = DataHelper.getDeviceData( mContext, Constants.USERINFO );
        if (userfriend != null)
            mView.onNext( userfriend );
        else
            mView.onError( new Throwable( "Không thể lấy thông tin cá nhân" ) );
    }

    public void getNowUserInfo() {
        Observable.create( (Observable.OnSubscribe<Friend>) subscriber -> {
            VCard vCard = XmppConnection.getInstance().getUserInfo( null );
            if (vCard != null) {
                Friend mfriend = new Friend();
                String username = Constants.USER_NAME;
                String nikename = vCard.getField( "nickName" );
                String userheard = vCard.getField( "avatar" );
                if (nikename == null) {
                    nikename = username;
                }
                mfriend.setUsername( username );
                mfriend.setUserHead( userheard );
                mfriend.setNickname( nikename );
                DataHelper.saveDeviceData( MyApplication.getInstance(), Constants.USERINFO, mfriend );
                subscriber.onNext( mfriend );
                subscriber.onCompleted();
            } else
                subscriber.onError( new Throwable( "Không thể tìm thông tin" ) );
        } ).compose( RxUtils.bindToSchedulers( mView ) )
                .subscribe( friend -> mView.onNext( friend ), throwable -> mView.onError( throwable ) );
    }
}
