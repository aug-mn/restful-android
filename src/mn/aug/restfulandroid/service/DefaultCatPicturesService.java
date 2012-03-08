package mn.aug.restfulandroid.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class DefaultCatPicturesService extends IntentService implements CatPicturesService {

	private static final int REQUEST_INVALID = -1;

	private ResultReceiver mCallback;

	private Intent mOriginalRequestIntent;

	public DefaultCatPicturesService() {
		super("DefaultCatPicturesService");
	}

	@Override
	protected void onHandleIntent(Intent requestIntent) {

		mOriginalRequestIntent = requestIntent;

		// Get request data from Intent
		String method = requestIntent.getStringExtra(CatPicturesService.METHOD_EXTRA);
		int resourceType = requestIntent.getIntExtra(CatPicturesService.RESOURCE_TYPE_EXTRA, -1);
		mCallback = requestIntent.getParcelableExtra(CatPicturesService.SERVICE_CALLBACK_EXTRA);
		Bundle parameters = requestIntent.getBundleExtra(CatPicturesService.EXTRA_REQUEST_PARAMETERS);
		ResourceProcessor processor = CatPicturesProcessorFactory.getInstance(this).getProcessor(resourceType);

		switch (resourceType) {
		case RESOURCE_TYPE_CAT_PICTURES:

			if (method.equalsIgnoreCase(METHOD_GET)) {
				processor.getResource(makeCatPicturesProcessorCallback(), parameters);
			} else {
				mCallback.send(REQUEST_INVALID, getOriginalIntentBundle());
			}
			break;
			
		case RESOURCE_TYPE_COMMENTS:

			if (method.equalsIgnoreCase(METHOD_GET)) {
				processor.getResource(makeCatPicturesProcessorCallback(), parameters);
			} else if (method.equalsIgnoreCase(METHOD_POST)){
				// TODO Add hooks to processor once its complete
			} else {
				mCallback.send(REQUEST_INVALID, getOriginalIntentBundle());
			}
			break;

		default:
			mCallback.send(REQUEST_INVALID, getOriginalIntentBundle());
			break;
		}

	}

	@Override
	public ResourceProcessorCallback makeCatPicturesProcessorCallback() {
		ResourceProcessorCallback callback = new ResourceProcessorCallback() {

			@Override
			public void send(int resultCode, String resourceId) {
				if (mCallback != null) {
					mCallback.send(resultCode, getOriginalIntentBundle());
				}
			}
		};
		return callback;
	}

	protected Bundle getOriginalIntentBundle() {
		Bundle originalRequest = new Bundle();
		originalRequest.putParcelable(ORIGINAL_INTENT_EXTRA, mOriginalRequestIntent);
		return originalRequest;
	}
}
