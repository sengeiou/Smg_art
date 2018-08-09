package com.smg.art.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.utils.TimeUtils;
import com.blankj.utilcode.utils.ToastUtils;
import com.google.gson.Gson;
import com.smg.art.R;
import com.smg.art.base.AuctionBuyerDepositBean;
import com.smg.art.base.AuctionDetailBean;
import com.smg.art.base.BaseActivity;
import com.smg.art.base.Constant;
import com.smg.art.base.FindCustomerServiceBean;
import com.smg.art.bean.SaveCollectsBean;
import com.smg.art.bean.UpudterMessageBean;
import com.smg.art.component.AppComponent;
import com.smg.art.component.DaggerMainComponent;
import com.smg.art.presenter.contract.activity.GoodsDetailContract;
import com.smg.art.presenter.impl.activity.GoodsDetailActivityPresenter;
import com.smg.art.ui.adapter.ServiceDialogApadter;
import com.smg.art.utils.CallPhone;
import com.smg.art.utils.GlideUtils;
import com.smg.art.utils.LocalAppConfigUtil;
import com.smg.art.utils.TimeTools;
import com.smg.art.utils.ValidateTime;
import com.smg.art.view.CustomDialog;
import com.smg.art.view.MyBridgeWebView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.CSCustomServiceInfo;

public class GoodsDetailActivity extends BaseActivity implements GoodsDetailContract.View {

