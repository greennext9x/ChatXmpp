package hoangcuongdev.com.xmpp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.dialog.widget.NormalListDialog;
import com.jakewharton.rxbinding.view.RxView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import immortalz.me.library.TransitionsHeleper;
import hoangcuongdev.com.xmpp.R;
import hoangcuongdev.com.xmpp.base.BaseActivity;
import hoangcuongdev.com.xmpp.model.bean.Friend;
import hoangcuongdev.com.xmpp.model.bean.MessageEvent;
import hoangcuongdev.com.xmpp.model.dao.NewFriendDbHelper;
import hoangcuongdev.com.xmpp.model.dao.NewMsgDbHelper;
import hoangcuongdev.com.xmpp.presenter.NewFriendPresenter;
import hoangcuongdev.com.xmpp.ui.adapter.NewFriendAdapter;
import hoangcuongdev.com.xmpp.ui.view.NewFriendView;

public class NewFriendActivity extends BaseActivity<NewFriendPresenter> implements NewFriendView{
    @Bind(R.id.im_back)
    ImageView mImBack;
    @Bind(R.id.tv_right)
    TextView mTvRight;
    @Bind(R.id.etContent)
    EditText mEtContent;
    @Bind(R.id.rvNewfriend)
    RecyclerView mRvNewfriend;

    private List<Friend> mData = new ArrayList<>(  );
    private NewFriendAdapter mAdapter;
    private View emtyView;
    private int position = 0;
    private NormalListDialog dialog;

    @Override
    protected void initView() {
        setContentView( R.layout.activity_new_friend );
    }

    @Override
    protected void initPresenter() {
        mPresenter = new NewFriendPresenter( this,this );
    }

    @Override
    protected void init() {
        initToobar();
        initDialog();
        initEmtyView();
        initAdapter();
        initListener();
        mPresenter.getNewFriend();
    }

    private void initDialog() {
        dialog = new NormalListDialog( mContext,new String[]{"Xóa tin nhắn"} );
        dialog.isTitleShow( false );
        dialog.setOnOperItemClickL( (parent, view, position, id) -> {
            NewFriendDbHelper.getInstance(mContext).delFriend( mData.get( this.position ).getUsername() );
            dialog.dismiss();
            mPresenter.getNewFriend();
        } );
    }

    private void initEmtyView() {
        emtyView = getLayoutInflater().inflate( R.layout.item_emtyview,(ViewGroup)mRvNewfriend.getParent(),false );
        TextView emtyTv = (TextView) emtyView.findViewById( R.id.tv_emty );
        emtyTv.setText( "Không có bạn bè xin thông tin" );
    }

    private void initListener() {
        RxView.clicks( mTvRight )
                .throttleFirst( 1,TimeUnit.SECONDS )
                .subscribe( aVoid -> {
                    NewFriendDbHelper.getInstance(getApplicationContext()).clear();
                    mAdapter.setNewData( null );
                    mAdapter.setEmptyView( emtyView );
                } );
        RxView.clicks( mEtContent )
                .throttleFirst( 1, TimeUnit.SECONDS )
                .subscribe( aVoid -> startActivity( new Intent( this, SearchUserActivity.class ) ) );
        mAdapter.setOnItemChildClickListener( (adapter, view, position) -> {
                switch (view.getId()){
                    case R.id.ivHeader:
                        Intent intent = new Intent( this,FriendInfoActivity.class );
                        intent.putExtra( "friendinfo",mData.get( position ));
                        TransitionsHeleper.startActivity( (Activity) mContext,intent,view,mData.get( position ).getUserHead());
                        break;
                    case R.id.btnAck:
                        this.position = position;
                        mPresenter.addFriend( mData.get( position ).getUsername() );
                }
        } );
        mAdapter.setOnItemLongClickListener( (adapter, view, position) -> {
            this.position = position;
            dialog.show();
            return false;
        } );
    }

    private void initAdapter() {
        mRvNewfriend.setHasFixedSize(true);
        mRvNewfriend.setLayoutManager( new LinearLayoutManager( this ) );
        mAdapter = new NewFriendAdapter( R.layout.item_newfriend,mData );
        mRvNewfriend.setAdapter( mAdapter );

        View headview = getLayoutInflater().inflate( R.layout.item_searchuser_head,(ViewGroup)mRvNewfriend.getParent(),false );
        TextView headviewTV = (TextView) headview.findViewById( R.id.tv_search_head );
        headviewTV.setText( "Bạn mới" );
        mAdapter.addHeaderView( headview );
    }

    private void initToobar() {
        RxView.clicks( mImBack )
                .throttleFirst( 1, TimeUnit.SECONDS )
                .subscribe( aVoid -> finish() );
    }

    @Override
    public void onNext(List<Friend> mlist) {
        mData = mlist;
        if (mData.size() == 0){
            mAdapter.setNewData( null );
            mAdapter.setEmptyView( emtyView );
        } else {
            mAdapter.setNewData( mData );
        }
    }

    @Override
    public void onAddNext() {
        EventBus.getDefault().post( new MessageEvent( "friendchange","" ) );
        NewFriendDbHelper.getInstance(this).delFriend(mData.get( position ).getUsername());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NewMsgDbHelper.getInstance(this).delNewMsg(""+0);
        EventBus.getDefault().post( new MessageEvent( "ChatNewMsg","" ) );
    }
}
