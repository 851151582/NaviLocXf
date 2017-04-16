package com.sannas.navilocxf.activity;

import android.content.Intent;
import android.os.Bundle;

import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.sannas.navilocxf.R;


public class GPSNaviActivity extends BaseActivity {
    protected NaviLatLng mEndLatlng;
    protected NaviLatLng mStartLatlng;
    private double mEndLatitude;
    private double mEndLongitude;
    private double mStartLatitude;
    private double mStartLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从POISearchActivity中获取起始位置、终点位置的坐标信息
        Intent intent = getIntent();
        mEndLatitude = intent.getDoubleExtra("mEndLatitude",30.76801+0.01);
        mEndLongitude = intent.getDoubleExtra("mEndLongitude",103.986022+0.01);
        mStartLatitude = intent.getDoubleExtra("mStartLatitude",30.76801+1);
        mStartLongitude = intent.getDoubleExtra("mStartLongitude",103.986022+1);
        mEndLatlng = new NaviLatLng(mEndLatitude,mEndLongitude);
        mStartLatlng = new NaviLatLng(mStartLatitude,mStartLongitude);
        sList.add(mStartLatlng);
        eList.add(mEndLatlng);
        setContentView(R.layout.activity_basic_navi);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);

    }

    /**
     * 地图初始化成功回调的方法
     * 在此方法中进行路径的规划
     */
    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        /**
         * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
         *
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
         */
        int strategy = 0;
        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAMapNavi.setCarNumber("川", "ZZ1234");
        mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);

    }

    /**
     * 路径计算成功回调的方法
     */
    @Override
    public void onCalculateRouteSuccess() {
        super.onCalculateRouteSuccess();
        mAMapNavi.startNavi(NaviType.GPS);   //实时导航
        //mAMapNavi.startNavi(NaviType.EMULATOR);   //模拟导航
    }
}
