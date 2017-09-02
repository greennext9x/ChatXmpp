package hoangcuongdev.com.xmpp.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.widget.ImageView;

import com.aitangba.swipeback.ActivityLifecycleHelper;
import com.bumptech.glide.Glide;
import com.lqr.emoji.LQREmotionKit;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.loader.ImageLoader;
import com.lqr.imagepicker.view.CropImageView;

/**
 * Created by GreenLove on 2017
 */

public class MyApplication extends Application{
    private static MyApplication mInstance = null;
    private static Context mContext;
    private static Handler mHandler;


    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = this.getApplicationContext();
        mHandler = new Handler();
        registerActivityLifecycleCallbacks( ActivityLifecycleHelper.build());
        LQREmotionKit.init( this );
        initImagePicker();
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
                Glide.with(getContext()).load( Uri.parse("file://" + path).toString()).centerCrop().into(imageView);
            }

            @Override
            public void clearMemoryCache() {

            }
        });
        imagePicker.setShowCamera(true);
        imagePicker.setCrop(true);
        imagePicker.setSaveRectangle(true);
        imagePicker.setSelectLimit(9);
        imagePicker.setStyle( CropImageView.Style.RECTANGLE);
        imagePicker.setFocusWidth(800);
        imagePicker.setFocusHeight(800);
        imagePicker.setOutPutX(1000);
        imagePicker.setOutPutY(1000);
    }


    public static Handler getMainHandler() {
        return mHandler;
    }


    public Context getContext(){
        return this.mContext;
    }
}
