package com.joe.oil.http;

import java.util.List;

import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.joe.oil.entity.PlanDetail;
import com.joe.oil.util.Constants;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.PreferencesCookieStore;

/**
 * Created by Administrator on 2014/9/12.
 */
public class XUtilsHttp {

	private static XUtilsHttp httpClient = null;
	private Context context;
	private HttpUtils httpUtil;
	private PreferencesCookieStore preferencesCookieStore;

	private static final String TAG = "RequestHttp";
	/**
	 * 网络请求失败
	 */
	public static final int REQUEST_FAILER = 0;

	/**
	 * 网络请求成功
	 */
	public static final int REQUEST_SUCCESS = 1;

	public static XUtilsHttp getInstance(Context context) {
		if (httpClient == null) {
			httpClient = new XUtilsHttp(context);
		}
		return httpClient;
	}

	public XUtilsHttp(Context context) {
		this.context = context;
		preferencesCookieStore = new PreferencesCookieStore(context);
		BasicClientCookie cookie = new BasicClientCookie("oil_cookie", "sichuanyl");
		cookie.setDomain("mapi.comsys.net.cn");
		cookie.setPath("/");
		preferencesCookieStore.addCookie(cookie);
		httpUtil = new HttpUtils();
		httpUtil.configTimeout(1000 * 8);
		// httpUtil.configDefaultHttpCacheExpiry(1000 * 60 * 10);
		httpUtil.configCookieStore(preferencesCookieStore); // 设置自动管理 cookie
	}

	public HttpUtils getHttpUtil() {
		return httpUtil;
	}

	public void requestSubmitGasList(final Handler mHandler, List<PlanDetail> planDetails, String flag, String chargerId) {
		String url = Constants.BASE_URL + "/api/patrol/plan/update";
		RequestParams params = new RequestParams();
		params.addBodyParameter("flag", flag);
		for (int i = 0; i < planDetails.size(); i++) {
			params.addBodyParameter("children[" + i + "].item.id", planDetails.get(i).getItemId());
			params.addBodyParameter("children[" + i + "].patrolTime", planDetails.get(i).getPatrolTime());
			params.addBodyParameter("children[" + i + "].handleAdvice", planDetails.get(i).getHandleAdvice());
			params.addBodyParameter("children[" + i + "].memo", planDetails.get(i).getMemo());
			if (planDetails.get(i).getPicId().equals("") || planDetails.get(i).getPicId() == null) {
				params.addBodyParameter("children[" + i + "].picIds", planDetails.get(i).getPicId());
			} else {
				params.addBodyParameter("children[" + i + "].picIds", planDetails.get(i).getPicId() + ",");
			}
			params.addBodyParameter("children[" + i + "].result", planDetails.get(i).getResult());
			params.addBodyParameter("children[" + i + "].status", planDetails.get(i).getExceptionStatus());
			params.addBodyParameter("children[" + i + "].videoId", planDetails.get(i).getVideoId());
			params.addBodyParameter("children[" + i + "].updateTime", planDetails.get(i).getUpdateTime());
			params.addBodyParameter("children[" + i + "].handleMemo", planDetails.get(i).getHandleMemo());
			params.addBodyParameter("children[" + i + "].charger.id", chargerId);
			params.addBodyParameter("children[" + i + "].logging", planDetails.get(i).getLogging());

			Log.d(TAG, "itemId: " + planDetails.get(i).getItemId() + "  PatrolTime: " + planDetails.get(i).getPatrolTime() + "   updateTime:  " + planDetails.get(i).getUpdateTime());
		}
		Log.d(TAG, "params: " + params.toString());
		httpUtil.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				String tags = "SubmitGasListFailer:  ";
				Message msg = mHandler.obtainMessage();
				msg.what = REQUEST_FAILER;
				msg.obj = 0;
				Log.d(TAG, tags + arg1);
				msg.sendToTarget();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				String tags = "SubmitGasListSuccess:  ";
				Log.d(TAG, tags + arg0.result);
				Message msg = mHandler.obtainMessage();
				msg.what = REQUEST_SUCCESS;
				msg.obj = arg0.result;
				msg.sendToTarget();
			}
		});
	}

}
