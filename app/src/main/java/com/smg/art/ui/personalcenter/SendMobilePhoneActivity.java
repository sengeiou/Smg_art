package com.smg.art.ui.personalcenter;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smg.art.R;
import com.smg.art.base.BaseActivity;
import com.smg.art.component.AppComponent;
import com.smg.art.utils.KeyBoardUtils;
import com.zhy.autolayout.AutoRelativeLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Mervin on 2018/7/26 0026.
 */

public class SendMobilePhoneActivity extends BaseActivity {
    @BindView(R.id.rl_back)
    AutoRelativeLayout rlBack;
    @BindView(R.id.left_title)
    TextView leftTitle;
    @BindView(R.id.actionbar_title)
    TextView actionbarTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.actionbar_text_action)
    TextView actionbarTextAction;
    @BindView(R.id.et_context)
    EditText etContext;
    @BindView(R.id.name)
    LinearLayout name;
    @BindView(R.id.graph_code)
    EditText graphCode;
    @BindView(R.id.get_code)
    TextView getCode;
    @BindView(R.id.next)
    TextView next;


    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.send_mess;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void detachView() {

    }

    @Override
    public void initView() {
        actionbarTitle.setText(R.string.change_phone);
    }

    @OnClick({R.id.rl_back, R.id.next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                KeyBoardUtils.hiddenKeyboart(this);
                finish();
                break;
            case R.id.next:
                startActivityIn(new Intent(this, BindingPhoneActivity.class), this);
                break;
        }
    }

}