package com.biu.modulebase.binfenjiari.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biu.modulebase.binfenjiari.R;
import com.biu.modulebase.binfenjiari.adapter.CommentAvailableAdapter;
import com.biu.modulebase.binfenjiari.adapter.CommentLoader;
import com.biu.modulebase.binfenjiari.adapter.HeaderHandler;
import com.biu.modulebase.binfenjiari.communication.RequestCallBack2;
import com.biu.modulebase.binfenjiari.datastructs.Constant;
import com.biu.modulebase.binfenjiari.model.AvHeaderParentItem;
import com.biu.modulebase.binfenjiari.model.ReplyItem;
import com.biu.modulebase.binfenjiari.util.JSONUtil;
import com.biu.modulebase.binfenjiari.util.LogUtil;
import com.biu.modulebase.binfenjiari.util.OtherUtil;
import com.biu.modulebase.binfenjiari.widget.expandablerecyclerview.model.ParentListItem;
import com.biu.modulebase.binfenjiari.widget.expandablerecyclerview.viewholder.ParentViewHolder;
import com.biu.modulebase.binfenjiari.widget.expandablerecyclerview.viewholder.ViewHolderCallbacks;
import com.biu.modulebase.common.base.BaseFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jhj_Plus on 2016/5/23.
 */
public class AvItDetailFragment extends BaseFragment {
    private static final String TAG = "AvItDetailFragment";

    private RecyclerView mRecyclerView;

    private HeaderHandler mHeaderHandler;
    private CommentLoader mCommentLoader;

    private CheckBox mLikeBox;
    private CheckBox mCollectBox;

    private int position =-1;
    private String mId="-1";
    private String oldLikeStatus="";

    private AvHeaderParentItem avHeader;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHeaderHandler=new HeaderHandler(getActivity());

        setHasOptionsMenu(true);

