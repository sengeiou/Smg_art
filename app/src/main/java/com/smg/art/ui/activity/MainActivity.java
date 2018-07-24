package com.smg.art.ui.activity;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.smg.art.R;
import com.smg.art.base.BaseActivity;
import com.smg.art.base.BaseFragmentPageAdapter;
import com.smg.art.bean.Apk_UpdateBean;
import com.smg.art.component.AppComponent;
import com.smg.art.component.DaggerMainComponent;
import com.smg.art.presenter.contract.activity.MainContract;
import com.smg.art.presenter.impl.activity.MainActivityPresenter;
import com.smg.art.ui.fragment.AuctionFragment;
import com.smg.art.ui.fragment.HomeFragment;
import com.smg.art.ui.fragment.MyFragment;
import com.smg.art.utils.UIUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity implements MainContract.View {


    public static MainActivity mainActivity;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.vp)
    ViewPager vp;
    @Inject
    MainActivityPresenter mPresenter;
    private ArrayList<String> mTitleList = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
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
        setSwipeBackEnable(false);

        mTitleList.add(UIUtils.getString(R.string.app_name));
        mTitleList.add(UIUtils.getString(R.string.app_name));
        mTitleList.add(UIUtils.getString(R.string.app_name));
        mTitleList.add(UIUtils.getString(R.string.app_name));

        mFragments.add(new HomeFragment());
        mFragments.add(new AuctionFragment());
        mFragments.add(new HomeFragment());
        mFragments.add(new MyFragment());
//        vp = (ViewPager) findViewById(R.id.vp);
        BaseFragmentPageAdapter myAdapter = new BaseFragmentPageAdapter(getSupportFragmentManager(), mFragments, mTitleList);
        vp.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        tabLayout.setupWithViewPager(vp);

        mPresenter.Apk_Update();
        mainActivity = this;
    }

    @Override
    public void showError(String message) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void ApkUpdateS(Apk_UpdateBean.DataBean dataBean) {

    }

    @Override
    public TextView tv() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
