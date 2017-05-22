/**
 *OnTaskResultListener
 *11/20/14 11:20 AM
 *demo
 **/
package com.joe.oil.imagepicker;

/**
 * User: demo
 * wuchangqi@meet-future.com
 * Date: 11/20/14
 * Time: 11:20 AM
 */

/**
 * 异步任务执行完后回调接口
 */
public interface OnTaskResultListener {
	/**
	 * 回调函数
	 *
	 * @param success 是否成功
	 * @param error   错误信息，[成功的时候错误信息为空]
	 * @param result  获取到的结果
	 */
	void onResult (final boolean success, final String error, final Object result);
}
