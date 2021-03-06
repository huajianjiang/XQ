package com.biu.modulebase.binfenjiari.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.biu.modulebase.binfenjiari.R;
import com.biu.modulebase.binfenjiari.activity.EventDetailActivity;
import com.biu.modulebase.binfenjiari.activity.MapActivity;
import com.biu.modulebase.common.adapter.BaseAdapter;
import com.biu.modulebase.common.adapter.BaseViewHolder;
import com.biu.modulebase.binfenjiari.communication.Communications;
import com.biu.modulebase.binfenjiari.communication.ContextRequestCallBack;
import com.biu.modulebase.binfenjiari.communication.ImageDisplayUtil;
import com.biu.modulebase.binfenjiari.datastructs.Constant;
import com.biu.modulebase.binfenjiari.fragment.dialogFragment.ShareDialogFragment;
import com.biu.modulebase.binfenjiari.model.EventVO;
import com.biu.modulebase.binfenjiari.util.JSONUtil;
import com.biu.modulebase.binfenjiari.util.OtherUtil;
import com.biu.modulebase.binfenjiari.util.PreferencesUtils;
import com.biu.modulebase.binfenjiari.util.Utils;
import com.biu.modulebase.binfenjiari.widget.swiperefreshlayout.LSwipeRefreshLayout;
import com.biu.modulebase.common.base.BaseFragment;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lee
 * @Title: {标题}
 * @Description:{描述}
 * @date 2016/6/5
 */
