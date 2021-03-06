package com.binfenjiari.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.binfenjiari.R;
import com.binfenjiari.activity.AvVideoDetailActivity;
import com.binfenjiari.utils.DataTest;
import com.binfenjiari.utils.DividerCommon8dpDecoration;
import com.binfenjiari.widget.FixedRecycleScrollView_AV;
import com.binfenjiari.widget.FixedRecycleScrollView_Navi_AV;
import com.binfenjiari.widget.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import com.biu.modulebase.binfenjiari.widget.RefreshRecyclerView;
import com.biu.modulebase.common.adapter.BaseAdapter;
import com.biu.modulebase.common.adapter.BaseViewHolder;
import com.biu.modulebase.common.base.BaseFragment;

/**
 * @author tangjin
 * @Title: {标题}
 * @Description:{描述}
 * @date 2017/6/8
 */
public class NaviHomeVideoAudioFragment extends BaseFragment {

    private RefreshRecyclerView mRefreshRecyclerView;
    private RecyclerView mRecyclerView;
    private int mPage;

    public static NaviHomeVideoAudioFragment newInstance() {
        NaviHomeVideoAudioFragment fragment = new NaviHomeVideoAudioFragment();
        return fragment;
    }
    //

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_common_refreshrecyclerview, container, false);
        return super.onCreateView(inflater, rootView, savedInstanceState);
    }


    @Override
    protected void initView(View rootView) {

//        setHasOptionsMenu(true);
//        searchEditText = (SearchEditText) getBaseActivity().setToolBarCustomView(com.biu.modulebase.binfenjiari.R.layout.search_editview);
//        searchEditText.requestFocus();

        mRefreshRecyclerView = (RefreshRecyclerView) rootView.findViewById(R.id.rrv_refresh_recycleview);
        mRecyclerView = mRefreshRecyclerView.getRecyclerView();

        mRefreshRecyclerView.setRefreshAction(new RefreshRecyclerView.Action() {

            @Override
            public void onAction(int page) {
                mPage = page;
                refreshData();

            }
        });
        mRefreshRecyclerView.setLoadMoreAction(new RefreshRecyclerView.Action() {

            @Override
            public void onAction(int page) {
                mPage = page;
                loadMoreData();

            }
        });

        BaseAdapter<String> adapter = new BaseAdapter<String>(getActivity()) {

            @Override
            public BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
                BaseViewHolder holder = null;

                if (viewType == 0) {
                    holder = new BaseViewHolder(LayoutInflater.from(getActivity())
                            .inflate(R.layout.item_navi_home_fixedrecyclescrollview_navi_video, parent, false),
                            new BaseViewHolder.Callbacks2() {
                                @Override
                                public void bind(BaseViewHolder holder, Object data) {
                                    FixedRecycleScrollView_Navi_AV view = (FixedRecycleScrollView_Navi_AV) holder.itemView;
                                    view.setLinearLayoutData();
                                }

                                @Override
                                public void onItemClick(BaseViewHolder holder, View view, int position) {


                                }
                            });

                    holder.setItemChildViewClickListener(R.id.like, R.id.share, R.id.tv_location);
                } else {
                    holder = new BaseViewHolder(LayoutInflater.from(getActivity())
                            .inflate(R.layout.item_mine_publish_av, parent, false),
                            new BaseViewHolder.Callbacks2() {
                                @Override
                                public void bind(BaseViewHolder holder, Object data) {
//                                    mBannerSliderView = (BannerSliderView) holder.itemView;

                                }

                                @Override
                                public void onItemClick(BaseViewHolder holder, View view,
                                        int position)
                                {
                                    Intent detail =
                                            new Intent(getContext(), AvVideoDetailActivity.class);
                                    startActivity(detail);

                                }
                            });
                }

                holder.setItemChildViewClickListener(R.id.like, R.id.share, R.id.tv_location);

                return holder;
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }

        };

        mRecyclerView.setAdapter(adapter);
        DividerCommon8dpDecoration dividerDecoration = new DividerCommon8dpDecoration(getContext());
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this.getContext())
                .paintProvider(dividerDecoration)
                .visibilityProvider(dividerDecoration)
                .marginProvider(dividerDecoration)
                .build());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @Override
    public void loadData() {
        mRefreshRecyclerView.showSwipeRefresh();
    }

    private void refreshData() {
        visibleLoading();
        mRefreshRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                inVisibleLoading();
                DataTest.load(mRefreshRecyclerView, mPage);
            }
        }, 500);
    }

    private void loadMoreData() {
        mRefreshRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                DataTest.load(mRefreshRecyclerView, mPage);
            }
        }, 1000);
    }


}
