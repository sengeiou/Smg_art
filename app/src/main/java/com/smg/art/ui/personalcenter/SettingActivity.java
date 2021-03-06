package com.smg.art.ui.personalcenter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.TimeUtils;
import com.blankj.utilcode.utils.ToastUtils;
import com.smg.art.BuildConfig;
import com.smg.art.R;
import com.smg.art.base.BaseActivity;
import com.smg.art.base.Constant;
import com.smg.art.bean.PersonalCenterBean;
import com.smg.art.bean.UpLoadBean;
import com.smg.art.component.AppComponent;
import com.smg.art.component.DaggerMainComponent;
import com.smg.art.photo.ClipImageActivity;
import com.smg.art.presenter.contract.activity.SettingContract;
import com.smg.art.presenter.impl.activity.SettingActivityPresenter;
import com.smg.art.ui.login.LoginActivity;
import com.smg.art.utils.CameraCanUseUtils;
import com.smg.art.utils.ClipFileUtil;
import com.smg.art.utils.GlideUtils;
import com.smg.art.utils.ImageFormartUtils;
import com.smg.art.utils.KeyBoardUtils;
import com.smg.art.utils.L;
import com.smg.art.utils.LocalAppConfigUtil;
import com.smg.art.view.PopDialog;
import com.smg.art.view.RoundImageView;
import com.zhy.autolayout.AutoRelativeLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Mervin on 2018/7/25 0025.
 */

