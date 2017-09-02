package hoangcuongdev.com.xmpp.ui.activity;

import com.bm.library.PhotoView;

import butterknife.Bind;
import hoangcuongdev.com.xmpp.R;
import hoangcuongdev.com.xmpp.base.BaseActivity;
import hoangcuongdev.com.xmpp.utils.ImageUtil;
import hoangcuongdev.com.xmpp.utils.RxUtils;
import hoangcuongdev.com.xmpp.wight.statusbar.StatusBarUtil;
import rx.Observable;

public class ShowBigImgActivity extends BaseActivity {
    @Bind(R.id.pv)
    PhotoView mPv;

    @Override
    protected void initView() {
        setContentView( R.layout.activity_show_big_img );
    }

    @Override
    protected void initPresenter() {

    }

    @Override
    protected void init() {
        StatusBarUtil.setTransparent(this);
        mPv.enable();
        Observable.just( ImageUtil.getBitmapFromBase64String( getIntent().getStringExtra( "showbigimg" ) ) )
                .compose( RxUtils.applySchedulers( this ) )
                .subscribe( bitmap -> {
                    if (bitmap != null)
                        mPv.setImageBitmap( bitmap );
                    else
                        mPv.setImageResource( R.mipmap.default_tp );
                } );
    }
}
