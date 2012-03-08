package mn.aug.restfulandroid.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mn.aug.restfulandroid.provider.CatPicturesProviderContract.CommentsTable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * CatPictures API
 * 
 * @author jeremyhaberman
 */
public class CatPicturesServiceHelper {

	@SuppressWarnings("unused")
	private static final String TAG = CatPicturesServiceHelper.class.getSimpleName();

	public static String ACTION_REQUEST_RESULT = "REQUEST_RESULT";
	public static String EXTRA_REQUEST_ID = "EXTRA_REQUEST_ID";
	public static String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";

	private List<Long> pendingRequests = new ArrayList<Long>();
	private Object pendingRequestsLock = new Object();
	private Context mAppContext;
	private ServiceResultReceiver serviceCallback;

	private Class<? extends CatPicturesService> mCatPicturesServiceClass;

	public CatPicturesServiceHelper(Context context) {
		this.mAppContext = context.getApplicationContext();
		this.serviceCallback = new ServiceResultReceiver();
		this.mCatPicturesServiceClass = DefaultCatPicturesService.class;
	}

	/**
	 * Initiates a request to get cat pictures via the CatPicturesService
	 * 
	 * @return request ID
	 */
	public long getCatPictures() {

		long requestId = generateRequestID();
		synchronized (pendingRequestsLock) {			
			pendingRequests.add(requestId);
		}


		Intent intent = new Intent(this.mAppContext, mCatPicturesServiceClass);
		intent.putExtra(CatPicturesService.METHOD_EXTRA, CatPicturesService.METHOD_GET);
		intent.putExtra(CatPicturesService.RESOURCE_TYPE_EXTRA,
				CatPicturesService.RESOURCE_TYPE_CAT_PICTURES);
		intent.putExtra(CatPicturesService.SERVICE_CALLBACK_EXTRA, serviceCallback);
		intent.putExtra(EXTRA_REQUEST_ID, requestId);

		this.mAppContext.startService(intent);

		return requestId;
	}

	/**
	 * Initiates a request to get cat pictures via the CatPicturesService
	 * 
	 * @return request ID
	 */
	public long getComments(String catPictureId) {

		long requestId = generateRequestID();
		synchronized (pendingRequestsLock) {
			pendingRequests.add(requestId);
		}

		Intent intent = new Intent(this.mAppContext, mCatPicturesServiceClass);
		intent.putExtra(CatPicturesService.METHOD_EXTRA, CatPicturesService.METHOD_GET);
		intent.putExtra(CatPicturesService.RESOURCE_TYPE_EXTRA,
				CatPicturesService.RESOURCE_TYPE_COMMENTS);
		intent.putExtra(CatPicturesService.SERVICE_CALLBACK_EXTRA, serviceCallback);
		intent.putExtra(EXTRA_REQUEST_ID, requestId);

		Bundle requestParams = new Bundle();
		requestParams.putString(CommentsTable.CAT_PICTURE_ID, catPictureId);
		intent.putExtra(CatPicturesService.EXTRA_REQUEST_PARAMETERS, requestParams);

		this.mAppContext.startService(intent);

		return requestId;
	}

	/**
	 * Submit a new comment for the cat picture
	 * @param catPictureId
	 * @param comment
	 */
	public long submitNewComment(String catPictureId, String comment) {
		long requestId = generateRequestID();
		synchronized (pendingRequestsLock) {			
			pendingRequests.add(requestId);
		}

		Intent intent = new Intent(this.mAppContext, mCatPicturesServiceClass);
		intent.putExtra(CatPicturesService.METHOD_EXTRA, CatPicturesService.METHOD_POST);
		intent.putExtra(CatPicturesService.RESOURCE_TYPE_EXTRA,
				CatPicturesService.RESOURCE_TYPE_COMMENTS);
		intent.putExtra(CatPicturesService.SERVICE_CALLBACK_EXTRA, serviceCallback);
		intent.putExtra(EXTRA_REQUEST_ID, requestId);

		Bundle requestParams = new Bundle();
		requestParams.putString(CommentsTable.CAT_PICTURE_ID, catPictureId);
		requestParams.putString(CommentsTable.COMMENT_TEXT, comment);
		intent.putExtra(CatPicturesService.EXTRA_REQUEST_PARAMETERS, requestParams);

		this.mAppContext.startService(intent);

		return requestId;

	}


	public void setCatPicturesServiceClass(Class<? extends CatPicturesService> service) {
		mCatPicturesServiceClass = service;
	}

	private long generateRequestID() {
		long requestId = UUID.randomUUID().getLeastSignificantBits();
		return requestId;
	}

	/**
	 * Determines whether a request is pending
	 * 
	 * @param requestId
	 *            the ID of a previous request
	 * @return <code>true</code> if the request is pending, <code>false</code>
	 *         otherwise
	 */
	public boolean isRequestPending(long requestId) {
		synchronized (pendingRequestsLock) {			
			return this.pendingRequests.contains(requestId);
		}
	}


	/**
	 * Handles the result code and data during a callback from
	 * CatPicturesService
	 * 
	 * @param resultCode
	 *            the result code
	 * @param resultData
	 *            the data
	 */
	class ServiceResultReceiver extends ResultReceiver {


		public ServiceResultReceiver() {
			super(null);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			Intent origIntent = (Intent) resultData
					.getParcelable(CatPicturesService.ORIGINAL_INTENT_EXTRA);

			if (origIntent != null) {
				long requestId = origIntent.getLongExtra(EXTRA_REQUEST_ID, 0);
				synchronized (pendingRequestsLock) {			
					pendingRequests.remove(requestId);
				}

				Intent resultBroadcast = new Intent(ACTION_REQUEST_RESULT);
				resultBroadcast.putExtra(EXTRA_REQUEST_ID, requestId);
				resultBroadcast.putExtra(EXTRA_RESULT_CODE, resultCode);

				mAppContext.sendBroadcast(resultBroadcast);
			}
		}

	}
}
