package hoangcuongdev.com.xmpp.base;

/**
 * Created by GreenLove on 2017
 */

public interface IBaseView {
    /**
     * Show message
     *
     * @param msg
     */
    void showTip(String msg);

    /**
     * Load success result
     *
     * @param object
     */
    void loadSuccess(Object object);

    /**
     * Load fail result
     *
     * @param errorMsg
     */
    void loadFailure(String errorMsg);

    /**
     * Show loading dialog
     */
    void showLoadingDialog();

    /**
     * Close loading dialog
     */
    void closeLoadingDialog();

    /**
     * Error
     */
    void onError(Throwable e);
}
