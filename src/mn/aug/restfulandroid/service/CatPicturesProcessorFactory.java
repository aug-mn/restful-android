package mn.aug.restfulandroid.service;

import android.content.Context;
import mn.aug.restfulandroid.service.CatPicturesService;

public class CatPicturesProcessorFactory {

	private static CatPicturesProcessorFactory mSingleton;
	private Context mContext;
	private ResourceProcessor mDefaultProcessor;

	public void setDefaultProcessor(ResourceProcessor processor) {
		mDefaultProcessor = processor;
	}

	public ResourceProcessor getProcessor(int resourceType) {
		switch (resourceType) {
		case CatPicturesService.RESOURCE_TYPE_CAT_PICTURES:
			return new CatPicturesProcessor(mContext);
		case CatPicturesService.RESOURCE_TYPE_COMMENTS:
			return new CatPictureCommentsProcessor(mContext);
		}
		return mDefaultProcessor;
	}

	public static CatPicturesProcessorFactory getInstance(Context context) {
		if (mSingleton == null) {
			mSingleton = new CatPicturesProcessorFactory(context.getApplicationContext());
		}
		return mSingleton;
	}
	
	private CatPicturesProcessorFactory(Context context) {
		mContext = context;
		mDefaultProcessor = new CatPicturesProcessor(mContext);
	};

}
