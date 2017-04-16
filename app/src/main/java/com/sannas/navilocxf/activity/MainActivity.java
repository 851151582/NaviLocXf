package com.sannas.navilocxf.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.sannas.navilocxf.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 能够获取当前的位置信息
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener
        ,AMapLocationListener {
    private double mStartLatitude;
    private double mStartLongitude;
    private Button bt_start_emulator;
    private Button bt_start_gps;
    private Button bt_poi_search;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    //public AMapLocationListener mLocationListener = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initLocation();
    }

    private void initUI() {
        bt_start_emulator = (Button)findViewById(R.id.bt_start_emulator);
        bt_start_gps = (Button)findViewById(R.id.bt_start_gps);
        bt_poi_search = (Button)findViewById(R.id.bt_poi_search);
        bt_start_emulator.setOnClickListener(this);
        bt_start_gps.setOnClickListener(this);
        bt_poi_search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_start_emulator:
                //跳转到模拟导航界面
                Toast.makeText(this,"跳转到模拟导航界面",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, EmulatorActivity.class);
                intent.putExtra("mStartLatitude",mStartLatitude);
                intent.putExtra("mStartLongitude",mStartLongitude);
                startActivity(intent);
                break;
            case R.id.bt_start_gps:
                //跳转到实时导航界面
                Toast.makeText(this,"跳转到实时导航界面",Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this, GPSNaviActivity.class);
                intent1.putExtra("mStartLatitude",mStartLatitude);
                intent1.putExtra("mStartLongitude",mStartLongitude);
                startActivity(intent1);
                break;
            case R.id.bt_poi_search:
                //跳转到POI搜索界面
                Toast.makeText(this,"跳转到实时导航界面",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, POISearchActivity.class));
                break;
        }
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
}
