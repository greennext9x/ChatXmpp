package hoangcuongdev.com.xmpp.ui.adapter;

import android.graphics.Bitmap;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import hoangcuongdev.com.xmpp.R;
import hoangcuongdev.com.xmpp.model.bean.Friend;
import hoangcuongdev.com.xmpp.model.bean.MyContacts;
import hoangcuongdev.com.xmpp.utils.ImageUtil;

/**
 * Created by GreenLove on 2017
 */

public class ContactsAdapter extends BaseSectionQuickAdapter<MyContacts,BaseViewHolder>{
    public ContactsAdapter(int layoutResId, int sectionHeadResId, List<MyContacts> data) {
        super( layoutResId, sectionHeadResId, data );
    }

    @Override
    protected void convertHead(BaseViewHolder helper, MyContacts item) {
        helper.setText( R.id.header, item.header);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyContacts item) {
        Friend mfriend = item.t;
        helper.setText(R.id.tvName,mfriend.getNickname());
        Bitmap headimg = ImageUtil.getBitmapFromBase64String(mfriend.getUserHead());
        if (headimg != null){
            helper.setImageBitmap(R.id.ivHeader,headimg);
        }else {
            helper.setImageResource(R.id.ivHeader,R.mipmap.default_tp);
        }
        helper.addOnClickListener(R.id.ivHeader)
        .addOnClickListener(R.id.llcontent);
    }
}
