package hoangcuongdev.com.xmpp.ui.adapter;

import android.graphics.Bitmap;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lqr.emoji.MoonUtils;

import java.util.List;

import cn.bingoogolapple.badgeview.BGABadgeView;
import hoangcuongdev.com.xmpp.R;
import hoangcuongdev.com.xmpp.model.bean.ChatItem;
import hoangcuongdev.com.xmpp.model.bean.Constants;
import hoangcuongdev.com.xmpp.model.dao.NewMsgDbHelper;
import hoangcuongdev.com.xmpp.utils.DateUtil;
import hoangcuongdev.com.xmpp.utils.ImageUtil;

/**
 * Created by GreenLove on 2017
 */

public class MessageAdapter extends BaseQuickAdapter<ChatItem, BaseViewHolder> {
    private NewMsgDbHelper newMsgDbHelper;

    public MessageAdapter(int layoutResId, List<ChatItem> data) {
        super( layoutResId, data );
    }

    @Override
    protected void convert(BaseViewHolder helper, ChatItem item) {
        newMsgDbHelper = NewMsgDbHelper.getInstance( mContext );
        if (item.chatType == ChatItem.CHAT) {
            helper.setText( R.id.tvName, item.nickName )
                    .setText( R.id.tvTime, DateUtil.getMsgFormatTime( item.sendDate ) );
            Bitmap headimg = ImageUtil.getBitmapFromBase64String( item.head );
            if (headimg != null)
                helper.setImageBitmap( R.id.ivHeader, headimg );
            else
                helper.setImageResource( R.id.ivHeader, R.mipmap.default_tp );
            if (item.msg != null) {
                if (item.subject == null || item.subject.equals( Constants.SEND_TXT )) {
                    TextView tvContent = helper.getView( R.id.tvMsg );
                    MoonUtils.identifyFaceExpression( mContext, tvContent, item.msg, ImageSpan.ALIGN_BOTTOM );
                } else if (item.subject.equals( Constants.SEND_IMG ))
                    helper.setText( R.id.tvMsg, "[Hình ảnh]" );
                else if (item.subject.equals( Constants.SEND_SOUND ))
                    helper.setText( R.id.tvMsg, "[Tiếng nói]" );
                else if (item.subject.equals( Constants.SEND_LOCATION ))
                    helper.setText( R.id.tvMsg, "[Địa điểm]" );
            }
        }
        int newCount = newMsgDbHelper.getMsgCount( item.chatName );
        BGABadgeView header = helper.getView( R.id.bVheadview );
        if (newCount > 0) {
            header.showTextBadge( newCount + "" );
            header.setDragDismissDelegage( badgeable -> newMsgDbHelper.delNewMsg( item.chatName ) );
        }else {
            header.hiddenBadge();
        }
    }
}
