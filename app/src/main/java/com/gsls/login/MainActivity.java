package com.gsls.login;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.gsls.gt.GT;
import com.gsls.login.qq.QQActivity;
import com.gsls.login.wx.WXActivity;

@GT.Annotations.GT_AnnotationActivity(R.layout.activity_main)
public class MainActivity extends GT.GT_Activity.AnnotationActivity {

    @GT.Annotations.GT_View(R.id.img_head)
    private ImageView img_head;

    @Override
    protected void initView(Bundle savedInstanceState) {
        build(this);
    }

    @GT.Annotations.GT_Click({R.id.btn_QQ, R.id.btn_WX})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_QQ:
                toast("切换QQ");
                GT.startAct(QQActivity.class);
                break;
            case R.id.btn_WX:
                toast("切换WX");
                log("切换WX");
                GT.startAct(WXActivity.class);
                break;
        }


    }

}
