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

package com.binfenjiari.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.binfenjiari.R;
import com.binfenjiari.base.AppMvpActivity;
import com.binfenjiari.fragment.ReporterWorksDetailFragment;
import com.binfenjiari.fragment.presenter.ReporterWorksDetailPresenter;

/**
 * Created by Huajian Jiang on 2017/8/24.
 * developer.huajianjiang@gmail.com
 */
public class ReporterWorksDetailActivity extends AppMvpActivity<ReporterWorksDetailFragment, ReporterWorksDetailPresenter> {
    @NonNull
    @Override
    public ReporterWorksDetailFragment mvpView() {
        ReporterWorksDetailFragment v= new ReporterWorksDetailFragment();
        v.setArguments(getIntent().getExtras());
        return v;
    }

    @NonNull
    @Override
    public ReporterWorksDetailPresenter mvpPresenter() {
        return new ReporterWorksDetailPresenter();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackNaviAction(R.mipmap.ico_fanhui_back);
        setTitle("作品详情");
    }
}
