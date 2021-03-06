package com.biu.modulebase.binfenjiari.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biu.modulebase.binfenjiari.R;
import com.biu.modulebase.binfenjiari.activity.CommVoteDetailNewActivity;
import com.biu.modulebase.binfenjiari.activity.WebViewVoteDetailActivity;
import com.biu.modulebase.common.adapter.BaseAdapter;
import com.biu.modulebase.common.adapter.BaseViewHolder;
import com.biu.modulebase.binfenjiari.communication.Communications;
import com.biu.modulebase.binfenjiari.communication.ContextRequestCallBack;
import com.biu.modulebase.binfenjiari.communication.ImageDisplayUtil;
import com.biu.modulebase.binfenjiari.datastructs.Constant;
import com.biu.modulebase.binfenjiari.datastructs.MyApplication;
import com.biu.modulebase.binfenjiari.eventbus.VoteNextEvent;
import com.biu.modulebase.binfenjiari.model.NewVoteBeanVO;
import com.biu.modulebase.binfenjiari.model.VoteNewProjectVO;
import com.biu.modulebase.binfenjiari.model.VoteProjectVO;
import com.biu.modulebase.binfenjiari.other.pop.PopSearchVoteList;
import com.biu.modulebase.binfenjiari.util.JSONUtil;
import com.biu.modulebase.binfenjiari.util.LogUtil;
import com.biu.modulebase.binfenjiari.util.OtherUtil;
import com.biu.modulebase.binfenjiari.util.PreferencesUtils;
import com.biu.modulebase.binfenjiari.util.Utils;
import com.biu.modulebase.binfenjiari.widget.SearchEditText;
import com.biu.modulebase.binfenjiari.widget.swiperefreshlayout.LSwipeRefreshLayout;
import com.biu.modulebase.common.base.BaseFragment;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by jhj_Plus on 2016/4/18.
 */
public class CommVoteDetailNewImageFragment<E> extends BaseFragment {
    private static final String TAG = "CommVoteDetailNewImageFragment";

    private static final int SEARCH_VOTE_AND_DO_VOTE =111;

    private LSwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private SearchEditText searchEditText;
    private String id;
    private int mposition;
    private NewVoteBeanVO mNewVoteBeanVO;

//    private VoteVO voteVO;
    private long time;
    private int allPageNumber =1;
    private int pageNum;
    /**搜索关键词**/
    private String title;
    int flag=0;

    private TextView fab_vote;
    List index=new LinkedList();

