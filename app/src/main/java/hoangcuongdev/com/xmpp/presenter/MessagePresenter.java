package hoangcuongdev.com.xmpp.presenter;

import android.content.Context;

import hoangcuongdev.com.xmpp.base.BasePresenter;
import hoangcuongdev.com.xmpp.model.dao.MsgDbHelper;
import hoangcuongdev.com.xmpp.ui.view.MessageView;
import hoangcuongdev.com.xmpp.utils.RxUtils;
import rx.Observable;

/**
 * Created by GreenLove on 2017
 */

public class MessagePresenter extends BasePresenter{
    private MessageView mView;

    public MessagePresenter(Context mContext,MessageView mView) {
        super( mContext );
        this.mView = mView;
    }

    public void getMessge(){
        Observable.just( MsgDbHelper.getInstance( mContext ).getLastMsg() )
                .compose( RxUtils.applySchedulers( mView ) )
                .subscribe( itemList -> {
                    mView.onNext( itemList );
                },throwable -> mView.onError( new Throwable( "Không thể nhận được danh sách tin nhắn" ) ) );
    }
}