public class SearchResultEventFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private LSwipeRefreshLayout mRefreshLayout;
    private BaseAdapter adapter;
    private long time;

    private int pageNum =1;
    private int allPageNumber;
    /**搜索参数**/
    private String title;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        ViewGroup rootView= (ViewGroup) inflater.inflate(R.layout.fragment_recyclerview_swiperefresh,
                container,false);
        return super.onCreateView(inflater, rootView, savedInstanceState);
    }

    @Override
    protected void getIntentData() {
        title =getActivity().getIntent().getStringExtra("title");
    }

    @Override
    protected void initView(View rootView) {
        visibleLoading();
        setHasOptionsMenu(true);
        mRefreshLayout= (LSwipeRefreshLayout) rootView.findViewById(R.id.swipe);
        mRefreshLayout.setSwipeRefreshListener(new LSwipeRefreshLayout.SwipeRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }

            @Override
            public void onLoadMore() {
                pageNum++;
                getEventList(title,null,null,null,Constant.LIST_LOAD_MORE);
            }
        });
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        adapter=new BaseAdapter(getActivity()) {
            @Override
            public BaseViewHolder getViewHolder(ViewGroup parent, final int viewType) {
                BaseViewHolder viewHolder = new BaseViewHolder(LayoutInflater.from(getActivity())
                        .inflate(R.layout.item_event_home, parent, false),
                        new BaseViewHolder.Callbacks2() {
                            @Override
                            public void bind(BaseViewHolder holder, Object data) {
//                                EventVO bean = (EventVO) data;
//                                holder.setNetImage(Constant.IMG_COMPRESS,R.id.banner,bean.getBanner_pic(), ImageDisplayUtil.DISPLAY_BIG_IMAGE);
//                                holder.setText(R.id.title,bean.getName());
//                                holder.setText(R.id.address,bean.getAddress());
//                                holder.setText(R.id.time,String.format(getString(R.string.list_event_time), Utils.sec2Date(bean.getOpen_time(),"yyyy-MM-dd"),Utils.hSec2Date(bean.getEnd_time(),"yyyy-MM-dd")));
//                                holder.setText(R.id.people_num,String.format(getString(R.string.event_list_people_num),bean.getApply_number(),bean.getLimit_number()));
//                                CheckBox likeBox = holder.getView(R.id.like);
//                                likeBox.setText(bean.getLike_number());
//                                if(bean.getLike_status().equals("1")){
//                                    likeBox.setChecked(true);
//                                }else{
//                                    likeBox.setChecked(false);
//                                }
//                                ImageView over =holder.getView(R.id.over);
//                                if(bean.getIsopen().equals("1")){//未結束
//                                    over.setVisibility(View.GONE);
//                                }else{
//                                    over.setVisibility(View.VISIBLE);
//                                }

                                EventVO bean = (EventVO) data;
                                holder.setNetImage(Constant.IMG_COMPRESS,R.id.banner,bean.getBanner_pic(), ImageDisplayUtil.DISPLAY_BIG_IMAGE);
                                holder.setText(R.id.title,bean.getName());
                                String address =bean.getAddress();
                                holder.setText(R.id.address,address.length()>16?address.substring(0,16)+"...":address);
                                holder.setText(R.id.time,String.format(getString(R.string.list_event_time), Utils.sec2Date(bean.getOpen_time(),"yyyy/MM/dd"),Utils.sec2Date(bean.getEnd_time(),"yyyy/MM/dd")));
                                holder.setText(R.id.base,String.format(getString(R.string.home_activity_address),bean.getBase_name()));
                                if(Utils.isInteger(bean.getLimit_number())>0){
                                    holder.setText(R.id.join_num,String.format(getString(R.string.event_list_people_num),bean.getApply_number(),bean.getLimit_number()));
                                }else{
                                    holder.setText(R.id.join_num,getString(R.string.home_activity_num2));
                                }
                            }

                            @Override
                            public void onItemClick(BaseViewHolder holder, View view, int position) {
//                                int i = view.getId();
//                                if (i == R.id.share) {
//                                    ShareDialogFragment shareDialog = ShareDialogFragment.newInstance(ShareDialogFragment.STYLE_NO_TITLE, R.layout.pop_share);
//                                    shareDialog.show(getActivity().getSupportFragmentManager(), "share");
//
//                                } else if (i == R.id.like) {
//                                    final EventVO bean1 = (EventVO) getData(position);
//                                    JSONObject params = new JSONObject();
//                                    try {
//                                        params.put("model", Constant.MODEL_ACTIVITY);
//                                        params.put("action", Constant.ACTION_ACTIVITY_LIKE);
//                                        params.put("id", bean1.getId());
//                                        params.put("token", PreferencesUtils.getString(getActivity(), PreferencesUtils.KEY_TOKEN));
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                    OtherUtil.like(SearchResultEventFragment.this, (CheckBox) view, params, new OtherUtil.LikeCallback() {
//                                        @Override
//                                        public void onFinished(int backKey) {
//                                            if (backKey != -1) {
//                                                bean1.setLike_status(backKey + "");
//                                                if (backKey == 1) {
//                                                    bean1.setLike_number(Utils.isInteger(bean1.getLike_number()) + 1 + "");
//                                                } else if (backKey == 2) {
//                                                    bean1.setLike_number(Utils.isInteger(bean1.getLike_number()) - 1 + "");
//                                                }
//                                            }
//                                        }
//                                    });
//
//                                } else {
//                                    EventVO bean = (EventVO) getData(position);
//                                    Intent intent = new Intent(getActivity(), EventDetailActivity.class);
//                                    intent.putExtra(Constant.KEY_ID, bean.getId());
//                                    intent.putExtra(Constant.KEY_POSITION, position);
//                                    startActivityForResult(intent, EVENT_LIKE_REQUEST);
//
//                                }


                                final EventVO bean = (EventVO) getData(position);
                                int i = view.getId();
                                if (i == R.id.share) {
                                    OtherUtil.showShareFragment(SearchResultEventFragment.this, bean.getId(), bean.getName(), bean.getRemark(), Constant.SHARE_ACTIVITY);

                                } else if (i == R.id.like) {//                                            likeEvent(position,bean1.getId());
                                    JSONObject params2 = OtherUtil.getJSONObject(getActivity(), Constant.MODEL_ACTIVITY,
                                            Constant.ACTION_ACTIVITY_LIKE, true);
                                    JSONUtil.put(params2, "id", bean.getId());
                                    OtherUtil.like(SearchResultEventFragment.this, (CheckBox) view, params2, new OtherUtil.LikeCallback() {
                                        @Override
                                        public void onFinished(int backKey) {
                                            if (backKey != -1) {
                                                bean.setLike_status(backKey + "");
                                                if (backKey == 1) {
                                                    bean.setLike_number(Utils.isInteger(bean.getLike_number()) + 1 + "");
                                                } else if (backKey == 2) {
                                                    bean.setLike_number(Utils.isInteger(bean.getLike_number()) - 1 + "");
                                                }
                                            }
                                        }
                                    });

                                } else if (i == R.id.address) {
                                    Intent mapIntent = new Intent(getActivity(), MapActivity
                                            .class);
                                    mapIntent.putExtra(Constant.KEY_LOCATION_LATITUDE, bean
                                            .getLatitude());
                                    mapIntent.putExtra(Constant.KEY_LOCATION_LONGITUDE, bean.getLongitude());
                                    mapIntent.putExtra(Constant.KEY_MAP_TARGET, bean.getAddress());
                                    startActivity(mapIntent);

                                } else {
                                    Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                                    intent.putExtra(Constant.KEY_ID, bean.getId());
                                    intent.putExtra(Constant.KEY_POSITION, position);
                                    startActivityForResult(intent, EVENT_LIKE_REQUEST);

                                }

                            }
                        });
                viewHolder.setItemChildViewClickListener(R.id.like,R.id.share);
                return viewHolder;

            }

        };
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(adapter.getItemDecoration());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
                        mRefreshLayout.startLoad();
                    }
                }
            }
        });
    }

    @Override
    public void loadData() {
        time = new Date().getTime()/1000;
        pageNum =1;
        getEventList(title,null,null,null,Constant.LIST_REFRESH);
    }

    /**
     *
     * @param title 活动标题 可选（模糊查询）
     * @param districtId 区域id 可选
     * @param timeOrder 活动开始时间排序 可选
     * @param moneyOrder 活动价格排序 可选
     * @param tag 请求标示（下拉刷新 or 上拉加载）
     */
    public void getEventList(String title,String districtId,String timeOrder,String moneyOrder,final int tag){
        JSONObject params = new JSONObject();
        try {
            params.put("model",Constant.MODEL_ACTIVITY);
            params.put("action",Constant.ACTION_ACTIVITY_HOME);
            params.put("time",time+"");
            params.put("pageNum",pageNum+"");
            params.put("token", PreferencesUtils.getString(getActivity(),PreferencesUtils.KEY_TOKEN));
            if(!Utils.isEmpty(title))
                params.put("title",title);
            if(!Utils.isEmpty(districtId))
                params.put("districtId",districtId);
            if(!Utils.isEmpty(timeOrder))
                params.put("timeOrder",timeOrder);
            if(!Utils.isEmpty(moneyOrder))
                params.put("moneyOrder",moneyOrder);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonRequest(false, params, Constant.SERVERURL, getClass().getSimpleName().toString(), new ContextRequestCallBack() {
            @Override
            public void onSuccess(String jsonString) {
                try {
                    JSONObject result =new JSONObject(jsonString);
                    time = JSONUtil.getLong(result,"time");
                    allPageNumber =JSONUtil.getInt(result,"allPageNumber");
                    JSONArray array =JSONUtil.getJSONArray(result,"activityList");
                    ArrayList<EventVO> list =JSONUtil.fromJson(array.toString(),new TypeToken<List<EventVO>>(){}.getType());
                    refreshList(tag, list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCodeError(int key, String message) {
                if(pageNum==1){
                    showTost("未搜索相关结果！",1);
                    getActivity().finish();
                }
                if(key==3){
                    refreshList(tag, new ArrayList<EventVO>());
                }else{
                    showTost(message,1);
                }

            }

            @Override
            public void onConnectError(String message) {
            }
        });
    }

    private void refreshList(int tag,ArrayList<EventVO> datas){
        BaseAdapter adapter = (BaseAdapter) mRecyclerView.getAdapter();
        switch (tag){
            case Constant.LIST_REFRESH:
                if(datas.size()==0) {
                    visibleNoData();
                    showTost("未搜索相关结果！",1);
                    getActivity().finish();
                    return;
                }
                adapter.setData(datas);
                mRefreshLayout.setRefreshing(false);
                break;
            case Constant.LIST_LOAD_MORE:
                adapter.addItems(datas);
                mRefreshLayout.setLoading(false);
                break;
        }
        if(pageNum<allPageNumber){
            mRefreshLayout.setMode(LSwipeRefreshLayout.Mode.BOTH);
        }else{
            mRefreshLayout.setMode(LSwipeRefreshLayout.Mode.PULL_FROM_START);
        }
    }

    private final static int EVENT_LIKE_REQUEST =111;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            switch (requestCode){
                case EVENT_LIKE_REQUEST:
                    BaseAdapter adapter = (BaseAdapter) mRecyclerView.getAdapter();
                    int position = data.getIntExtra("position",-1);
                    int backKey = data.getIntExtra("backKey",-1);
                    Object object =adapter.getData(position);
                    EventVO bean = (EventVO) object;
                    bean.setLike_status(backKey==1?"1":"2");
                    int likeNum = Utils.isInteger(bean.getLike_number());
                    bean.setLike_number(backKey==1?++likeNum +"":--likeNum+"" );
                    adapter.changeData(position,bean);
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        Communications.cancelRequest(getClass().getSimpleName());
        super.onDestroy();
    }

}