    private HashMap<String,String> voteMap =new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            id = args.getString(Constant.KEY_ID);
            mposition = args.getInt(Constant.KEY_POSITION);
            if(CommVoteDetailNewActivity.sNewVoteBeanVOList!=null) {
                mNewVoteBeanVO = CommVoteDetailNewActivity.sNewVoteBeanVOList.get(mposition);
            }
        }
    }

    public static CommVoteDetailNewImageFragment newInstance(int position,String id) {
        Bundle args = new Bundle();
        args.putInt(Constant.KEY_POSITION,position);
        args.putString(Constant.KEY_ID,id);
        CommVoteDetailNewImageFragment fragment = new CommVoteDetailNewImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ViewGroup mFilterLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.header_vote_detail_new_image, container, false);
        return super.onCreateView(inflater, rootView, savedInstanceState);
    }

    @Override
    protected void initView(View rootView) {
        visibleLoading();
//        searchEditText = (SearchEditText) getBaseActivity().setToolBarCustomView(R.layout.search_editview);
//        searchEditText.setTextHint("搜索投票项...");
//        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    title =searchEditText.getText().toString();
//                    if(Utils.isEmpty(title)){
//                        showTost("请输入搜索关键词",0);
//                    }else{
//                        /* 隐藏软键盘 */
//                        showSoftKeyboard();
//                        Intent intent = new Intent(getActivity(), SearchResultVoteActivity.class);
//                        intent.putExtra(Constant.KEY_ID,id);
//                        intent.putExtra("title",title);
//                        intent.putExtra("voteVO",voteVO);
//                        startActivityForResult(intent ,SEARCH_VOTE_AND_DO_VOTE);
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });
        mFilterLayout = (ViewGroup)rootView.findViewById(R.id.ll_pop);

        fab_vote =(TextView)rootView.findViewById(R.id.fab_vote);
        fab_vote.setOnClickListener(this);
        if(CommVoteDetailNewActivity.sNewVoteBeanVOList!=null) {
            mNewVoteBeanVO = CommVoteDetailNewActivity.sNewVoteBeanVOList.get(mposition);
            if(mposition==CommVoteDetailNewActivity.sNewVoteBeanVOList.size()-1){
                fab_vote.setText("提交");
            }
            getBaseActivity().setToolBarTitle(mNewVoteBeanVO.getTitle());
        }
        mRefreshLayout= (LSwipeRefreshLayout) rootView.findViewById(R.id.swipe);
        mRefreshLayout.requestFocus();
        mRefreshLayout.setSwipeRefreshListener(new LSwipeRefreshLayout.SwipeRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }

            @Override
            public void onLoadMore() {
                pageNum++;
                getProjects(Constant.LIST_LOAD_MORE);
            }
        });
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setBackgroundResource(R.color.white);

        final int width = (Utils.getScreenWidth(getActivity()) - 2 * getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin) - getResources().getDimensionPixelSize(R.dimen.view_margin_12dp) * 2) / 3;

        BaseAdapter adapter = new BaseAdapter(getActivity()) {
            private static final int TYPE_HEADER = 0X0001;//头部
            private static final int TYPE_BODY = 0X0002;
            @Override
            public BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
                BaseViewHolder viewHolder = null;
                if(viewType ==TYPE_HEADER){
                    viewHolder = new BaseViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_vote_img_new_header, parent, false), new BaseViewHolder.Callbacks2() {
                        @Override
                        public void bind(BaseViewHolder holder, Object data) {
                            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                            p.setFullSpan(true);
//                            final NewVoteBeanVO bean = (NewVoteBeanVO) data;
//                            holder.setText(R.id.tv_nickname,mNewVoteBeanVO.getTitle());

                            ImageView imageView = holder.getView(R.id.img_banner);
                            int[] widthHeight =  ImageDisplayUtil.getWidthHeigh(Utils.getScreenWidth(getActivity()),mNewVoteBeanVO.getBanner_pic());
                            FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) imageView.getLayoutParams();
                            fl.width = widthHeight[0];
                            fl.height = widthHeight[1];
                            imageView.setLayoutParams(fl);


                            holder.setNetImage(Constant.IMG_SOURCE,R.id.img_banner,mNewVoteBeanVO.getBanner_pic(),ImageDisplayUtil.DISPLAY_BIG_IMAGE);

//                            ImageView header = holder.getView(R.id.iv_head_portrait);
//                            header.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent =new Intent(getActivity(),PersonalInfoActivity.class);
//                                    intent.putExtra(Constant.KEY_ID,bean.getUser_id());
//                                    startActivity(intent);
//                                }
//                            });
//                            int day_type=bean.getDay_type();
//                            if(day_type ==1){
//                                holder.setText(R.id.rule,String.format(getString(R.string.vote_rule1),bean.getMax_number()+""));
//                            }else{
//                                holder.setText(R.id.rule,String.format(getString(R.string.vote_rule2),bean.getMax_number()+""));
//                            }
                            holder.setText(R.id.rule, mNewVoteBeanVO.getRule());
                            long extra =Utils.isLong(mNewVoteBeanVO.getEnd_time())*1000-new Date().getTime();
                            String timeStr ="";
                            try {
                                timeStr=  Utils.getDistanceTimes(Utils.getCurrentDate2(),Utils.sec2Date(mNewVoteBeanVO.getEnd_time(),"yyyy-MM-dd HH:mm:ss"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if(extra>0){
                                holder.setText(R.id.extra_day,String.format(getString(R.string.extra_day),timeStr));
                            }else{
                                holder.setText(R.id.extra_day,"投票已结束");
                            }
//                            holder.setText(R.id.tv_date, Utils.getReleaseTime(new Date(Utils.isLong(bean.getCreate_time())*1000)));
//                            holder.setText(R.id.tv_content,bean.getTitle());
                        }

                        @Override
                        public void onItemClick(BaseViewHolder holder, View view, int position) {

                        }
                    });
                }else if(viewType ==TYPE_BODY){
                    //part_image_vote_new_detail_twostyle  part_image_vote_new_detail
                    viewHolder = new BaseViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.part_image_vote_new_detail_twostyle, parent, false), new BaseViewHolder.Callbacks2() {
                        @Override
                        public void bind(BaseViewHolder holder, Object data) {
                            VoteNewProjectVO bean = (VoteNewProjectVO) data;

                            FrameLayout fl = holder.getView(R.id.fl_vote_image);
                            LinearLayout.LayoutParams layoutParams = null;
                            layoutParams = (LinearLayout.LayoutParams) fl.getLayoutParams();
                            layoutParams.width = width;
                            layoutParams.height = width;
                            fl.setLayoutParams(layoutParams);

                            ImageView imageView = holder.getView(R.id.vote_pic);
                            ImageDisplayUtil.LoadCircleImg(getContext(), Constant.IMG_COMPRESS, bean.getPic(), imageView);

//                            holder.setNetImage(Constant.IMG_COMPRESS,R.id.vote_pic,bean.getPic(),ImageDisplayUtil.DISPLAY_COMMON_IMAGE);
                            holder.setText(R.id.name,String.format("%s%s","",bean.getSmall_title()));//bean.getCreate_number()
                            holder.setText(R.id.num, bean.getNumber()+"");

                            Utils.setSexIconState(getContext(),(TextView)(holder.getView(R.id.name)),bean.getSex());

//                            int number =bean.getNumber();
//                            holder.setText(R.id.vote_num,String.format(getString(R.string.voted_num2),number+""));
//                            ProgressBar progress =holder.getView(R.id.progress);
//                            progress.setMax(Utils.isInteger(voteVO.getAll_poll()));
//                            progress.setProgress(number);
//                            holder.setText(R.id.percent, Utils.getPencent(Utils.isInteger(number),Utils.isInteger(voteVO.getAll_poll()))+"%");
                            CheckBox voteCheckBox =holder.getView(R.id.voteCheckBox);

                            if(bean.getIs_able()==1) {
                                //可选
                                voteCheckBox.setVisibility(View.VISIBLE);
                                if (voteCheckBox == null)
                                    return;
                                voteCheckBox.setOnCheckedChangeListener(null);
                                if (voteMap.containsKey(bean.getId())) {
                                    voteCheckBox.setChecked(true);
                                } else {
                                    voteCheckBox.setChecked(false);
                                }
                                voteCheckBox.setOnCheckedChangeListener(new OnVoteCheckChangeListener(holder.getAdapterPosition(), bean.getId()));
                            }else{
                                voteCheckBox.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onItemClick(BaseViewHolder holder, View view, int position) {
                            VoteNewProjectVO bean = (VoteNewProjectVO)getData().get(position);
                            int i = view.getId();
                            if (i == R.id.fl_vote_image) {
                                if (bean.getIs_able() == 0) {
                                    showTost("数据审核中", 0);
                                }

//                                case R.id.voteCheckBox:
//                                    CheckBox checkBox = holder.getView(R.id.voteCheckBox);
//                                    if(checkBox.isChecked()){
//                                        checkBox.setChecked(false);
//                                    }else{
//                                        checkBox.setChecked(true);
//                                    }
//                                    break;
                            } else {
                                Intent intent = new Intent(getActivity(), WebViewVoteDetailActivity.class);
                                intent.putExtra("type", WebViewVoteDetailFragment.LOAD_TYPE_APP_FINDVOTEPROJECTINFO);
                                VoteNewProjectVO vo = (VoteNewProjectVO) getData().get(position);
                                String id1 = vo.getId();
                                intent.putExtra("type1", 2);
                                intent.putExtra("id", id1);
                                intent.putExtra("title1", mNewVoteBeanVO.getTitle());
                                startActivity(intent);


                            }

                        }
                    });
                    viewHolder.setItemChildViewClickListener(R.id.fl_vote_image);
                }
                return viewHolder;
            }

            @Override
            public int getItemViewType(int position) {
                if(position == 0){
                    return TYPE_HEADER;
                }else{
                    return TYPE_BODY;
                }
            }
            @Override
            protected void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                          RecyclerView.State state)
            {
                int childAdapterPosition = parent.getChildAdapterPosition(view);
                if(childAdapterPosition==0){
                    outRect.set(0,0,0,0);
                }else{
                    outRect.set(0,getResources().getDimensionPixelSize(R.dimen.item_divider_height_2dp), 0,
                            0);
                }

            }
        };
        StaggeredGridLayoutManager manager =new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
