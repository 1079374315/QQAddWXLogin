package com.gsls.login.qq;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gsls.gt.GT;
import com.gsls.login.R;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

@GT.Annotations.GT_AnnotationActivity(R.layout.activity_show_data)
public class QQActivity extends GT.GT_Activity.AnnotationActivity {

    @GT.Annotations.GT_View(R.id.QQ_Data)
    private TextView QQ_Data;
    @GT.Annotations.GT_View(R.id.img_head)
    private ImageView img_head;

    private Tencent mTencent;

    @Override
    protected void initView(Bundle savedInstanceState) {
        build(this);
        log("开始登录");
        mTencent = Tencent.createInstance("101962552", getApplicationContext());//将123123123改为自己的AppID
        mTencent.login(QQActivity.this, "all", new BaseUiListener());
    }


    //QQ数据回调
    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            log("登录成功：" + o);
            toast("登录成功");

            //获取 数据 并设置
            JSONObject qqLogin = (JSONObject) o;
            try {
                String openID = qqLogin.getString("openid");  //openid用户唯一标识
                String access_token = qqLogin.getString("access_token");
                String expires = qqLogin.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(access_token, expires);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //再获取用户的详细信息
            UserInfo info = new UserInfo(getApplicationContext(), mTencent.getQQToken());
            info.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    log("获取用户信息", o);
                    QQ_Data.setText(o.toString());
                    JSONObject jb = (JSONObject) o;
                    try {
                        String name = jb.getString("nickname");
                        toast("欢迎" + name + "回来");
                        String figureurl = jb.getString("figureurl_qq_2");  //头像图片的url
                        log("figureurl:" + figureurl);
                        Glide.with(QQActivity.this).load(figureurl).into(img_head);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(UiError uiError) {
                    log("获取用户信息失败:" + uiError.errorMessage);
                }

                @Override
                public void onCancel() {
                    log("获取用户信息取消");
                }

                @Override
                public void onWarning(int i) {

                }
            });

        }

        @Override
        public void onError(UiError uiError) {
            log("登录失败：" + uiError.errorMessage);
            toast("登录失败");
        }

        @Override
        public void onCancel() {
            toast("登录取消");
        }

        @Override
        public void onWarning(int i) {

        }
    }


    //QQ回调方法
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, new BaseUiListener());
            }
        }
    }

}
