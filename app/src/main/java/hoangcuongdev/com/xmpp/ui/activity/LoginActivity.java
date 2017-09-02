package hoangcuongdev.com.xmpp.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.qiushui.blurredview.BlurredView;

import butterknife.Bind;
import butterknife.OnClick;
import hoangcuongdev.com.xmpp.R;
import hoangcuongdev.com.xmpp.base.BaseActivity;
import hoangcuongdev.com.xmpp.presenter.LoginPresenter;
import hoangcuongdev.com.xmpp.ui.view.LoginView;
import hoangcuongdev.com.xmpp.wight.login.AndroidBug5497Workaround;
import hoangcuongdev.com.xmpp.wight.statusbar.StatusBarUtil;

import static hoangcuongdev.com.xmpp.R.id.btn_login;
import static hoangcuongdev.com.xmpp.R.id.clean_password;
import static hoangcuongdev.com.xmpp.R.id.iv_clean_phone;
import static hoangcuongdev.com.xmpp.R.id.iv_show_pwd;

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginView {

    @Bind(R.id.logo)
    ImageView mLogo;
    @Bind(R.id.et_name)
    EditText mEtName;
    @Bind(R.id.il_name)
    TextInputLayout mIlName;
    @Bind(R.id.il_passsword)
    TextInputLayout mIlPasssword;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
    @Bind(R.id.content)
    LinearLayout mContent;
    @Bind(R.id.iv_clean_phone)
    ImageView mIvCleanPhone;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.clean_password)
    ImageView mCleanPassword;
    @Bind(R.id.iv_show_pwd)
    ImageView mIvShowPwd;
    @Bind(R.id.root)
    RelativeLayout mRoot;
    @Bind(R.id.bd_back)
    BlurredView mBdBack;
    @Bind(R.id.btn_login)
    Button mBtnLogin;
    @Bind(R.id.tVregister)
    TextView mTVregister;


    private int screenHeight = 0;
    private int keyHeight = 0;
    private float scale = 0.6f;

    @Override
    protected void initView() {
        setContentView( R.layout.activity_login );
    }

    @Override
    protected void initPresenter() {
        mPresenter = new LoginPresenter( mContext, this );
    }

    @Override
    protected void init() {
        if (isFullScreen( this )) {
            AndroidBug5497Workaround.assistActivity( this );
        }
        mBdBack.setBlurredLevel( 100 );
        mBdBack.showBlurredView();
        StatusBarUtil.setTranslucent( this );
        screenHeight = this.getResources().getDisplayMetrics().heightPixels;
        keyHeight = screenHeight / 3;
        mIlName.setHint( getString( R.string.hint_login_username ) );
        mIlPasssword.setHint( getString( R.string.hint_login_password ) );
        initListener();
    }

    @OnClick({iv_clean_phone, clean_password, iv_show_pwd, btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case iv_clean_phone:
                mEtName.setText( "" );
                break;
            case clean_password:
                mEtPassword.setText( "" );
                break;
            case iv_show_pwd:
                if (mEtPassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    mEtPassword.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                    mIvShowPwd.setImageResource( R.mipmap.pass_visuable );
                } else {
                    mEtPassword.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
                    mIvShowPwd.setImageResource( R.mipmap.pass_gone );
                }
                String pwd = mEtPassword.getText().toString();
                if (!TextUtils.isEmpty( pwd ))
                    mEtPassword.setSelection( pwd.length() );
                break;
            case btn_login:
                if (TextUtils.isEmpty( mEtName.getText() ))
                    showTip( getString( R.string.fail_login_username ) );
                if (TextUtils.isEmpty( mEtPassword.getText() ))
                    showTip( getString( R.string.fail_login_password ) );
                else {
                    String name = mEtName.getText().toString();
                    String password = mEtPassword.getText().toString();
                    mPresenter.toLogin( name, password );
                }
                break;
        }
    }


    @Override
    public void onNext() {
        showTip( getString( R.string.success_login ) );
        startActivity( new Intent( this, MainActivity.class ) );
        finish();
    }

    private void initListener(){
        RxView.clicks( mTVregister )
                .subscribe( aVoid -> startActivity( new Intent( LoginActivity.this,RegisterActivity.class ) ) );
        mEtName.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty( s ) && mIvCleanPhone.getVisibility() == View.GONE) {
                    mIvCleanPhone.setVisibility( View.VISIBLE );
                } else if (TextUtils.isEmpty( s )) {
                    mIvCleanPhone.setVisibility( View.GONE );
                }
            }
        } );
        mEtPassword.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty( s ) && mCleanPassword.getVisibility() == View.GONE) {
                    mCleanPassword.setVisibility( View.VISIBLE );
                } else if (TextUtils.isEmpty( s )) {
                    mCleanPassword.setVisibility( View.GONE );
                }
                if (s.toString().isEmpty())
                    return;
                if (!s.toString().matches( "[A-Za-z0-9]+" )) {
                    String temp = s.toString();
                    Toast.makeText( LoginActivity.this, R.string.please_input_limit_pwd, Toast.LENGTH_SHORT ).show();
                    s.delete( temp.length() - 1, temp.length() );
                    mEtPassword.setSelection( s.length() );
                }
            }
        } );

        mScrollView.setOnTouchListener( (v, event) -> true );
        mScrollView.addOnLayoutChangeListener( (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {

            if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
                if ((mBtnLogin.getTop() - (oldBottom - bottom)) > 0) {
                    int dist = mBtnLogin.getTop() - (oldBottom - bottom);
                    ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat( mContent, "translationY", 0.0f, -dist );
                    mAnimatorTranslateY.setDuration( 300 );
                    mAnimatorTranslateY.setInterpolator( new LinearInterpolator() );
                    mAnimatorTranslateY.start();
                }
                zoomIn( mLogo, (oldBottom - bottom) - keyHeight );
            } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
                Log.e( "wenzhihao", "down------>" + (bottom - oldBottom) );
                ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat( mContent, "translationY", mContent.getTranslationY(), 0 );
                mAnimatorTranslateY.setDuration( 300 );
                mAnimatorTranslateY.setInterpolator( new LinearInterpolator() );
                mAnimatorTranslateY.start();
                zoomOut( mLogo, (bottom - oldBottom) - keyHeight );
            }
        } );
    }

    /**
     *
     * @param view
     */
    public void zoomIn(final View view, float dist) {
        view.setPivotY( view.getHeight() );
        view.setPivotX( view.getWidth() / 2 );
        AnimatorSet mAnimatorSet = new AnimatorSet();
        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat( view, "scaleX", 1.0f, scale );
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat( view, "scaleY", 1.0f, scale );
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat( view, "translationY", 0.0f, -dist );

        mAnimatorSet.play( mAnimatorTranslateY ).with( mAnimatorScaleX );
        mAnimatorSet.play( mAnimatorScaleX ).with( mAnimatorScaleY );
        mAnimatorSet.setDuration( 300 );
        mAnimatorSet.start();
    }

    /**
     *
     * @param view
     */
    public void zoomOut(final View view, float dist) {
        view.setPivotY( view.getHeight() );
        view.setPivotX( view.getWidth() / 2 );
        AnimatorSet mAnimatorSet = new AnimatorSet();

        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat( view, "scaleX", scale, 1.0f );
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat( view, "scaleY", scale, 1.0f );
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat( view, "translationY", view.getTranslationY(), 0 );

        mAnimatorSet.play( mAnimatorTranslateY ).with( mAnimatorScaleX );
        mAnimatorSet.play( mAnimatorScaleX ).with( mAnimatorScaleY );
        mAnimatorSet.setDuration( 300 );
        mAnimatorSet.start();
    }

    public boolean isFullScreen(Activity activity) {
        return (activity.getWindow().getAttributes().flags &
                WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }
}
