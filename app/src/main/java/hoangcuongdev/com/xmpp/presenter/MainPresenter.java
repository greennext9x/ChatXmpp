package hoangcuongdev.com.xmpp.presenter;

import android.content.Context;

import hoangcuongdev.com.xmpp.base.BasePresenter;
import hoangcuongdev.com.xmpp.model.bean.Constants;
import hoangcuongdev.com.xmpp.scoket.XmppConnection;
import hoangcuongdev.com.xmpp.ui.view.MainView;
import hoangcuongdev.com.xmpp.utils.DataHelper;
import hoangcuongdev.com.xmpp.utils.RxUtils;

/**
 * Created by GreenLove on 2017
 */

public class MainPresenter extends BasePresenter {
    private MainView mView;

    public MainPresenter(Context mContext, MainView view) {
        super( mContext );
        this.mView = view;
    }

    public void toLogin() {
        String name = DataHelper.getStringSF( mContext, Constants.LOGIN_ACCOUNT );
        String password = DataHelper.getStringSF( mContext, Constants.LOGIN_PWD );
        XmppConnection.getInstance().login( name, password )
                .compose( RxUtils.applySchedulers( mView ) )
                .subscribe( list -> {
                    DataHelper.saveDeviceData( mContext, name, list );
                }, throwable -> mView.showTip( throwable.getMessage() ) );
    }
}
