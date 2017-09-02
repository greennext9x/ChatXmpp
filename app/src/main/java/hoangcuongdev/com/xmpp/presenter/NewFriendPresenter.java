package hoangcuongdev.com.xmpp.presenter;

import android.content.Context;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.List;

import hoangcuongdev.com.xmpp.base.BasePresenter;
import hoangcuongdev.com.xmpp.model.bean.Friend;
import hoangcuongdev.com.xmpp.model.dao.NewFriendDbHelper;
import hoangcuongdev.com.xmpp.scoket.XmppConnection;
import hoangcuongdev.com.xmpp.ui.view.NewFriendView;
import hoangcuongdev.com.xmpp.utils.RxUtils;
import rx.Observable;

/**
 * Created by GreenLove on 2017
 */

public class NewFriendPresenter extends BasePresenter{
    private NewFriendView mView;

    public NewFriendPresenter(Context mContext,NewFriendView mView) {
        super( mContext );
        this.mView = mView;
    }

    public void getNewFriend(){
        Observable.just( NewFriendDbHelper.getInstance(mContext).getNewFriend())
                .compose( RxUtils.applySchedulers( mView ) )
                .subscribe( strings -> {
                    getFriendlist( strings );
                } );
    }

    public void getFriendlist(List<String> mlist) {
        Observable.from( mlist )
                .map( s -> {
                    VCard vCard = XmppConnection.getInstance().getUserInfo( s );
                    Friend friend = new Friend();
                    String nickName = "";
                    String userHead = "";
                    nickName = vCard.getField( "nickName" );
                    userHead = vCard.getField( "avatar" );
                    if (nickName == null) {
                        nickName = s;
                    }
                    friend.setUsername( s );
                    friend.setNickname( nickName );
                    friend.setUserHead( userHead );
                    return friend;
                } )
                .toList()
                .subscribe( list -> mView.onNext(list) );
    }


    public void addFriend(String username){
        XmppConnection.getInstance().addUser( username )
                .compose( RxUtils.applySchedulers( mView ) )
                .subscribe( aBoolean -> {
                    mView.showTip( "Đã thêm thành công" );
                    mView.onAddNext();
                },throwable -> {
                    mView.showTip( throwable.getMessage() );
                } );
    }
}
