package hoangcuongdev.com.xmpp.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnClick;
import hoangcuongdev.com.xmpp.R;
import hoangcuongdev.com.xmpp.base.BaseFragment;
import hoangcuongdev.com.xmpp.model.bean.Constants;
import hoangcuongdev.com.xmpp.model.bean.Friend;
import hoangcuongdev.com.xmpp.model.bean.MessageEvent;
import hoangcuongdev.com.xmpp.presenter.MePresenter;
import hoangcuongdev.com.xmpp.scoket.XmppConnection;
import hoangcuongdev.com.xmpp.ui.activity.LoginActivity;
import hoangcuongdev.com.xmpp.ui.activity.UserInfoActivity;
import hoangcuongdev.com.xmpp.ui.view.MeView;
import hoangcuongdev.com.xmpp.utils.DataHelper;
import hoangcuongdev.com.xmpp.utils.ImageUtil;

/**
 * Created by GreenLove on 2017
 */

public class MeFragment extends BaseFragment<MePresenter> implements MeView {
    @Bind(R.id.ivHeader)
    ImageView mIvHeader;
    @Bind(R.id.tvName)
    TextView mTvName;
    @Bind(R.id.tvAccount)
    TextView mTvAccount;
    @Bind(R.id.llMyInfo)
    LinearLayout mLlMyInfo;

    @Override
    public boolean isEventBus() {
        return true;
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate( R.layout.fragment_me, null );
    }

    @Override
    protected void initPresenter() {
        mPresenter = new MePresenter( mContext, this );
    }

    @Override
    protected void init() {
        initListener();
        mPresenter.getUserInfo();
    }

    private void initListener() {
        RxView.clicks( mLlMyInfo )
                .throttleFirst( 1, TimeUnit.SECONDS )
                .subscribe( aVoid -> startActivity( new Intent( mContext,UserInfoActivity.class) ) );
    }

    @OnClick(R.id.oivout)
    public void onClick() {
        DataHelper.SetBooleanSF( mContext, Constants.LOGIN_CHECK,false );
        XmppConnection.getInstance().closeConnection();
        startActivity( new Intent( mContext, LoginActivity.class ) );
        getActivity().finish();
    }

    @Override
    public void onNext(Friend userfriend) {
        String userhead = userfriend.getUserHead();
        Bitmap headimg = ImageUtil.getBitmapFromBase64String( userhead );
        String nickName = userfriend.getNickname();
        String username = userfriend.getUsername();
        if (headimg != null)
            mIvHeader.setImageBitmap( headimg );
        mTvName.setText( nickName );
        mTvAccount.setText( "Hang talk number:" + username );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changefriend(MessageEvent event){
        if (event.getTag().equals( "changeVcard" ))
            mPresenter.getNowUserInfo();
    }
}
