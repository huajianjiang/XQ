package com.binfenjiari.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binfenjiari.R;
import com.binfenjiari.activity.CommentDetailActivity;
import com.binfenjiari.utils.DataTest;
import com.binfenjiari.utils.SoftKeyboardStateChangeListener;
import com.binfenjiari.widget.recyclerviewflexibledivider.FlexibleDividerDecoration;
import com.binfenjiari.widget.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.biu.modulebase.binfenjiari.util.Utils;
import com.biu.modulebase.binfenjiari.widget.RefreshRecyclerView;
import com.biu.modulebase.common.adapter.BaseAdapter;
import com.biu.modulebase.common.adapter.BaseViewHolder;
import com.biu.modulebase.common.base.BaseFragment;

/**
 * @author tangjin
 * @Title: {小记者话题}
 * @Description:{描述}
 * @date 2017/6/8
 */
public class YoungReporterTopicFragment extends BaseFragment {

    private View action_part;
    private TextView action_post;

    private RefreshRecyclerView mRefreshRecyclerView;
    private RecyclerView mRecyclerView;
    private int mPage;

    public static YoungReporterTopicFragment newInstance() {
        YoungReporterTopicFragment fragment = new YoungReporterTopicFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_young_reporter_topic, container, false);
        return super.onCreateView(inflater, rootView, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //软键盘监听器，用于切换 UI
        SoftKeyboardStateChangeListener mSoftStateListener = new SoftKeyboardStateChangeListener(view);
        view.getViewTreeObserver().addOnGlobalLayoutListener(mSoftStateListener);
        mSoftStateListener.setListener(
                new SoftKeyboardStateChangeListener.OnSoftKeyboardStateChangeListener() {
                    @Override
                    public void onShow() {
                        hideActionPartView();
                        showPostView();
                    }

                    @Override
                    public void onHide() {
                        showActionPartView();
                        hidePostView();
//                        reset();
                    }
                });
    }

    private void showPostView() {
        action_post.setVisibility(View.VISIBLE);
    }

    private void showActionPartView() {
        action_part.setVisibility(View.VISIBLE);
    }

    private void hidePostView() {
        action_post.setVisibility(View.GONE);
    }

    private void hideActionPartView() {
        action_part.setVisibility(View.GONE);
    }

    @Override
    protected void initView(View rootView) {

        action_part = rootView.findViewById(R.id.action_like);
        action_post = (TextView) rootView.findViewById(R.id.action_post);

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
                            .inflate(R.layout.item_young_reporter_topic_head, parent, false),
                            new BaseViewHolder.Callbacks2() {
                                FrameLayout fl_button_yes_no;
                                LinearLayout ll_progress_yes, ll_progress_no;

                                @Override
                                public void bind(BaseViewHolder holder, Object data) {
                                    fl_button_yes_no = holder.getView(R.id.fl_button_yes_no);
                                    ll_progress_yes = holder.getView(R.id.ll_progress_yes);
                                    ll_progress_yes.setVisibility(View.GONE);
                                    ll_progress_no = holder.getView(R.id.ll_progress_no);
                                    ll_progress_no.setVisibility(View.GONE);

                                }

                                @Override
                                public void onItemClick(BaseViewHolder holder, View view, int position) {
                                    if (view.getId() == R.id.tv_yes) {
                                        fl_button_yes_no.setVisibility(View.GONE);
                                        ll_progress_yes.setVisibility(View.VISIBLE);
                                        ll_progress_no.setVisibility(View.VISIBLE);
                                    }

                                }
                            });

                    holder.setItemChildViewClickListener(R.id.tv_yes, R.id.tv_no);
                } else {
                    holder = new BaseViewHolder(LayoutInflater.from(getActivity())
                            .inflate(R.layout.item_general_comment_parent, parent, false),
                            new BaseViewHolder.Callbacks2() {
                                @Override
                                public void bind(BaseViewHolder holder, Object data) {


                                }

                                @Override
                                public void onItemClick(BaseViewHolder holder, View view, int position) {

                                    if (view.getId() == R.id.ll_add_comment) {
                                        LinearLayout ll_add_comment = holder.getView(R.id.ll_add_comment);
                                        TextView tv = (TextView) View.inflate(getContext(), R.layout.widget_textview_comment_reply, null);
                                        ll_add_comment.addView(tv);
                                    } else {
                                        CommentDetailActivity.beginActivity(getContext());
                                    }


                                }
                            });

                    holder.setItemChildViewClickListener(R.id.ll_add_comment, R.id.share, R.id.tv_location);
                }

                return holder;
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }

        };

        mRecyclerView.setAdapter(adapter);
        DividerCommonDecoration dividerDecoration = new DividerCommonDecoration(getContext());
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

    public class DividerCommonDecoration implements
            FlexibleDividerDecoration.PaintProvider,
            FlexibleDividerDecoration.VisibilityProvider,
            HorizontalDividerItemDecoration.MarginProvider {

        private Context mContext;

        public DividerCommonDecoration(Context context) {
            this.mContext = context;
        }

        public Context getContext() {
            return mContext;
        }

        @Override
        public int dividerLeftMargin(int position, RecyclerView parent) {
//        if (position > 7) {
            return Utils.dp2px(getContext(), 15);
//        }
//        return 0;
        }

        @Override
        public int dividerRightMargin(int position, RecyclerView parent) {
//        if (position > 7) {
            return Utils.dp2px(getContext(), 15);
//        }
//        return 0;
        }

        @Override
        public boolean shouldHideDivider(int position, RecyclerView parent) {
            if (position == 0)
                return true;
            return false;
        }

        @Override
        public Paint dividerPaint(int position, RecyclerView parent) {
            Paint paint = new Paint();
//        if (position >= 1 && position <= 6) {
//            Resources resource = (Resources) getContext().getResources();
//            ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.colorDividerLight);
//            paint.setColor(csl.getDefaultColor());
//            paint.setStrokeWidth(Utils.dp2px(getContext(), 8));
//            return paint;
//
//        }
//
//        if (position > 7) {
            Resources resource = (Resources) getContext().getResources();
            ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.colorAppBackground);
            paint.setColor(csl.getDefaultColor());
            paint.setStrokeWidth(Utils.dp2px(getContext(), 1));
            return paint;
//        }
//        paint.setStrokeWidth(0);
//        return paint;
        }

    }


}
