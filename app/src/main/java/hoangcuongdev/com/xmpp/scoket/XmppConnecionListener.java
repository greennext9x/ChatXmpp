package hoangcuongdev.com.xmpp.scoket;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import java.util.Timer;
import java.util.TimerTask;

import hoangcuongdev.com.xmpp.base.MyApplication;
import hoangcuongdev.com.xmpp.model.bean.Constants;
import hoangcuongdev.com.xmpp.utils.DataHelper;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by GreenLove on 2017
 */

public class XmppConnecionListener implements ConnectionListener {
    private Timer tExit;
    private String username;
    private String password;
    private int logintime = 5000;

    @Override
    public void connected(XMPPConnection connection) {

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean b) {

    }

    @Override
    public void connectionClosed() {
        // 重连服务器
        tExit = new Timer();
        tExit.schedule(new timetask(), logintime);
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        if (e.getMessage().contains("conflict")) {
        } else {
            tExit = new Timer();
            tExit.schedule(new timetask(), logintime);
        }
    }

    class timetask extends TimerTask {
        @Override
        public void run() {
            XmppConnection.getInstance().closeConnection();
            username = DataHelper.getStringSF( MyApplication.getInstance().getContext(), Constants.LOGIN_ACCOUNT );
            password = DataHelper.getStringSF( MyApplication.getInstance().getContext(), Constants.LOGIN_PWD );
            XmppConnection.getInstance().login( username,password )
                    .subscribeOn( Schedulers.io() )
                    .observeOn( AndroidSchedulers.mainThread() )
                    .subscribe( list -> {

                    },throwable -> tExit.schedule(new timetask(), logintime));
        }
    }

    @Override
    public void reconnectionSuccessful() {

    }

    @Override
    public void reconnectingIn(int i) {

    }

    @Override
    public void reconnectionFailed(Exception e) {

    }
}
