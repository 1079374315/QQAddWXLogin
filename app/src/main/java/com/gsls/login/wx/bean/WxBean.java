/**
  * Copyright 2021 json.cn 
  */
package com.gsls.login.wx.bean;

/**
 * Auto-generated: 2021-07-07 16:17:26
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class WxBean {

    private String access_token;
    private int expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    private String unionid;
    public void setAccess_token(String access_token) {
         this.access_token = access_token;
     }
     public String getAccess_token() {
         return access_token;
     }

    public void setExpires_in(int expires_in) {
         this.expires_in = expires_in;
     }
     public int getExpires_in() {
         return expires_in;
     }

    public void setRefresh_token(String refresh_token) {
         this.refresh_token = refresh_token;
     }
     public String getRefresh_token() {
         return refresh_token;
     }

    public void setOpenid(String openid) {
         this.openid = openid;
     }
     public String getOpenid() {
         return openid;
     }

    public void setScope(String scope) {
         this.scope = scope;
     }
     public String getScope() {
         return scope;
     }

    public void setUnionid(String unionid) {
         this.unionid = unionid;
     }
     public String getUnionid() {
         return unionid;
     }

}