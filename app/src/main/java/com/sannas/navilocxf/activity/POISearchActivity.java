package com.sannas.navilocxf.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;

import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.RotateAnimation;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;

import com.sannas.navilocxf.R;
import com.sannas.navilocxf.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class POISearchActivity extends AppCompatActivity implements
        Inputtips.InputtipsListener,AMapLocationListener{

    private AMap mAmap;
    private TextView btn_search;
    private EditText input_edittext;
    private RelativeLayout poi_detail;
    private TextView poi_name, poi_address,poi_info;
    private Marker mMarker;
    private double mStartLatitude;
    private double mStartLongitude;
    private double mEndLatitude;
    private double mEndLongtitude;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    //public AMapLocationListener mLocationListener = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poisearch);

        initUi();
        initLocation();
        MapView mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        mAmap = mapView.getMap();

        MyLocationStyle myLocationStyle;
       // MyLocationStyle myLocationIcon(;//设置定位蓝点的icon图标方法，需要用到BitmapDescriptor类对象作为参数。
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
        //myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，
        // 定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        mAmap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        mAmap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        mAmap.getUiSettings().setCompassEnabled(true);   //设置指南针用于向 App 端用户展示地图方向，默认不显示
        mAmap.getUiSettings().setScaleControlsEnabled(true);    //设置比例尺控件。位于地图右下角，可控制其显示与隐藏
        mAmap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        //mStartLatitude = mAmap.getMyLocation().getLatitude();
        //mStartLongitude = mAmap.getMyLocation().getLongitude();
        Toast.makeText(getApplicationContext(),"当前的：" + mStartLatitude +mStartLongitude,Toast.LENGTH_SHORT).show();
        // 定义 Marker 点击事件监听
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                poi_name.setText(marker.getTitle());
                poi_address.setText(marker.getSnippet());
                poi_info.setText("纬度：" + marker.getPosition().latitude + "|"
                        + "经度：" + marker.getPosition().longitude);
                poi_detail.setVisibility(View.VISIBLE);
                mEndLatitude = marker.getPosition().latitude;
                mEndLongtitude = marker.getPosition().longitude;
                return true;
            }
        };
        mAmap.setOnMarkerClickListener(markerClickListener);
    }

    /**
     *
     */
    private void initUi() {
        btn_search = (TextView)findViewById(R.id.btn_search);
        input_edittext = (EditText)findViewById(R.id.input_edittext);
        poi_detail = (RelativeLayout)findViewById(R.id.poi_detail);
        poi_name = (TextView)findViewById(R.id.poi_name);
        poi_address = (TextView)findViewById(R.id.poi_address);
        poi_info = (TextView)findViewById(R.id.poi_info);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poi_detail.setVisibility(View.INVISIBLE);
                //清除之前的Marker标记
                mAmap.getMapScreenMarkers().clear();
                //进行搜索，并给对应的点做标记
                doSearchQuery();
            }
        });

        //***************//
        //点击详细信息文本框后进入导航
        poi_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入开始导航,将获得的起点和终点位置坐标传给导航界面
                Intent intent = new Intent(getApplicationContext(),GPSNaviActivity.class);
                intent.putExtra("mEndLatitude",mEndLatitude);
                intent.putExtra("mEndLongitude",mEndLongtitude);
                intent.putExtra("mStartLatitude",mStartLatitude);
                intent.putExtra("mStartLongitude",mStartLongitude);
                startActivity(intent);
                //Toast.makeText(getApplicationContext(),"开始导航了"+mEndLatitude +mEndLongtitude,Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 对当前位置进行定位，获取当前位置的坐标信息
     */
    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //设置定位参数
        mLocationClient.setLocationOption(getDefaultOption());
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mLocationClient.startLocation();
    }

    /**
     * 对输入的地点进行位置查询
     */
    private void doSearchQuery() {
        //第二个参数传入null或者“”代表在全国进行检索，否则按照传入的city进行检索
        InputtipsQuery inputquery = new InputtipsQuery(input_edittext.getText().toString(),null );
        inputquery.setCityLimit(false);//限制在当前城市
        Inputtips inputTips = new Inputtips(POISearchActivity.this, inputquery);
        inputTips.setInputtipsListener(this);
        inputTips.requestInputtipsAsyn();
    }

    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * @param list   查询成功后回调此方法返回的结果集
     * @param i    返回结果成功或者失败的响应码。1000为成功，其他为失败
     */
    @Override
    public void onGetInputtips(List<Tip> list, int i) {

        //通过tipList获取Tip信息
        if(i == 1000){
            StringBuffer sb = new StringBuffer();
            for(Tip tip : list){
                sb.append(tip.getPoiID()+"|||");
                mMarker = mAmap.addMarker(new MarkerOptions().position(
                        new LatLng(tip.getPoint().getLatitude(),tip.getPoint().getLongitude()))
                        .title(tip.getAddress()).snippet(tip.getDistrict()+"("+tip.getPoiID()+")"));
                Animation animation = new RotateAnimation(mMarker.getRotateAngle()-30,mMarker.getRotateAngle()+30,0,0,3);
                long duration = 1000L;
                animation.setDuration(duration);
                animation.setInterpolator(new LinearInterpolator());
                mMarker.setAnimation(animation);
                mMarker.startAnimation();
                //将地图的中心坐标移动至marker
                //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、
                //           俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
                CameraUpdate update = CameraUpdateFactory.newCameraPosition(
                        new CameraPosition(mMarker.getPosition(),18,30,0));
                mAmap.moveCamera(update);
                //ToastUtil.show(getApplicationContext(),sb.toString());
            }
        }else{
            ToastUtil.showerror(getApplicationContext(),i);
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mLocationClient != null){
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mLocationClient.onDestroy();
            mLocationClient = null;
            mLocationOption = null;
        }
    }

    /**
     *    定位成功后回调的方法
     * @param aMapLocation  返回的定位对象
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation != null){
            if(aMapLocation.getErrorCode() == 0){
                //定位成功回调信息，设置相关消息
                //解析定位结果,在主线程中执行的
                //Log.i(TAG, "线程2："+Thread.currentThread());
                /*aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息
                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                aMapLocation.getStreet();//街道信息
                aMapLocation.getStreetNum();//街道门牌号信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码
                aMapLocation.getAoiName();//获取当前定位点的AOI信息
                aMapLocation.getAccuracy();//获取精度信息*/
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
                mStartLatitude = aMapLocation.getLatitude();
                mStartLongitude = aMapLocation.getLongitude();
                /*tv_location_data.setText("纬度：" + mCurrentLatitude
                        + "经度：" + mCurrentLongitude);*/
            }else{
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }
}
