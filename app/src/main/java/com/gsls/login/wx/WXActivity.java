package com.gsls.login.wx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gsls.gt.GT;
import com.gsls.login.R;
import com.gsls.login.wx.bean.WxBean;
import com.gsls.login.wx.bean.WxBean2;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;

@GT.Annotations.GT_AnnotationActivity(R.layout.activity_show_data)
public class WXActivity extends GT.GT_Activity.AnnotationActivity {

    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    private static final String APP_ID = "wxb859c3fc6112f86f";
    private static final String APP_SECRET = "2c256f19d54f7330e07f32cbad2fd2d8";

    // IWXAPI 是第三方app和微信通信的openApi接口
    public static IWXAPI api;

    @GT.Annotations.GT_View(R.id.QQ_TV)
    private TextView QQ_TV;
    @GT.Annotations.GT_View(R.id.QQ_Data)
    private TextView QQ_Data;
    @GT.Annotations.GT_View(R.id.img_head)
    private ImageView img_head;

    @Override
    protected void onStart() {
        super.onStart();
        GT.log("进入 onStart");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        build(this);

        QQ_TV.setText("WX");
        //注册广播
        if (isRegisterBroadcastAction()) {
            uiReceiver = new UiReceiver();
            //实例化过滤器并设置要过滤的广播
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(getClass().getName());
            registerReceiver(uiReceiver, intentFilter); //注册广播
        }

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        // 将应用的appId注册到微信
        api.registerApp(APP_ID);
        //建议动态监听微信启动广播进行注册到微信
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 将该app注册到微信
                api.registerApp(APP_ID);
            }
        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));

        //请求登录
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";//可以获取用户信息
        req.state = "wechat_sdk_demo_test";//用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击
        api.sendReq(req);

    }

    /**
     * 请求用户登录信息
     *
     * @param code
     */
    public void getAccessToken(String code) {
        //获取授权
        StringBuffer loginUrl = new StringBuffer();
        loginUrl.append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=")
                .append(APP_ID)
                .append("&secret=")
                .append(APP_SECRET)
                .append("&code=")
                .append(code)
                .append("&grant_type=authorization_code");

        GT.HttpUtil.getRequest(loginUrl.toString(), new GT.HttpUtil.OnLoadData() {
            @Override
            public void onSuccess(String response) {
                GT.log("请求成功：" + response);
                GT.Thread.runAndroid(WXActivity.this, new Runnable() {
                    @Override
                    public void run() {
                        QQ_Data.setText(response);
                    }
                });
                try {
                    WxBean wxBean = GT.JSON.fromJson(response, WxBean.class);
                    GT.log("access_token:" + wxBean.getAccess_token());
                    GT.log("openid:" + wxBean.getOpenid());
                    getUserData(wxBean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String response) {
                GT.log("请求失败：" + response);
            }
        });

    }

    /**
     * 请求用户信息
     *
     * @param wxBean
     */
    private void getUserData(WxBean wxBean) {
        String url = "https://api.weixin.qq.com/sns/userinfo?" +
                "access_token=" + wxBean.getAccess_token() +
                "&openid=" + wxBean.getOpenid();

        GT.HttpUtil.getRequest(url, new GT.HttpUtil.OnLoadData() {
            @Override
            public void onSuccess(String response) {
                GT.log("用户信息获取成功：" + response);
                GT.Thread.runAndroid(WXActivity.this, new Runnable() {
                    @Override
                    public void run() {
                        QQ_Data.setText(response);
                    }
                });
                try {
                    WxBean2 wxBean2 = GT.JSON.fromJson(response, WxBean2.class);
                    String userName = wxBean2.getNickname();//获取名称
                    GT.log("userName:" + userName);
                    GT.toast(WXActivity.this, "欢迎回来：" + userName);
                    String headUrl = wxBean2.getHeadimgurl();//获取头像
                    GT.log("headUrl1:" + headUrl);
                    GT.log("加载头像:" + img_head);
                    String finalHeadUrl = headUrl;
                    GT.Thread.runAndroid(WXActivity.this, new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WXActivity.this).load(finalHeadUrl).into(img_head);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String response) {
                GT.log("用户信息获取失败：" + response);
            }
        });

    }

    private UiReceiver uiReceiver;//定义一个刷新UI的广播

    private class UiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                String code = intent.getStringExtra("code");
                getAccessToken(code);
            }

        }

    }

}