    @Inject
    GoodsDetailActivityPresenter mPresenter;
    @BindView(R.id.viewpager)
    NestedScrollView viewpager;
    @BindView(R.id.toolbar_back)
    ImageView toolbarBack;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appBar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.tv_collectioin)
    TextView tvCollectioin;
    @BindView(R.id.phone_service)
    TextView phoneService;
    @BindView(R.id.tv_actionName)
    TextView tvActionName;
    @BindView(R.id.webview)
    MyBridgeWebView webview;
    @BindView(R.id.banner)
    BGABanner banner;
    @BindView(R.id.tv_startPrice)
    TextView tvStartPrice;
    @BindView(R.id.tv_frontMoneyAmount)
    TextView tvFrontMoneyAmount;
    @BindView(R.id.tv_show)
    TextView tvShow;
    @BindView(R.id.tv_hh)
    TextView tvHh;
    @BindView(R.id.tv_mm)
    TextView tvMm;
    @BindView(R.id.tv_ss)
    TextView tvSs;
    @BindView(R.id.tv_auction)
    TextView tvAuction;
    @BindView(R.id.bt_auction)
    Button btAuction;

    private int postion;
    private AuctionDetailBean detailBean;
    private List<FindCustomerServiceBean.DataBean> serviceDatas = new ArrayList<>();
    private ServiceDialogApadter apadter;


    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_goods_detail;
    }

    @Override
    public void attachView() {
        mPresenter.attachView(this, this);
    }

    @Override
    public void detachView() {
        mPresenter.detachView();
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        setSwipeBackEnable(true);
        webview.setBackgroundColor(0);
        postion = getIntent().getIntExtra("postion", 0);
        mPresenter.FetchHomepageGetauctiondetail("id", String.valueOf(postion));

        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Rect rect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                int endOffset = appBarLayout.getHeight() - toolbar.getHeight();
                int startOffset = 0;
                int offset = Math.abs(verticalOffset);
                if (offset <= startOffset) {
                    toolbar.getBackground().setAlpha(0);
                    toolbarTitle.setVisibility(View.GONE);
                } else if (offset > startOffset && offset < endOffset) {
                    float precent = (float) (offset - startOffset) / endOffset;
                    int alpha = Math.round(precent * 255);
                    toolbar.getBackground().setAlpha(alpha);
                    toolbarTitle.setVisibility(View.GONE);
                    if (alpha <= 126) {
                        toolbarBack.setImageResource(R.drawable.arrow_back);
                    } else {
                        toolbarBack.setImageResource(R.drawable.arrow_back);
                    }
                } else if (offset >= endOffset) {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    toolbar.getBackground().setAlpha(255);
                    toolbarTitle.setVisibility(View.VISIBLE);
                    toolbarTitle.setText("详情介绍");
                    toolbarBack.setImageResource(R.drawable.arrow_back);

                }
            }
        });
    }

    @Override
    public void showError(String message) {
        ToastUtils.showLongToast(message);
    }


    /**
     * 新增收藏商品
     */
    @Override
    public void FetchMembercollectspageSaveSuccess(SaveCollectsBean saveCollectsBean) {
        ToastUtils.showLongToast(saveCollectsBean.getMsg());
    }

    /**
     * 保证金支付
     */
    @Override
    public void FetchAuctionBuyerDepositSuccess(AuctionBuyerDepositBean auctionBuyerDepositBean) {
        ToastUtils.showLongToast(auctionBuyerDepositBean.getMsg());
    }

    /**
     * 查询客服信息
     */
    @Override
    public void FetchFindCustomerServiceSuccess(FindCustomerServiceBean findCustomerServiceBean) {
        if (this.serviceDatas.size() != 0) serviceDatas.clear();
        serviceDatas = findCustomerServiceBean.getData();
        apadter.setNewData(serviceDatas);
    }

    /**
     * 拍卖品详情
     */
    @Override
    public void FetchHomepageGetauctiondetailSuccess(AuctionDetailBean auctionDetailBean) {
        this.detailBean = auctionDetailBean;
        tvActionName.setText(auctionDetailBean.getData().getActionName());
        tvStartPrice.setText("￥ " + String.valueOf(auctionDetailBean.getData().getStartPrice()));
        tvFrontMoneyAmount.setText("￥ " + String.valueOf(auctionDetailBean.getData().getFrontMoneyAmount()));
        if (detailBean.getData().getDepositStatus() == 0) {
            btAuction.setText("交保证金参与");
        } else {
            btAuction.setText("保证金已支付");
        }


        long time;
        if (auctionDetailBean.getData().getSysDate() > 0) {
            time = auctionDetailBean.getData().getSysDate();
        } else {
            time = System.currentTimeMillis();//获取系统时间的10位的时间戳
        }

        if (time < auctionDetailBean.getData().getStartTime()) {
            long countTime =auctionDetailBean.getData().getStartTime()- time;
            //将前一个缓存清除
            if (countTime > 0) {
                CountDownTimer countDownTimer = new CountDownTimer(countTime, 1000) {
                    public void onTick(long millisUntilFinished) {
                        String hour = TimeTools.getCountTimeByLong(millisUntilFinished);
                        String[] array = hour.split(":");
                        tvHh.setText(array[0]);
                        tvMm.setText(array[1]);
                         tvSs.setText(array[2]);
                    }
                    public void onFinish() {
                        tvHh.setText("00");
                        tvSs.setText("00");
                        tvMm.setText("00");
                    }
                }.start();
            } else {
                tvHh.setText("00");
                tvSs.setText("00");
                tvMm.setText("00");
            }
        } else if (time > auctionDetailBean.getData().getStartTime()) {
            tvHh.setText("00");
            tvSs.setText("00");
            tvMm.setText("00");
        }

        String[] split = auctionDetailBean.getData().getPictureUrl().split(",");
        List<String> imgUrls = new ArrayList<>();

        for (int i = 0; i < split.length; i++) {
            imgUrls.add(Constant.BaseImgUrl + split[i]);
        }

        banner.setAdapter(new BGABanner.Adapter<ImageView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
                GlideUtils.loadFitCenter(GoodsDetailActivity.this, model, itemView);
            }
        });
        banner.setData(imgUrls, null);
        webview.loadDataWithBaseURL(null, getNewContent(auctionDetailBean.getData().getAuctionDesc()), "text/html", "utf-8", null);
    }

    @Override
    public void finish() {
        if (webview != null) {
            webview.removeAllViews();
            webview.destroy();
            webview = null;
        }
        super.finish();
    }

    @Subscribe
    public void getEventBus(AuctionBuyerDepositBean auctionBuyerDepositBean) {
        //支付保证金回来
        btAuction.setText("保证金已支付");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @SuppressLint("MissingPermission")
    @OnClick({R.id.tv_collectioin, R.id.phone_service, R.id.toolbar_back, R.id.bt_auction})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back: //返回
                finish();
                break;
            case R.id.tv_collectioin:  //收藏
                if (detailBean != null)
                    mPresenter.FetchMembercollectspageSave("memberId", String.valueOf(LocalAppConfigUtil.getInstance().getCurrentMerberId()),
                            "goodsId", String.valueOf(detailBean.getData().getGoodsId()));
                break;
            case R.id.bt_auction:  // 交保证金参与
                if (detailBean != null) {
                    Intent intent = new Intent(this, AuctionBuyerDepositActivity.class);
                    intent.putExtra("data", new Gson().toJson(detailBean));
                    intent.putExtra("type", 1);
                    startActivityIn(intent, this);
                }
                break;

            case R.id.phone_service:  //客服

                apadter = new ServiceDialogApadter(serviceDatas,GoodsDetailActivity.this);
                View dialogview = View.inflate(GoodsDetailActivity.this, R.layout.dialog_service, null);
                RecyclerView rv = dialogview.findViewById(R.id.rv_service);
                rv.setLayoutManager(new LinearLayoutManager(GoodsDetailActivity.this));

                rv.setAdapter(apadter);
                final CustomDialog mDialogWaiting = new CustomDialog(GoodsDetailActivity.this, dialogview, R.style.MyDialog);
                mDialogWaiting.show();
                mDialogWaiting.setCancelable(true);
                mPresenter.FetchFindCustomerService();
                TextView tvCencl = dialogview.findViewById(R.id.tv_cencl);
                tvCencl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mDialogWaiting != null) mDialogWaiting.dismiss();
                    }
                });

                apadter.OnServiceItemListener(new ServiceDialogApadter.OnServiceItemListener() {
                    @Override
                    public void OnServiceItemListener(FindCustomerServiceBean.DataBean item) {
                        CSCustomServiceInfo.Builder csBuilder = new CSCustomServiceInfo.Builder();
                        CSCustomServiceInfo csInfo = csBuilder
                                .nickName("融云").build();
                        RongIM.getInstance().startCustomerServiceChat(GoodsDetailActivity.this, item.getMemberNo(), item.getMemberName(), csInfo);
                        if (mDialogWaiting != null) mDialogWaiting.dismiss();
                    }
                });
                break;
        }
    }


    private String getNewContent(String htmltext) {
        Document doc = Jsoup.parse(htmltext);
        Elements elements = doc.getElementsByTag("img");
        for (Element element : elements) {
            element.attr("width", "100%").attr("height", "auto");
        }
        return doc.toString();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }

    private PermissionListener listener = new PermissionListener() {
        @SuppressLint("MissingPermission")
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            if (!TextUtils.isEmpty(LocalAppConfigUtil.getInstance().getServiceTel())) {

                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + LocalAppConfigUtil.getInstance().getServiceTel());
                intent.setData(data);
                startActivity(intent);
            } else {
                Intent intent2 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:0755-82714092"));
                startActivity(intent2);
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {

        }
    };

    /**
     * 判断2个时间大小
     * yyyy-MM-dd HH:mm 格式
     * @param startTime
     * @param nowTime
     * @return
     */
    public static int getTimeCompareSize(String startTime, String nowTime){
        int i=0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//年-月-日 时-分
        try {
            Date date1 = dateFormat.parse(startTime);//开始时间
            Date date2 = dateFormat.parse(nowTime);//现在时间
            // 1 现在时间小于开始时间 2 开始时间与现在时间 3 现在时间大于开始时间
            if (date2.getTime()<date1.getTime()){
                i= 1;
            }else if (date2.getTime()==date1.getTime()){
                i= 2;
            }else if (date2.getTime()>date1.getTime()){
                i= 3;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  i;
    }

}
