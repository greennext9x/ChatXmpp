package hoangcuongdev.com.xmpp.presenter;

import android.content.Context;

import hoangcuongdev.com.xmpp.base.BasePresenter;
import hoangcuongdev.com.xmpp.model.bean.Constants;
import hoangcuongdev.com.xmpp.scoket.XmppConnection;
import hoangcuongdev.com.xmpp.ui.view.LoginView;
import hoangcuongdev.com.xmpp.utils.DataHelper;
import hoangcuongdev.com.xmpp.utils.RxUtils;

/**
 * Created by GreenLove on 2017
 */

public class LoginPresenter extends BasePresenter {
    private LoginView mView;

    public LoginPresenter(Context mContext, LoginView view) {
        super( mContext );
        this.mView = view;
    }

    public void toLogin(String name, String password) {
        XmppConnection.getInstance().login( name, password )
                .compose( RxUtils.applySchedulers( mView ) )
                .subscribe( list -> {
                            DataHelper.saveDeviceData( mContext, name, list );
                            DataHelper.SetBooleanSF( mContext, Constants.LOGIN_CHECK, true );
                            DataHelper.SetStringSF( mContext, Constants.LOGIN_ACCOUNT, name );
                            DataHelper.SetStringSF( mContext, Constants.LOGIN_PWD, password );
                            mView.onNext();
                        }, throwable ->
                                mView.showTip( throwable.getMessage() )
                );
    }
}
