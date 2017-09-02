package hoangcuongdev.com.xmpp.presenter;

import android.content.Context;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import hoangcuongdev.com.xmpp.base.BasePresenter;
import hoangcuongdev.com.xmpp.scoket.XmppConnection;
import hoangcuongdev.com.xmpp.ui.view.FriendView;
import hoangcuongdev.com.xmpp.utils.RxUtils;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by GreenLove on 2017
 */

public class FriendPresenter extends BasePresenter {
    private FriendView mView;

    public FriendPresenter(Context mContext, FriendView mView) {
        super( mContext );
        this.mView = mView;
    }

    public void getFriend(String username) {
        Observable.create( new Observable.OnSubscribe<VCard>() {
            @Override
            public void call(Subscriber<? super VCard> subscriber) {
                VCard vCard = XmppConnection.getInstance().getUserInfo( username );
                if (vCard != null) {
                    subscriber.onNext( vCard );
                    subscriber.onCompleted();
                } else {
                    subscriber.onError( new Throwable( "Nhận thông tin bạn bè không thành công" ) );
                }
            }
        } ).compose( RxUtils.bindToSchedulers( mView ) )
                .subscribe( vCard -> mView.onNext( vCard )
                        , throwable -> mView.onError( throwable ) );
    }

    public void addFriend(String username) {
        XmppConnection.getInstance().addUser( username )
                .compose( RxUtils.applySchedulers( mView ) )
                .subscribe( aBoolean -> {
                    mView.showTip( "Đã thêm thành công" );
                    mView.onAddNext();
                }, throwable -> mView.showTip( throwable.getMessage() ) );
    }

    public void deleteFriend(String username) {
        XmppConnection.getInstance().removeUser( username )
                .compose( RxUtils.applySchedulers( mView ) )
                .subscribe( aBoolean -> {
                    mView.showTip( "Xóa thành công" );
                    mView.onDeleteNext();
                }, throwable -> {
                    mView.showTip( "Xóa không thành công" );
                } );
    }
}