public class SettingActivity extends BaseActivity implements SettingContract.View {
    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;
    //请求访问外部存储
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    //请求写入外部存储
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;
    @Inject
    SettingActivityPresenter mPresenter;
    @BindView(R.id.rl_back)
    AutoRelativeLayout actionbarBack;
    @BindView(R.id.left_title)
    TextView leftTitle;
    @BindView(R.id.actionbar_title)
    TextView actionbarTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.actionbar_text_action)
    TextView actionbarTextAction;
    @BindView(R.id.civMyPicture)
    RoundImageView civMyPicture;
    @BindView(R.id.name)
    LinearLayout name;
    @BindView(R.id.phone)
    LinearLayout phone;
    @BindView(R.id.pwd)
    LinearLayout pwd;
    @BindView(R.id.pay_pwd)
    LinearLayout payPwd;
    @BindView(R.id.exit)
    TextView exit;
    PopDialog popDialog;
    Intent intent;
    @BindView(R.id.nick)
    TextView nick;
    @BindView(R.id.phone_num)
    TextView phoneNum;
    //调用照相机返回图片文件
    private File tempFile;
    // 1: 圆形裁剪, 2: 矩形裁剪
    private int type = 0;
    private int RECode = 1;
    private String headImg;

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.setting;
    }

    @Override
    public void attachView() {
        mPresenter.attachView(this, this);
    }

    @Override
    public void detachView() {
//        mPresenter.detachView();
    }

    @Override
    public void initView() {
        getData();
        actionbarTitle.setText(R.string.setting);
        GlideUtils.load(this, Constant.BaseImgUrl + LocalAppConfigUtil.getInstance().getHeadImg(), civMyPicture);
    }

    @OnClick({R.id.rl_back, R.id.civMyPicture, R.id.name, R.id.phone, R.id.pwd, R.id.pay_pwd, R.id.exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                KeyBoardUtils.hiddenKeyboart(this);
                finish();
                break;
            case R.id.civMyPicture:
                type = 1;
                uploadHeadImage();
                break;
            case R.id.name:
                intent = new Intent(this, ChangeNickNameActivity.class);
                startActivityIn(intent, this);
                break;
            case R.id.phone:
                intent = new Intent(this, SendMobilePhoneActivity.class);
                startActivityIn(intent, this);
                break;
            case R.id.pwd:
                intent = new Intent(this, ChangePasswordActivity.class);
                startActivityIn(intent, this);
                break;
            case R.id.pay_pwd:
                intent = new Intent(this, ChangeTradersPasswordActivity.class);
                startActivityIn(intent, this);
                break;
            case R.id.exit:
                popDialog = new PopDialog(this, R.layout.exit_item);
                popDialog.show();
                popDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popDialog.dismiss();
                    }
                });
                popDialog.findViewById(R.id.roll_out_submit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popDialog.dismiss();
                        RongIM.getInstance().logout();
                        LocalAppConfigUtil.getInstance().setAccessToken("");
                        LocalAppConfigUtil.getInstance().setCurrentMerberId(0);
                        LocalAppConfigUtil.getInstance().setCurrentMerberNo("");
                        LocalAppConfigUtil.getInstance().setJsessionidShiro("");
                        LocalAppConfigUtil.getInstance().setJsessionId("");
                        LocalAppConfigUtil.getInstance().setRCToken("");
                        LocalAppConfigUtil.getInstance().setUserTelephone("");
                        LocalAppConfigUtil.getInstance().setPassword("");
                        killAll();
                        intent = new Intent(SettingActivity.this, LoginActivity.class);
                        startActivityIn(intent, SettingActivity.this);
                    }
                });
                break;
        }
    }

    private void getData() {
        mPresenter.FetchPersonalCenter("memberId", String.valueOf(LocalAppConfigUtil.getInstance().getCurrentMerberId()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = getIntent();
        this.setResult(10, intent);
        finish();
    }

    /**
     * 上传头像
     */
    private void uploadHeadImage() {
        popDialog = new PopDialog(this, R.layout.layout_popupwindow);
        popDialog.show();
        popDialog.findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到调用系统相机
                if (CameraCanUseUtils.isCameraCanUse()) {
                    //摄像头可用
                    gotoCamera();
                } else {
                    //摄像头不可用
                    ToastUtils.showShortToast("没相机权限，请到应用程序权限管理开启权限");
                    //跳转至app设置
                    getAppDetailSettingIntent();
                }
               /* //权限判断
                if (ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    //跳转到调用系统相机
                    if (CameraCanUseUtils.isCameraCanUse()) {
                        //摄像头可用
                        gotoCamera();
                    } else {
                        //摄像头不可用
                        ToastUtils.showShortToast("没相机权限，请到应用程序权限管理开启权限");
                        //跳转至app设置
                        getAppDetailSettingIntent();
                    }

                }*/
                popDialog.dismiss();
            }
        });
        popDialog.findViewById(R.id.btn_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //权限判断
                if (ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //申请READ_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    //跳转到相册
                    gotoPhoto();
                }
                popDialog.dismiss();
            }
        });
        popDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDialog.dismiss();
            }
        });
    }


    /**
     * 外部存储权限申请返回
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                gotoCamera();
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                gotoPhoto();
            }
        }
    }

    /**
     * 跳转到相册
     */
    private void gotoPhoto() {
        L.e("evan", "*****************打开图库********************");
        //跳转到调用系统图库
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
    }


    /**
     * 跳转到照相机
     */
    private void gotoCamera() {
        Log.d("evan", "*****************打开相机********************");
        //创建拍照存储的图片文件
        tempFile = new File(ClipFileUtil.checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"), System.currentTimeMillis() + ".jpg");

        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(SettingActivity.this, BuildConfig.APPLICATION_ID + ".fileProvider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    //跳转app设置
    private void getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", this.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", this.getPackageName());
        }
        startActivity(localIntent);
    }

    /**
     * 打开截图界面
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(SettingActivity.this, ClipImageActivity.class);
        intent.putExtra("type", type);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == SettingActivity.this.RESULT_OK) {
                    gotoClipActivity(Uri.fromFile(tempFile));
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == SettingActivity.this.RESULT_OK) {
                    Uri uri = intent.getData();
                    gotoClipActivity(uri);
                }
                break;
            case REQUEST_CROP_PHOTO:  //剪切图片返回
                String imageFormat = null;
                if (resultCode == SettingActivity.this.RESULT_OK) {
                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    final String cropImagePath = ClipFileUtil.getRealFilePathFromUri(getApplicationContext(), uri);
                    File file = new File(cropImagePath);
                    final Bitmap bitMap = BitmapFactory.decodeFile(cropImagePath);
                    try {
                        imageFormat = ImageFormartUtils.getPicType(new FileInputStream(new File(cropImagePath)));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (type == 1) {

                        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM); //表单类型
                        File headerfile = new File(cropImagePath);
                        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), headerfile);//表单类型
                        builder.addFormDataPart("memberId", String.valueOf(LocalAppConfigUtil.getInstance().getCurrentMerberId()));//传入服务器需要的key，和相应value值
                        builder.addFormDataPart("upfile", headerfile.getName(), body); //添加图片数据，body创建的请求体
                        List<MultipartBody.Part> parts = builder.build().parts();
                        mPresenter.FetchUploadPic(parts);

                    }
                }
                break;
        }
    }

    @Override
    public void FetchUploadPicSuccess(UpLoadBean upLoadBean) {

        headImg = upLoadBean.getHeadImg() + "?t=" + String.valueOf(TimeUtils.getNowTimeMills());
        GlideUtils.load(this, Constant.BaseImgUrl +headImg, civMyPicture);
        LocalAppConfigUtil.getInstance().setRongUserHeadImg(headImg);

        RongIM.getInstance().refreshUserInfoCache(new UserInfo(

                LocalAppConfigUtil.getInstance().getRongUserId(),
                LocalAppConfigUtil.getInstance().getRongUserName(),
                Uri.parse(Constant.BaseImgUrl +headImg )

        ));
    }

    @Override
    public void FetchPersonalCenterSuccess(PersonalCenterBean announcementAuctionListBean) {

        if (announcementAuctionListBean.getStatus() == 1) {
            GlideUtils.clearImageAllCache();
            GlideUtils.load(this, Constant.BaseImgUrl + announcementAuctionListBean.getData().getHeadImg(), civMyPicture);
            nick.setText(announcementAuctionListBean.getData().getMemberName());
            phoneNum.setText(announcementAuctionListBean.getData().getMobilePhone());
        } else {
            ToastUtils.showShortToast(announcementAuctionListBean.getMsg());
        }
    }

    @Override
    public void showError(String message) {

    }
}