        Bundle args=getArguments();
        if (args!=null) {
            position =args.getInt(Constant.KEY_POSITION);
            mId=args.getString(Constant.KEY_ID);
        }
    }

    public static AvItDetailFragment newInstance(int position,String id) {
        Bundle args = new Bundle();
        args.putInt(Constant.KEY_POSITION, position);
        args.putString(Constant.KEY_ID, id);
        AvItDetailFragment fragment = new AvItDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        ViewGroup rootView= (ViewGroup) inflater.inflate(R.layout.fragment_av_it_detail,
                container,false);
        return super.onCreateView(inflater, rootView, savedInstanceState);
    }

    @Override
    protected void initView(View rootView) {

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        HashMap<String, Object> args = new HashMap<>();
        //获取主评论 的 action 和 model
        args.put(Constant.KEY_MODEL, Constant.MODEL_AV);
        args.put(Constant.KEY_ACTION, Constant.ACTION_AV_COMMENT_DETAIL);
        //回复主评论或回复 的 action 和 model
        args.put(Constant.KEY_MODEL_COMMENT_DETAIL,Constant.MODEL_AV);
        args.put(Constant.KEY_ACTION_COMMENT_DETAIL,Constant.ACTION_AV_REPLY);

        //删除评论 的 action 和 model
        args.put(Constant.KEY_MODEL_DELETE_COMMENT_COMMENT_DETAIL,Constant.MODEL_AV);
        args.put(Constant.KEY_ACTION_DELETE_COMMENT_COMMENT_DETAIL,Constant
                .ACTION_AV_DELETE_COMMENT);

        //删除回复 的 action 和 model
        args.put(Constant.KEY_MODEL_DELETE_REPLY_COMMENT_DETAIL,Constant.MODEL_AV);
        args.put(Constant.KEY_ACTION_DELETE_REPLY_COMMENT_DETAIL,Constant.ACTION_AV_DELETE_REPLY);

        //删除回复或评论的 的字段 名
        args.put(Constant.KEY_NAME_ARGS, "vedioId");
        //删除回复或评论的 的字段 值
        args.put(Constant.KET_VALUE_ARGS, mId);

        //评论举报类型
        args.put(Constant.KEY_REPORT_TYPE,Constant.REPORT_AUDIO_COMMENT);


        //评论基础参数设置
        JSONObject params = OtherUtil.getJSONObject(getActivity(), Constant.MODEL_AV,
                Constant.ACTION_AV_COMMENT_LIST, false);
        JSONUtil.put(params, "id", mId);
        //评论处理类(加载，回复，删除 评论)
        mCommentLoader = new CommentLoader(CommentLoader.TYPE_LIST, this, params, args);

        CommentAvailableAdapter adapter=new CommentAvailableAdapter(this,mCommentLoader,args,new
                ArrayList<ParentListItem>(),
                R.layout.header_av_it_detail, new ViewHolderCallbacks() {
            @Override
            public int[] getNeedRegisterClickListenerChildViewIds() {
                return new int[0];
            }

            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder, View view, int position,
                    int adapterPosition, int parentPosition, int parentAdapterPosition)
            {

            }

            @Override
            public void bindData(RecyclerView.ViewHolder viewHolder, Object data) {
                if (data==null||!(data instanceof AvHeaderParentItem)) return;

                AvHeaderParentItem item= (AvHeaderParentItem) data;

                ParentViewHolder holder = (ParentViewHolder) viewHolder;

                TextView tv_title =(TextView)holder.getView(R.id.tv_title);
                tv_title.setText(item.getName());
                TextView tv_content  =(TextView)holder.getView(R.id.tv_content1);
                tv_content.setText(item.getContent());
                TextView tv_comment_number = (TextView) holder.getView(R.id.tv_comment_number);
                tv_comment_number.setText(getString(R.string.comment,item.getComment_number()));

                if (mHeaderHandler!=null) {
                    mHeaderHandler.createBindImageViews((LinearLayout) holder.getView(R.id.layout_it)
                            ,item.getPicList());
                }
            }
        });

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCommentLoader.setCommentAvailableAdapter(adapter);

        View comment = rootView.findViewById(R.id.tv_comment);
        comment.setOnClickListener(this);

        mLikeBox= (CheckBox) rootView.findViewById(R.id.like);
        mLikeBox.setOnClickListener(this);

        mCollectBox= (CheckBox) rootView.findViewById(R.id.collect);
        mCollectBox.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.tv_comment) {
            JSONObject params = OtherUtil.getJSONObject(getActivity(),
                    Constant.MODEL_AV, Constant
                            .ACTION_AV_COMMENT, true);
            JSONUtil.put(params, "id", mId);
            mCommentLoader.doComment(params, -1);


        } else if (i == R.id.like) {
            JSONObject params2 = OtherUtil.getJSONObject(getActivity(), Constant.MODEL_AV,
                    Constant
                            .ACTION_AV_LIKE, true);
            JSONUtil.put(params2, "id", mId);
            OtherUtil.like(this, mLikeBox, params2, new OtherUtil.LikeCallback() {
                @Override
                public void onFinished(int backKey) {
                    Intent intent = new Intent();
                    intent.putExtra("position", position);
                    intent.putExtra("backKey", backKey);
                    if ((backKey == 1 && oldLikeStatus.equals("2")) || (backKey == 2 && oldLikeStatus.equals("1"))) {//点赞 或取消赞成功
                        getActivity().setResult(Activity.RESULT_OK, intent);
                    } else {
                        getActivity().setResult(Activity.RESULT_CANCELED);
                    }
                }
            });

        } else if (i == R.id.collect) {
            OtherUtil.collect(this, mCollectBox, mId, Constant.MODEL_AV, Constant
                    .ACTION_AV_COLLECT);

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        visibleLoading();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void loadData() {
        loadHeader();
        loadComments();
    }

    private void loadHeader() {
        JSONObject params= OtherUtil.getJSONObject(getActivity(),Constant.MODEL_AV,Constant
                .ACTION_AV_DETAIL,false);
        JSONUtil.put(params,"id",mId);

        dataRequest(false, params, Constant.SERVERURL, TAG, new RequestCallBack2() {
            @Override
            public void requestBefore() {
//                visibleLoading();
            }

            @Override
            public void onSuccess(String mainJsonString,JSONObject mainJsonObject,JSONObject rootJsonObject) {

                avHeader=JSONUtil.fromJson(mainJsonString,
                        AvHeaderParentItem.class);
                oldLikeStatus =avHeader.getLike_status();
                //头部数据加载完成设置给评论类
                mCommentLoader.addHeaderData(avHeader);

                bindOtherData(avHeader);

            }

            @Override
            public void onFail(int key,String message) {
                if (key == RequestCallBack2.KEY_FAIL) {
                    visibleNoNetWork();
                } else if (key == 3) {
                    visibleNoData();
                }else{
                    showTost(message,1);
                }
                LogUtil.LogE(TAG,"onFail*");
            }

            @Override
            public void requestAfter() {
                inVisibleLoading();
            }
        });
    }

    private void bindOtherData(AvHeaderParentItem avHeader) {
        if (mLikeBox != null) {
            mLikeBox.setChecked(avHeader.getLike_status().equals("1"));
            mLikeBox.setText(avHeader.getLike_number());
        }
        if (mCollectBox != null) {
            mCollectBox.setChecked(avHeader.getCollect_status().equals("1"));
        }
    }

    private void loadComments() {
        //加载评论处理丢给他处理
        mCommentLoader.loadComments();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.more,menu);
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.LogE(TAG,"onActivityResult====================");
        if (requestCode!=Constant.REQUEST_COMMENT_CHANGED_STATUS) return;
        if (resultCode==Activity.RESULT_OK) {
            ArrayList<ReplyItem> replies = (ArrayList<ReplyItem>) data.getSerializableExtra(
                    Constant.KEY_DATA);
            final int parentPosition=data.getIntExtra(Constant.KEY_PARENT_POSITION,-1);
            //删除评论列表数据，如果返回的回复数据为null表明直接删除整个评论，否则先删除该评论下的所有回复后再
            //添加新的回复数据
            if (replies==null) {
                mCommentLoader.deleteLocaleComment(CommentLoader.TYPE_LIST,parentPosition);
            } else {
                mCommentLoader.deleteLocaleReplies(CommentLoader.TYPE_LIST,parentPosition);
                mCommentLoader.addLocalReplies(CommentLoader.TYPE_LIST,parentPosition,replies);
            }
            //通知刷新评论数
            mCommentLoader.notifyCommentReplyDataChanged(CommentLoader.TYPE_LIST,
                    data.getIntExtra(Constant.KEY_COMMENT_OPREATE_TYPE,-1),data.getIntExtra
                            (Constant.KEY_COMMENT_CHANGED_COUNT,0),parentPosition,null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_more) {
            OtherUtil.showMoreOperate(AvItDetailFragment.this, mId, avHeader.getName(), avHeader.getContent(), null, Constant.SHARE_PIC_AND_TEXT
                    , Constant.REPORT_AUDIO, null, false, null, -1, null);

        } else {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        mCommentLoader.cancleRequest();
        super.onDestroyView();
    }
}