//        GridLayoutManager manager = new GridLayoutManager(getActivity(),2);
//        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                if(position==0){
//                    return 2;
//                }else{
//                    return 1;
//                }
//            }
//        });
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(adapter.getItemDecoration());
        mRecyclerView.setAdapter(adapter);

    }


    @Override
    public void loadData() {
        time =new Date().getTime()/1000;
        pageNum =1;
        getVoteDetail();
    }

    private void getVoteDetail(){
        if(TextUtils.isEmpty(id))
            return;
        if(mNewVoteBeanVO!=null){
            inVisibleLoading();
            setHasOptionsMenu(true);
            ArrayList<E> datas = new ArrayList<>();
            datas.add((E) mNewVoteBeanVO);
            BaseAdapter adapter = (BaseAdapter) mRecyclerView.getAdapter();
            adapter.setData(datas);
            getProjects(Constant.LIST_REFRESH);
        }

//        JSONObject params =new JSONObject();
//        JSONUtil.put(params,"model","NewVote");
//        JSONUtil.put(params,"action","app_findVoteProjectList");
//        JSONUtil.put(params,"id",id);
//        JSONUtil.put(params,"token", PreferencesUtils.getString(getActivity(),PreferencesUtils.KEY_TOKEN));
//        jsonRequest(false, params, Constant.SERVERURL, getClass().getSimpleName(), new ContextRequestCallBack() {
//            @Override
//            public void onSuccess(String jsonString) {
//                inVisibleLoading();
//                setHasOptionsMenu(true);
//                voteVO = JSONUtil.fromJson(jsonString, VoteVO.class);
//                ArrayList<E> datas = new ArrayList<>();
//                datas.add((E) voteVO);
//                BaseAdapter adapter = (BaseAdapter) mRecyclerView.getAdapter();
//                adapter.setData(datas);
//                //设置总票数
//                fab_vote.setText(String.format(getString(R.string.voted_num),voteVO.getAll_poll()+""));
//                getProjects(Constant.LIST_REFRESH);
//           }
//
//            @Override
//            public void onCodeError(int key, String message) {
//                if(key ==3) {
//                    visibleNoData();
//                }else{
//                    showTost(message,1);
//                }
//            }
//
//            @Override
//            public void onConnectError(String message) {
//                visibleNoNetWork();
//            }
//        });

    }

    private void getProjects(final int tag){
        JSONObject params =new JSONObject();
        JSONUtil.put(params,"model","NewVote");
        JSONUtil.put(params,"action","app_findVoteProjectList");
        JSONUtil.put(params,"id",id);
        JSONUtil.put(params,"time",time);
        JSONUtil.put(params,"pageNum",pageNum);
        jsonRequest(false, params, Constant.SERVERURL, getClass().getSimpleName(), new ContextRequestCallBack() {
            @Override
            public void onSuccess(String jsonString) {
                try {
                    JSONObject result = new JSONObject(jsonString);
                    time = JSONUtil.getLong(result, "time");
                    allPageNumber = JSONUtil.getInt(result, "allPageNumber");
                    JSONObject object = new JSONObject(jsonString);
                    JSONArray array =JSONUtil.getJSONArray(object,"projectList");
                    List<VoteNewProjectVO> projectVOList = JSONUtil.fromJson(array.toString(),new TypeToken<List<VoteNewProjectVO>>(){}.getType());
                    List<E> datas = new ArrayList<>();
                    datas.addAll((Collection<? extends E>) projectVOList);
                    refreshList(tag, datas);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCodeError(int key, String message) {
                if(key ==3) {
                    visibleNoData();
                }else{
                    showTost(message,1);
                }
            }

            @Override
            public void onConnectError(String message) {
                visibleNoNetWork();
            }
        });

    }

    private <T> void refreshList(int tag,List<T> datas){
        BaseAdapter adapter = (BaseAdapter) mRecyclerView.getAdapter();
        switch (tag){
            case Constant.LIST_REFRESH:
                if(datas.size()==0) {
                    showTost("没有最新数据...",1);
                    return;
                }
                adapter.addData(1,datas);
//                adapter.setData(datas);
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

    private void doVote(){
        showProgress(getClass().getSimpleName());
        StringBuilder projectIdBuilder =new StringBuilder();
        Collection values=voteMap.values();
        Iterator it =values.iterator();
        while (it.hasNext()) {
            projectIdBuilder.append(it.next()).append(",");
        }
        String projectId =projectIdBuilder.substring(0,projectIdBuilder.length()-1);
        JSONObject params =new JSONObject();
        JSONUtil.put(params,"model",Constant.MODEL_VOTE);
        JSONUtil.put(params,"action",Constant.ACTION_DO_VOTE);
        JSONUtil.put(params,"projectId",projectId);
        JSONUtil.put(params,"id",getArguments().getString(Constant.KEY_ID));
        JSONUtil.put(params,"token", PreferencesUtils.getString(getActivity(),PreferencesUtils.KEY_TOKEN));
        jsonRequest(false, params, Constant.SERVERURL, getClass().getSimpleName(), new ContextRequestCallBack() {
            @Override
            public void onSuccess(String jsonString) {
                dismissProgress();
                int size =voteMap.size();
//                voteVO.setAll_poll(voteVO.getAll_poll()+size);
//                if(voteVO.getDay_type() ==1){
//                    voteVO.setSurplus_number(0);
//                }else{
//                    voteVO.setSurplus_number(voteVO.getSurplus_number()-size);
//                }
//                fab_vote.setText(String.format(getString(R.string.voted_num),voteVO.getAll_poll()+""));
                //更新adapter数据源
                BaseAdapter adapter = (BaseAdapter) mRecyclerView.getAdapter();
                Set keys =voteMap.keySet();
                Iterator it = keys.iterator();
                while (it.hasNext()){
                    int position = (int) it.next();
                    VoteProjectVO voteProjectVO = (VoteProjectVO) adapter.getData(position);
                    voteProjectVO.setNumber(voteProjectVO.getNumber()+1);
                    voteProjectVO.setChecked(false);
                    adapter.changeData(position,voteProjectVO);
                }

                voteMap.clear();
                showTost("投票成功",1);

            }

            @Override
            public void onCodeError(int key, String message) {
                dismissProgress();
                if(key ==10){//投票次数已完，不能进行投票
                    showTost(message,1);
                }else if(key==11){//当天投票次数已完，不能进行投票
                    showTost(message,1);
                }else{
                    showTost(message,1);
                }

            }

            @Override
            public void onConnectError(String message) {
                dismissProgress();
                visibleNoNetWork();
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate( R.menu.share, menu);
        inflater.inflate(R.menu.more_search, menu);

//        loadSearchMenu(menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    SearchView mSearchView;
    /**
     * 加载搜索菜单
     */
    public void loadSearchMenu(Menu menu){
        final MenuItem item = menu.findItem(R.id.action_search);

        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        SearchView.SearchAutoComplete mEdit = (SearchView.SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);
        final float density = getResources().getDisplayMetrics().density;
//        mSearchView.setIconified(false);
//        mSearchView.setIconifiedByDefault(false);
        final int closeImgId = getResources().getIdentifier("search_close_btn", "id", getActivity().getPackageName());
        ImageView closeImg = (ImageView) mSearchView.findViewById(closeImgId);
        if (closeImg != null) {
            LinearLayout.LayoutParams paramsImg = (LinearLayout.LayoutParams) closeImg.getLayoutParams();
            paramsImg.topMargin = (int) (-2 * density);
//            paramsImg.rightMargin =(int)(16* density);
            closeImg.setImageResource(R.mipmap.close1);
            closeImg.setLayoutParams(paramsImg);
        }
        final int editViewId = getResources().getIdentifier("search_src_text", "id", getActivity().getPackageName());
        mEdit = (SearchView.SearchAutoComplete) mSearchView.findViewById(editViewId);
        if (mEdit != null) {
            mEdit.setPadding(4,4,4,4);
            mEdit.setHintTextColor(getResources().getColor(R.color.colorTextGray));
            mEdit.setTextColor(getResources().getColor(R.color.colorTextBlack));
            mEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mEdit.setHint("搜索...");
        }
        LinearLayout rootView = (LinearLayout) mSearchView.findViewById(R.id.search_bar);
        rootView.setClickable(true);
        LinearLayout editLayout = (LinearLayout) mSearchView.findViewById(R.id.search_plate);
//        editLayout.setBackgroundResource(R.mipmap.edit_bg);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) editLayout.getLayoutParams();
        LinearLayout tipLayout = (LinearLayout) mSearchView.findViewById(R.id.search_edit_frame);
        LinearLayout.LayoutParams tipParams = (LinearLayout.LayoutParams) tipLayout.getLayoutParams();
        tipParams.leftMargin = 0;
        tipParams.rightMargin = 0;
        tipLayout.setLayoutParams(tipParams);
        ImageView icTip = (ImageView) mSearchView.findViewById(R.id.search_mag_icon);
        icTip.setImageResource(R.mipmap.search);
        params.topMargin = (int) (4 * density);
        params.bottomMargin = (int) (4 * density);
        params.rightMargin=(int)(16* density);
        editLayout.setLayoutParams(params);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().setToolBarTitle("");
            }

        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getBaseActivity().setToolBarTitle(mNewVoteBeanVO==null?"投票详情":mNewVoteBeanVO.getTitle());
                return false;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                showTypePop(getBaseActivity().getToolbar());
                showVotesFilter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_more) {
            OtherUtil.showMoreOperate(CommVoteDetailNewImageFragment.this, id, mNewVoteBeanVO.getTitle(), CommVoteDetailNewActivity.project_title, null,
                    Constant.SHARE_VOTE_PROJECT_INFO, -1, "-1",
                    false, null, -1, null);

        } else if (i == R.id.action_search) {

        } else {
        }
        return super.onOptionsItemSelected(item);
    }

    String areaArray[] = {"综合排序","时间从近到远","时间从远到近"};
    private void showVotesFilter(String title){

//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.getContext(), R.layout.item_menu2,R.id.tv,areaArray);
//        OtherUtil.showPopupWin(this.getContext(), mFilterLayout, adapter, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
//                1, new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    }
//                });

        PopSearchVoteList pop = new PopSearchVoteList(this, mFilterLayout,title, id, mNewVoteBeanVO, voteMap, getClass().getSimpleName());
        pop.setOnPopSelectListener(new PopSearchVoteList.OnPopOperatorListener(){



            @Override
            public void popSelect(boolean isCheck, String str) {
                updateVoteState(isCheck,str);
            }

            @Override
            public void popDismiss() {
                hideSoftKeyboard();
                if(mSearchView!=null)
                mSearchView.onActionViewCollapsed();
                getBaseActivity().setToolBarTitle(mNewVoteBeanVO==null?"投票详情":mNewVoteBeanVO.getTitle());

            }

            @Override
            public void popSelectFull(String id) {
                selectUpdateCancleFirst(id);
            }
        });
        pop.show();
    }

    /**
     * 更改id
     * @param isCheck
     * @param id
     */
    public void updateVoteState(boolean isCheck, String id){
        BaseAdapter baseAdapter = (BaseAdapter)(mRecyclerView.getAdapter());
        ArrayList<E> datas = (ArrayList<E>) baseAdapter.getData();
        for(int i=0;i<datas.size();i++){
            E data = datas.get(i);
            if(data instanceof VoteNewProjectVO){
                if(((VoteNewProjectVO) data).getId().equals(id)){
                    ((VoteNewProjectVO) data).setChecked(isCheck);
                    baseAdapter.changeData(i,data);
                    break;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            switch (requestCode){
                case SEARCH_VOTE_AND_DO_VOTE://更改本地可投票数
                    loadData();
                    LogUtil.LogD("SEARCH_VOTE_AND_DO_VOTE");
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.fab_vote) {
            if (voteMap.size() < mNewVoteBeanVO.getMin_number()) {
                showTost("最小投票数" + mNewVoteBeanVO.getMin_number() + "票", 0);
                return;
            }
            if (voteMap.size() > mNewVoteBeanVO.getMax_number()) {
                showTost("已达最大投票数" + mNewVoteBeanVO.getMax_number() + "票", 0);
                return;
            }
            if (voteMap.size() == 0) {
                showTost("请选择投票...", 0);
                return;
            }
            StringBuffer sb = new StringBuffer();
            sb.append(mNewVoteBeanVO.getId()).append("@");
            for (String id : voteMap.values()) {
                sb.append(id).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            ((CommVoteDetailNewActivity) getActivity()).onVoteNextEvent(new VoteNextEvent(sb.toString(), mposition));

//                if(Utils.isEmpty(PreferencesUtils.getString(getActivity(), PreferencesUtils.KEY_TOKEN))|| MyApplication.getUserInfo(getActivity())==null) {
//                    showUnLoginSnackbar();
//                    return;
//                }
//                if(voteVO.getIsopen().equals("1")){//未结束
//                    if(voteVO.getSurplus_number()>0){
//                        int voteSize =voteMap.size();
//                        if(voteSize==0){
//                            showTost("请先选择投票项...",0);
//                        }else if(voteSize>voteVO.getSurplus_number()){
//                            showTost("已超过最大投票额...",0);
//                        }else{
//                            doVote();
//                        }
//                    }else{
//                        showTost("投票次数已用完...",0);
//                    }
//                }else{
//                    showTost("投票已结束...",0);
//                }

        }
    }

    @Override
    public void onDestroyView() {
        Communications.cancelRequest(getClass().getSimpleName());
        super.onDestroyView();
    }

    class OnVoteCheckChangeListener implements OnCheckedChangeListener {

        private int position;
       /**投票项id**/
       private String id;
       public OnVoteCheckChangeListener(int position,String id){
           this.position =position;
           this.id =id;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            showAreaFilter();

            if(Utils.isEmpty(PreferencesUtils.getString(getActivity(), PreferencesUtils.KEY_TOKEN))|| MyApplication.getUserInfo(getActivity())==null) {
                showUnLoginSnackbar();
                buttonView.setChecked(false);
                return;
            }

            if (isChecked) {
                if (voteMap.size() < mNewVoteBeanVO.getMax_number()) {
                    voteMap.put(id, id);
                } else {
                    if(mNewVoteBeanVO.getMax_number()>1) {
                        buttonView.setChecked(false);
                        showTost("已达最大投票数"+mNewVoteBeanVO.getMax_number()+"票", 0);
                    }else {
                        selectUpdateCancleFirst(id);
                    }

//                    voteMap.put(id, id);
//                    Set<String> keys =voteMap.keySet();
//                    for (String key: keys) {
//                        if(id.equals(key)){
//                            voteMap.remove(key);
//                        }
//                    }
//                        buttonView.setChecked(true);
                }
            } else {
                voteMap.remove(id);
            }
        }
    }

    /**
     * 已达最大投票数  移除第一个  选择
     * @param id
     */
    public void selectUpdateCancleFirst(String id){
        if (mNewVoteBeanVO.getMax_number()!=1) {
            showTost("已达最大投票数"+mNewVoteBeanVO.getMax_number()+"票", 0);
            return;
        }

        BaseAdapter baseAdapter = (BaseAdapter)(mRecyclerView.getAdapter());
        ArrayList<E> datas = (ArrayList<E>) baseAdapter.getData();
        for(int i=0;i<datas.size();i++){
            E data = datas.get(i);
            if(data instanceof VoteNewProjectVO){
                String voteid = ((VoteNewProjectVO) data).getId();
                if(voteMap.containsKey(voteid)){
                    voteMap.remove(voteid);
                    voteMap.put(id,id);
                    baseAdapter.changeData(i,data);
                    break;
                }
            }
        }

        for(int i=0;i<datas.size();i++){
            E data = datas.get(i);
            if(data instanceof VoteNewProjectVO){
                String voteid = ((VoteNewProjectVO) data).getId();
                if(voteid.equalsIgnoreCase(id)){
                    baseAdapter.changeData(i,data);
                    break;
                }
            }
        }

    }

}
