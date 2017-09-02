package hoangcuongdev.com.xmpp.presenter;

import android.content.Context;

import hoangcuongdev.com.xmpp.base.BasePresenter;
import hoangcuongdev.com.xmpp.scoket.XmppConnection;
import hoangcuongdev.com.xmpp.ui.view.RegisterView;
import hoangcuongdev.com.xmpp.utils.RxUtils;

/**
 * Created by GreenLove on 2017
 */

public class RegisterPresenter extends BasePresenter{
    private RegisterView mView;

    public RegisterPresenter(Context mContext,RegisterView mView) {
        super( mContext );
        this.mView = mView;
    }

    public void toRegister(String name, String password){
        XmppConnection.getInstance().regist( name, password )
                .compose( RxUtils.applySchedulers( mView ) )
                .subscribe( aBoolean -> mView.onNext(),throwable -> mView.onError( throwable ) );
    }
}
