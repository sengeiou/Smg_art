
package com.smg.art.presenter.impl.fragment;

import com.blankj.utilcode.utils.ToastUtils;
import com.smg.art.api.Api;
import com.smg.art.bean.AuctionDetailBean;
import com.smg.art.base.BasePresenter;
import com.smg.art.bean.AuctionCenterBean;
import com.smg.art.bean.RefundBean;
import com.smg.art.presenter.contract.fragment.AuctionCentreContract;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class AuctionConterPresenter extends BasePresenter<AuctionCentreContract.View> implements AuctionCentreContract.Presenter<AuctionCentreContract.View> {

    private Api api;

    @Inject
    public AuctionConterPresenter(Api api) {
        this.api = api;

    }

    @Override
    public void FetchAuctionCenterList(String... s) {
//    showWaitingDialog("加载中...");
        addSubscrebe(api.FetchAuctionCenterList(s).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AuctionCenterBean>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideWaitingDialog();
                        mView.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(AuctionCenterBean data) {
                        hideWaitingDialog();
                        if (mView != null && data != null && data.getStatus() == 1) {
                            mView.FetchAuctionCenterListSuccess(data);

                        }else if(mView != null && data!=null && data.getStatus() !=1 && data.getMsg()!=null){
                            ToastUtils.showLongToast(data.getMsg());

                        }
                    }
                }));
    }

    @Override
    public void FetchCreatBidding(String... s) {
        showWaitingDialog("加载中...");
        addSubscrebe(api.FetchCreatBidding(s).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AuctionCenterBean>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideWaitingDialog();
                        mView.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(AuctionCenterBean data) {
                        hideWaitingDialog();
                        if (mView != null && data != null && data.getStatus() == 1) {
                            mView.getetCreatBidding().setText(" ");
                            ToastUtils.showLongToast("出价成功");
                        } else if(mView != null && data!=null && data.getStatus() !=1 && data.getMsg()!=null){
                            ToastUtils.showLongToast(data.getMsg());
                            mView.getetCreatBidding().setText(" ");

                        }
                    }
                }));
    }

    @Override
    public void FetchHomepageGetauctiondetail(String... s) {
        addSubscrebe(api.FetchHomepageGetauctiondetail(s).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AuctionDetailBean>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideWaitingDialog();
                        mView.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(AuctionDetailBean data) {
                        hideWaitingDialog();
                        if (mView != null && data != null && data.getStatus() == 1) {
                            mView.FetchHomepageGetauctiondetailSuccess(data);
                        } else {
                            if (data != null && data.getMsg() != null)
                                mView.showError(data.getMsg());
                        }
                    }
                }));
    }

    @Override
    public void FetchvalidteTradePwd(String... s) {
        addSubscrebe(api.FetchvalidteTradePwd(s).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RefundBean>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideWaitingDialog();
                        ToastUtils.showLongToast("验证交易密码失败");
                    }

                    @Override
                    public void onNext(RefundBean data) {
                        hideWaitingDialog();
                        if (mView != null && data != null && data.getStatus() == 1) {
                            mView.FetchvalidteTradePwdSuccess(data);
                        } else {
                            if (mView != null && data != null && data.getMsg() != null)
                                ToastUtils.showLongToast(data.getMsg());
                        }
                    }
                }));
    }
}
