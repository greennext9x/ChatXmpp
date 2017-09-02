package hoangcuongdev.com.xmpp.base;

import android.content.Context;

/**
 * Created by GreenLove on 2017
 */

public abstract class BasePresenter {
    protected Context mContext;
    protected int mState=0;

    public BasePresenter(Context mContext) {
        this.mContext = mContext;
    }
}
