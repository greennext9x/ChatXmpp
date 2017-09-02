package hoangcuongdev.com.xmpp.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.SystemClock;

import com.cjt2325.cameralibrary.JCameraView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import hoangcuongdev.com.xmpp.R;
import hoangcuongdev.com.xmpp.base.BaseActivity;
import hoangcuongdev.com.xmpp.model.bean.Constants;

public class TakePhotoActivity extends BaseActivity {

    private JCameraView mJCameraView;

    @Override
    protected void initView() {
        setContentView( R.layout.activity_take_photo );
    }

    @Override
    protected void initPresenter() {

    }

    @Override
    protected void init() {
        mJCameraView = (JCameraView) findViewById( R.id.cameraview );
        mJCameraView.setSaveVideoPath( Environment.getExternalStorageDirectory().getPath() );
        mJCameraView.setAutoFoucs( false );
        File file = new File( Constants.SAVE_SOUND_PATH );
        if (!file.exists())
            file.mkdirs();
        mJCameraView.setSaveVideoPath( Constants.SAVE_SOUND_PATH );
        initListener();
    }

    private void initListener() {
        mJCameraView.setCameraViewListener( new JCameraView.CameraViewListener() {
            @Override
            public void quit() {
                finish();
            }

            @Override
            public void captureSuccess(Bitmap bitmap) {
                String path = saveBitmap( bitmap, Constants.SAVE_IMG_PATH + "/xmpp_" + SystemClock.currentThreadTimeMillis() + ".png" );
                Intent data = new Intent();
                data.putExtra( "take_photo", true );
                data.putExtra( "path", path );
                setResult( RESULT_OK, data );
                finish();
            }

            @Override
            public void recordSuccess(String url) {
//                Intent data = new Intent();
//                data.putExtra("take_photo", false);
//                data.putExtra("path", url);
//                setResult(RESULT_OK, data);
//                finish();
                showTip( "暂时没有小视频功能" );
            }
        } );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mJCameraView != null)
            mJCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mJCameraView != null)
            mJCameraView.onPause();
    }

    public String saveBitmap(Bitmap bm, String filePath) {
        String path = "";
        File f = new File( filePath );
        if (!f.exists()) {
            File file2 = new File( filePath.substring( 0, filePath.lastIndexOf( "/" ) + 1 ) );
            file2.mkdirs();
        }
        try {
            FileOutputStream out = new FileOutputStream( f );
            bm.compress( Bitmap.CompressFormat.PNG, 100, out );
            out.flush();
            out.close();
            path = f.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
