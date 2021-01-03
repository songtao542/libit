package com.liabit.citypicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
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
public class CityPickerDialogFragment extends AppCompatDialogFragment implements TextWatcher,
        View.OnClickListener, SideIndexBar.OnIndexTouchedChangedListener, InnerListener, CityPicker {
    private View mContentView;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private View mLoadingView;
    private TextView mOverlayTextView;
    private SideIndexBar mIndexBar;
    private EditText mSearchBox;
    private TextView mConfirmBtn;
    private ImageView mClearAllBtn;

    private LinearLayoutManager mLayoutManager;
    private CityListAdapter mAdapter;
    private List<City> mAllCities;
    private List<HotCity> mHotCities;
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
    public static CityPickerDialogFragment newInstance(boolean enable) {
        final CityPickerDialogFragment fragment = new CityPickerDialogFragment();
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

        initDefaultHotCities();
        initLocatedCity();

        if (mUserDefaultCities) {
            dbManager = new DBManager(getContext());
            mAllCities = dbManager.getAllCities();

            if (mEnableLocation) {
                mAllCities.add(0, mLocatedCity);
            }
            if (mEnableHotCities) {
                if (mEnableLocation) {
                    mAllCities.add(1, new HotCity("热门城市", "未知", "0"));
                } else {
                    mAllCities.add(0, new HotCity("热门城市", "未知", "0"));
                }
            }
        }
        mResults = mAllCities;
    }

    private void initLocatedCity() {
        if (mLocatedCity == null) {
            mLocatedCity = new LocatedCity(getString(R.string.cp_locating), "未知", "0");
            locateState = LocateState.FAILURE;
        } else {
            locateState = LocateState.SUCCESS;
        }
    }

    private void initDefaultHotCities() {
        if (mHotCities == null || mHotCities.isEmpty()) {
            mHotCities = new ArrayList<>();
            mHotCities.add(new HotCity("北京", "北京", "101010100"));
            mHotCities.add(new HotCity("上海", "上海", "101020100"));
            mHotCities.add(new HotCity("广州", "广东", "101280101"));
            mHotCities.add(new HotCity("深圳", "广东", "101280601"));
            mHotCities.add(new HotCity("天津", "天津", "101030100"));
            mHotCities.add(new HotCity("杭州", "浙江", "101210101"));
            mHotCities.add(new HotCity("南京", "江苏", "101190101"));
            mHotCities.add(new HotCity("成都", "四川", "101270101"));
            mHotCities.add(new HotCity("武汉", "湖北", "101200101"));
        }
    }

    public void setLocatedCity(LocatedCity location) {
        mLocatedCity = location;
    }

    public void setHotCities(List<HotCity> data) {
        if (data != null && !data.isEmpty()) {
            this.mHotCities = data;
        }
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
        //mContentView = themeInflater.inflate(R.layout.cp_dialog_city_picker, container, false);
        //getActivity().getTheme().applyStyle(R.style.DefaultCityPickerTheme, true);
        inflater.getContext().getTheme().applyStyle(R.style.DefaultCityPickerTheme, false);
        mContentView = inflater.inflate(R.layout.cp_dialog_city_picker, container, false);

        mRecyclerView = mContentView.findViewById(R.id.cp_city_recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SectionItemDecoration(inflater.getContext(), mAllCities), 0);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(inflater.getContext()), 1);
        mAdapter = new CityListAdapter(inflater.getContext(), mAllCities, mHotCities, locateState, mMultipleMode);
        mAdapter.setInnerListener(this);
        mAdapter.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //确保定位城市能正常刷新
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mAdapter.refreshLocationItem();
                }
            }
        });

        mEmptyView = mContentView.findViewById(R.id.cp_empty_view);
        mOverlayTextView = mContentView.findViewById(R.id.cp_overlay);

        mLoadingView = mContentView.findViewById(R.id.cp_loading_view);

        mIndexBar = mContentView.findViewById(R.id.cp_side_index_bar);
        mIndexBar.setOverlayTextView(mOverlayTextView)
                .setOnIndexChangedListener(this);

        mIndexBar.enableLocation(mEnableLocation);
        mIndexBar.enableHotCities(mEnableHotCities);

        mSearchBox = mContentView.findViewById(R.id.cp_search_box);
        mSearchBox.addTextChangedListener(this);


        mConfirmBtn = mContentView.findViewById(R.id.cp_confirm);
        mClearAllBtn = mContentView.findViewById(R.id.cp_clear_all);
        mConfirmBtn.setOnClickListener(this);
        mClearAllBtn.setOnClickListener(this);
        mContentView.findViewById(R.id.cp_back).setOnClickListener(this);

        if (mMultipleMode) {
            mContentView.findViewById(R.id.cp_confirm).setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.cp_divider).setVisibility(View.VISIBLE);
        } else {
            mContentView.findViewById(R.id.cp_confirm).setVisibility(View.GONE);
            mContentView.findViewById(R.id.cp_divider).setVisibility(View.GONE);
        }

        if (mAllCities == null || mAllCities.size() == 0) {
            mLoadingView.setVisibility(View.VISIBLE);
        }
        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ((mAllCities == null || mAllCities.size() == 0) && mOnRequestCitiesListener != null) {
            mOnRequestCitiesListener.onRequestCities(this);
        }
    }

    @SuppressLint("NewApi")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            if ((window.getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0) {
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            } else {
                int screenHeight = getScreenHeight(getActivity());
                int statusBarHeight = getStatusBarHeight(getContext());
                int dialogHeight = screenHeight - statusBarHeight;
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);
            }

//            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            }
            if (enableAnim) {
                window.setWindowAnimations(mAnimStyle);
            }
        }
        return dialog;
    }

    private int getScreenHeight(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
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
    public void setCities(List<City> cities) {
        if (cities != null && cities.size() > 0) {
            mLoadingView.setVisibility(View.GONE);
            mAllCities = cities;
            for (City city : mAllCities) {
                city.setSelected(false);
            }
            mAdapter.setCities(this.mAllCities);
            ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mAllCities);
            if (mEnableLocation) {
                mAllCities.add(0, mLocatedCity);
            }
            if (mEnableHotCities) {
                if (mEnableLocation) {
                    mAllCities.add(1, new HotCity("热门城市", "未知", "0"));
                } else {
                    mAllCities.add(0, new HotCity("热门城市", "未知", "0"));
                }
            }
        }
    }

    @Override
    public void updateLocation(LocatedCity location, @LocateState.State int state) {
        if (mAdapter != null) {
            mAdapter.updateLocateState(location, state);
        }
    }

}
