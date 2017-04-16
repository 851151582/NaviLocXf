package com.sannas.navilocxf.activity;

import android.content.Intent;
import android.os.Bundle;

import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.sannas.navilocxf.R;


public class EmulatorActivity extends BaseActivity{
    protected NaviLatLng mEndLatlng;
    protected NaviLatLng mStartLatlng;
    private double mEndLatitude;
    private double mEndLongitude;
    private double mStartLatitude;
    private double mStartLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_basic_navi);
        //从POISearchActivity中获取起始位置、终点位置的坐标信息
        Intent intent = getIntent();
        mEndLatitude = intent.getDoubleExtra("mEndLatitude",30.76801+0.01);
        mEndLongitude = intent.getDoubleExtra("mEndLongitude",103.986022 +0.01);
        mStartLatitude = intent.getDoubleExtra("mStartLatitude",30.76801+1);
        mStartLongitude = intent.getDoubleExtra("mStartLongitude",103.986022+1);
        mEndLatlng = new NaviLatLng(mEndLatitude,mEndLongitude);
        mStartLatlng = new NaviLatLng(mStartLatitude,mStartLongitude);
        sList.add(mStartLatlng);
        eList.add(mEndLatlng);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        //设置模拟导航的行车速度
        mAMapNavi.setEmulatorNaviSpeed(75);
        sList.add(mStartLatlng);
        eList.add(mEndLatlng);
    }


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
        mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);

    }

    @Override
    public void onCalculateRouteSuccess() {
        super.onCalculateRouteSuccess();
        mAMapNavi.startNavi(NaviType.EMULATOR);
    }
}
