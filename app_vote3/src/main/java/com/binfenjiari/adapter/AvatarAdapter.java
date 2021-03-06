/*
 * Copyright (C) 2017 Huajian Jiang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.binfenjiari.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.binfenjiari.R;
import com.binfenjiari.model.Img;
import com.binfenjiari.utils.Glides;
import com.github.huajianjiang.baserecyclerview.widget.ArrayAdapter;
import com.github.huajianjiang.baserecyclerview.widget.BaseViewHolder;

import java.util.List;

/**
 * Created by Huajian Jiang on 2017/8/24.
 * developer.huajianjiang@gmail.com
 */
public class AvatarAdapter extends ArrayAdapter<BaseViewHolder, Img> {

    public AvatarAdapter(Context ctxt) {
        super(ctxt);
    }

    public AvatarAdapter(Context ctxt, List<Img> items) {
        super(ctxt, items);
    }

    @Override
    public BaseViewHolder onGenerateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(getLayoutInflater().inflate(viewType, parent, false)) {
        };
    }

    @Override
    public void onPopulateViewHolder(BaseViewHolder holder, int position) {
        Img img = getItem(position);
        Glides.loadAvatarFormUrl(img.url, (ImageView) holder.getView(R.id.avatar));
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_img_avatar;
    }


    public class ItemDecor extends RecyclerView.ItemDecoration {
        private int offsets;

        public ItemDecor() {
            offsets = getContext().getResources().getDimensionPixelSize(R.dimen.item_offset);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                RecyclerView.State state)
        {
            final int childAdapterPos = parent.getChildAdapterPosition(view);
            final int right = childAdapterPos == getItemCount() - 1 ? 0 : offsets;
            outRect.set(0, 0, right, 0);
        }
    }
}
