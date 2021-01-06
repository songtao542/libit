package com.liabit.citypicker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liabit.citypicker.adapter.CityListAdapter;
import com.liabit.citypicker.adapter.CityPicker;
import com.liabit.citypicker.adapter.InnerListener;
import com.liabit.citypicker.adapter.OnRequestCitiesListener;
import com.liabit.citypicker.adapter.OnRequestLocationListener;
import com.liabit.citypicker.adapter.OnResultListener;
import com.liabit.citypicker.adapter.decoration.DividerItemDecoration;
import com.liabit.citypicker.adapter.decoration.SectionItemDecoration;
import com.liabit.citypicker.db.DBManager;
import com.liabit.citypicker.model.City;
import com.liabit.citypicker.model.HotCity;
import com.liabit.citypicker.model.LocateState;
import com.liabit.citypicker.model.LocatedCity;
import com.liabit.citypicker.view.SideIndexBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Bro0cL
 * @Date: 2018/2/6 20:50
 */
public class CityPickerFragment extends AppCompatDialogFragment implements TextWatcher,
        View.OnClickListener, SideIndexBar.OnIndexTouchedChangedListener, InnerListener, CityPicker {

    private static final String TAG = "CityPickerFragment";

    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private View mLoadingView;
    private EditText mSearchBox;
    private ImageView mClearAllBtn;

    private CityListAdapter mAdapter;
    private List<City> mAllCities;
    //private List<HotCity> mHotCities;
    private boolean mEnableHotCities;
    private boolean mEnableLocation;
    private boolean mUserDefaultCities = true;
    private List<City> mResults;

    private DBManager dbManager;

    private boolean enableAnim = false;
    private int mAnimStyle = R.style.DefaultCityPickerAnimation;
    private boolean mMultipleMode = false;
    private LocatedCity mLocatedCity;
    private int locateState;
    private OnResultListener mOnResultListener;
    protected OnRequestLocationListener mOnRequestLocationListener;
    protected OnRequestCitiesListener mOnRequestCitiesListener;

    /**
     * 获取实例
     *
     * @param enable 是否启用动画效果
     * @return
     */
    public static CityPickerFragment newInstance(boolean enable) {
        final CityPickerFragment fragment = new CityPickerFragment();
        Bundle args = new Bundle();
        args.putBoolean("cp_enable_anim", enable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            enableAnim = args.getBoolean("cp_enable_anim");
        }

        initLocatedCity();

        if (mUserDefaultCities) {
            LoaderManager.getInstance(this).initLoader(1, null, new LoaderManager.LoaderCallbacks<List<City>>() {
                @NonNull
                @Override
                public Loader<List<City>> onCreateLoader(int id, @Nullable Bundle args) {
                    return new AsyncTaskLoader<List<City>>(requireContext()) {
                        @Override
                        protected void onStartLoading() {
                            super.onStartLoading();
                            forceLoad();
                        }

                        @Nullable
                        @Override
                        public List<City> loadInBackground() {
                            dbManager = new DBManager(getContext());
                            return dbManager.getAllCities();
                        }
                    };
                }

                @Override
                public void onLoadFinished(@NonNull Loader<List<City>> loader, List<City> data) {
                    mAllCities = data;
                    mResults = mAllCities;
                    setCities(getDefaultHotCities(), mResults);
                }

                @Override
                public void onLoaderReset(@NonNull Loader<List<City>> loader) {
                }
            });
        }
    }

    private void initLocatedCity() {
        if (mLocatedCity == null) {
            mLocatedCity = new LocatedCity(getString(R.string.cp_locating), getString(R.string.cp_unknown), "0");
            locateState = LocateState.FAILURE;
        } else {
            locateState = LocateState.SUCCESS;
        }
    }

    private List<HotCity> getDefaultHotCities() {
        ArrayList<HotCity> hotCities = new ArrayList<>();
        hotCities.add(new HotCity("北京", "北京", "101010100"));
        hotCities.add(new HotCity("上海", "上海", "101020100"));
        hotCities.add(new HotCity("广州", "广东", "101280101"));
        hotCities.add(new HotCity("深圳", "广东", "101280601"));
        hotCities.add(new HotCity("天津", "天津", "101030100"));
        hotCities.add(new HotCity("杭州", "浙江", "101210101"));
        hotCities.add(new HotCity("南京", "江苏", "101190101"));
        hotCities.add(new HotCity("成都", "四川", "101270101"));
        hotCities.add(new HotCity("武汉", "湖北", "101200101"));
        return hotCities;
    }

    public void enableHotCities(boolean enableHotCities) {
        this.mEnableHotCities = enableHotCities;
    }

    public void enableLocation(boolean enableLocation) {
        this.mEnableLocation = enableLocation;
    }

    @SuppressLint("ResourceType")
    public void setAnimationStyle(@StyleRes int style) {
        this.enableAnim = true;
        this.mAnimStyle = style <= 0 ? R.style.DefaultCityPickerAnimation : style;
    }

    public void setMultipleMode(boolean multipleMode) {
        this.mMultipleMode = multipleMode;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.DefaultCityPickerTheme);
        //LayoutInflater themeInflater = inflater.cloneInContext(contextThemeWrapper);
        //view = themeInflater.inflate(R.layout.cp_dialog_city_picker, container, false);
        inflater.getContext().getTheme().applyStyle(R.style.DefaultCityPickerTheme, false);
        View view = inflater.inflate(R.layout.cp_dialog_city_picker, container, false);
        view.findViewById(R.id.toolbar).setPadding(0, getStatusBarHeight(requireContext()), 0, 0);
        mRecyclerView = view.findViewById(R.id.cp_city_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SectionItemDecoration(inflater.getContext(), mAllCities), 0);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(inflater.getContext()), 1);
        mAdapter = new CityListAdapter(inflater.getContext(), locateState, mMultipleMode);
        mAdapter.setInnerListener(this);
        mAdapter.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                //确保定位城市能正常刷新
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mAdapter.refreshLocationItem();
                }
            }
        });

        mEmptyView = view.findViewById(R.id.cp_empty_view);
        TextView overlayTextView = view.findViewById(R.id.cp_overlay);

        mLoadingView = view.findViewById(R.id.cp_loading_view);

        SideIndexBar indexBar = view.findViewById(R.id.cp_side_index_bar);
        indexBar.setOverlayTextView(overlayTextView).setOnIndexChangedListener(this);

        indexBar.enableLocation(mEnableLocation);
        indexBar.enableHotCities(mEnableHotCities);

        mSearchBox = view.findViewById(R.id.cp_search_box);
        mSearchBox.addTextChangedListener(this);

        TextView confirmBtn = view.findViewById(R.id.cp_confirm);
        confirmBtn.setOnClickListener(this);
        mClearAllBtn = view.findViewById(R.id.cp_clear_all);
        mClearAllBtn.setOnClickListener(this);
        view.findViewById(R.id.cp_back).setOnClickListener(this);

        if (mMultipleMode) {
            view.findViewById(R.id.cp_confirm).setVisibility(View.VISIBLE);
            view.findViewById(R.id.cp_divider).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.cp_confirm).setVisibility(View.GONE);
            view.findViewById(R.id.cp_divider).setVisibility(View.GONE);
        }

        if (mAllCities == null || mAllCities.size() == 0) {
            mLoadingView.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ((mAllCities == null || mAllCities.size() == 0) && mOnRequestCitiesListener != null) {
            mOnRequestCitiesListener.onRequestCities(this);
        }
    }

    @SuppressLint({"NewApi", "ObsoleteSdkInt"})
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
            window.setDimAmount(0);
            layoutUnderSystemUI(window, true, false);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            if (enableAnim) {
                window.setWindowAnimations(mAnimStyle);
            }
        }
        return dialog;
    }

    private void layoutUnderSystemUI(Window window, boolean lightStatusBar, boolean lightNavigationBar) {
        int flag = window.getDecorView().getSystemUiVisibility();
        boolean ls = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            ls = ((flag & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0);
        }
        boolean lightStatus = lightStatusBar || ls;
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (lightStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            boolean lightNavigation = lightNavigationBar || (flag & View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR) != 0;
            if (lightNavigation) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
        }
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(flags);
    }

    private int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 搜索框监听
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        final String keyword = s.toString();
        if (TextUtils.isEmpty(keyword)) {
            mClearAllBtn.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
            mResults = mAllCities;
            ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mResults);
            mAdapter.updateData(mResults);
        } else {
            mClearAllBtn.setVisibility(View.VISIBLE);
            //开始数据库查找
            //mResults = dbManager.searchCity(keyword);
            mResults = filter(keyword);
            ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mResults);
            if (mResults == null || mResults.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                mAdapter.updateData(mResults);
            }
        }
        mRecyclerView.scrollToPosition(0);
    }

    private List<City> filter(String keyword) {
        if (mAllCities != null) {
            ArrayList<City> results = new ArrayList<>();
            for (City city : mAllCities) {
                if (city.getName().contains(keyword) || city.getPinyin().contains(keyword)) {
                    results.add(city);
                }
            }
            results.trimToSize();
            return results;
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cp_back) {
            dismiss(null);
        } else if (id == R.id.cp_confirm) {
            dismiss();
            if (mOnResultListener != null) {
                mOnResultListener.onResult(mAdapter.getSelected());
            }
        } else if (id == R.id.cp_clear_all) {
            mSearchBox.setText("");
        }
    }

    @Override
    public void onIndexChanged(String index, int position) {
        //滚动RecyclerView到索引位置
        mAdapter.scrollToSection(index);
    }


    @Override
    public void dismiss(City data) {
        dismiss();
        if (mOnResultListener != null) {
            ArrayList<City> result = new ArrayList<>();
            if (data != null) {
                result.add(data);
            }
            mOnResultListener.onResult(result);
        }
    }

    @Override
    public void requestLocation() {
        if (mOnRequestLocationListener != null) {
            mOnRequestLocationListener.onRequestLocation(this);
        }
    }

    public void setOnResultListener(OnResultListener listener) {
        this.mOnResultListener = listener;
    }

    public void setOnRequestLocationListener(OnRequestLocationListener listener) {
        this.mOnRequestLocationListener = listener;
    }

    public void setOnRequestCitiesListener(OnRequestCitiesListener listener) {
        this.mOnRequestCitiesListener = listener;
    }


    /**
     * 从自带的城市数据库中加载城市列表
     */
    public void useDefaultCities(boolean use) {
        mUserDefaultCities = use;
    }

    @Override
    public void setCities(List<HotCity> hotCities, List<City> cities) {
        if (cities != null && cities.size() > 0) {
            mLoadingView.setVisibility(View.GONE);
            mAllCities = cities;
            for (City city : mAllCities) {
                city.setSelected(false);
            }
            ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mAllCities);
            int index = 0;
            if (mEnableLocation) {
                mAllCities.add(index, mLocatedCity);
                index += 1;
            }
            if (mEnableHotCities && hotCities != null && hotCities.size() > 0) {
                mAllCities.add(index, new HotCity(getString(R.string.cp_hot_city), getString(R.string.cp_unknown), "0"));
            }
            mAdapter.setCities(hotCities, mAllCities);
        }
    }

    @Override
    public void updateLocation(LocatedCity location, @LocateState.State int state) {
        if (mAdapter != null) {
            mAdapter.updateLocateState(location, state);
        }
    }

    public static class Builder {
        private FragmentManager mFragmentManager;
        private Fragment mTargetFragment;

        private boolean mMultipleMode;
        private int mAnimStyle;
        private List<HotCity> mHotCities;
        private List<City> mCities;
        private boolean mEnableHotCities;
        private boolean mEnableLocation;
        private boolean mUserDefaultCities = true;
        private OnResultListener mOnResultListener;
        private OnRequestLocationListener mOnRequestLocationListener;
        private OnRequestCitiesListener mOnRequestCitiesListener;

        public Builder() {
        }

        public CityPickerFragment.Builder fragmentManager(FragmentManager fm) {
            mFragmentManager = fm;
            return this;
        }

        public CityPickerFragment.Builder targetFragment(Fragment targetFragment) {
            mTargetFragment = targetFragment;
            return this;
        }

        /**
         * 设置动画效果
         *
         * @param animStyle
         * @return
         */
        public CityPickerFragment.Builder animationStyle(@StyleRes int animStyle) {
            mAnimStyle = animStyle;
            return this;
        }

        /**
         * 设置多选模式
         *
         * @param multipleMode
         * @return
         */
        public CityPickerFragment.Builder multipleMode(boolean multipleMode) {
            mMultipleMode = multipleMode;
            return this;
        }

        public CityPickerFragment.Builder hotCities(List<HotCity> data) {
            mHotCities = data;
            return this;
        }

        public CityPickerFragment.Builder cities(List<City> data) {
            mCities = data;
            return this;
        }

        public CityPickerFragment.Builder enableHotCities(boolean enable) {
            mEnableHotCities = enable;
            return this;
        }

        public CityPickerFragment.Builder enableLocation(boolean enable) {
            mEnableLocation = enable;
            return this;
        }

        /**
         * 从自带的城市数据库中加载城市列表
         */
        public CityPickerFragment.Builder useDefaultCities(boolean use) {
            mUserDefaultCities = use;
            return this;
        }

        /**
         * 设置选择结果的监听器
         *
         * @param listener
         * @return
         */
        public CityPickerFragment.Builder resultListener(OnResultListener listener) {
            mOnResultListener = listener;
            return this;
        }

        /**
         * 设置选择结果的监听器
         *
         * @param listener
         * @return
         */
        public CityPickerFragment.Builder requestLocationListener(OnRequestLocationListener listener) {
            mOnRequestLocationListener = listener;
            return this;
        }

        /**
         * 设置选择结果的监听器
         *
         * @param listener
         * @return
         */
        public CityPickerFragment.Builder requestCitiesListener(OnRequestCitiesListener listener) {
            mOnRequestCitiesListener = listener;
            return this;
        }


        public CityPickerFragment show() {
            final CityPickerFragment cityPickerFragment = new CityPickerFragment();
            cityPickerFragment.setCities(mHotCities, mCities);
            cityPickerFragment.enableHotCities(mEnableHotCities);
            cityPickerFragment.enableLocation(mEnableLocation);
            cityPickerFragment.setAnimationStyle(mAnimStyle);
            cityPickerFragment.useDefaultCities(mUserDefaultCities);
            cityPickerFragment.setMultipleMode(mMultipleMode);
            cityPickerFragment.setOnResultListener(mOnResultListener);
            cityPickerFragment.setOnRequestLocationListener(mOnRequestLocationListener);
            cityPickerFragment.setOnRequestCitiesListener(mOnRequestCitiesListener);
            if (mTargetFragment != null) {
                cityPickerFragment.setTargetFragment(mTargetFragment, 0);
            }

            if (mFragmentManager != null) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                final Fragment prev = mFragmentManager.findFragmentByTag(TAG);
                if (prev != null) {
                    ft.remove(prev).commit();
                    ft = mFragmentManager.beginTransaction();
                }
                ft.addToBackStack(TAG);
                cityPickerFragment.show(ft, TAG);
            }
            return cityPickerFragment;
        }
    }

}
