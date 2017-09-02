package hoangcuongdev.com.xmpp.ui.adapter;

import android.graphics.Bitmap;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import hoangcuongdev.com.xmpp.R;
import hoangcuongdev.com.xmpp.model.bean.Friend;
import hoangcuongdev.com.xmpp.model.dao.NewFriendDbHelper;
import hoangcuongdev.com.xmpp.utils.ImageUtil;

/**
 * Created by GreenLove on 2017
 */

public class NewFriendAdapter extends BaseQuickAdapter<Friend,BaseViewHolder>{

    public NewFriendAdapter(int layoutResId, List<Friend> data) {
        super( layoutResId, data );
    }

    @Override
    protected void convert(BaseViewHolder helper, Friend item) {
        helper.setText( R.id.tvName,item.getNickname());
        Bitmap headimg = ImageUtil.getBitmapFromBase64String(item.getUserHead());
        if (headimg != null){
            helper.setImageBitmap(R.id.ivHeader,headimg);
        }else {
            helper.setImageResource(R.id.ivHeader,R.mipmap.default_tp);
        }

        if (NewFriendDbHelper.getInstance(mContext).isDeal(item.getUsername())){
            helper.setVisible( R.id.tvAdded,true );
            helper.setVisible( R.id.btnAck,false );
        }else {
            helper.setVisible( R.id.tvAdded,false );
            helper.setVisible( R.id.btnAck,true );
        }
        helper.addOnClickListener( R.id.btnAck )
        .addOnClickListener( R.id.ivHeader );
    }
}
