package com.gsls.login.wxapi;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gsls.gt.GT;
import com.gsls.login.R;
import com.gsls.login.wx.WXActivity;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    public static final String TAG = WXEntryActivity.class.getSimpleName();
    public static String code;
    public static BaseResp resp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        GT.logs("进入 onCreate");
        WXActivity.api.handleIntent(getIntent(), this);//微信登录后的回调注册
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

    }

    @Override
    public void onReq(BaseReq baseReq) {
        GT.log("onReq:" + baseReq);
        finish();
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp != null) {
            resp = baseResp;
            code = ((SendAuth.Resp) baseResp).code; //即为所需的code
            GT.log("code:" + code);
            finish();
        }
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                GT.log("onResp: 登录成功");
                GT.toast(WXEntryActivity.this,"登录成功");
                Intent intent = new Intent();
                intent.setAction(WXActivity.class.getName());
                intent.putExtra("code",code);
                sendBroadcast(intent);
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                GT.log("onResp: 用户取消");
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                GT.log("onResp: 发送请求被拒绝");
                finish();
                break;
        }
    }



}
