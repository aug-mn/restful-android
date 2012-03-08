package mn.aug.restfulandroid.rest.method;

import java.net.URI;

import mn.aug.restfulandroid.rest.AbstractRestMethod;
import mn.aug.restfulandroid.rest.Request;
import mn.aug.restfulandroid.rest.RestMethodFactory.Method;
import mn.aug.restfulandroid.rest.resource.Comments;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

public class GetCatPictureCommentsRestMethod extends AbstractRestMethod<Comments> {

	private static final String TAG = GetCatPictureCommentsRestMethod.class.getSimpleName();

	private Context mContext;

	private static final String BASE_URI = "http://www.reddit.com/r/catpictures/comments/%s/.json";

	/* The id of the story for which to get comments */
	private String mStoryId;

	private URI mUri;

	public GetCatPictureCommentsRestMethod(Context context, String storyId) {
		mContext = context.getApplicationContext();
		mStoryId = storyId;
		mUri = buildUri();
	}

	@Override
	protected Request buildRequest() {
		Request request = new Request(Method.GET, mUri, null, null);
		return request;
	}

	private URI buildUri() {
		String uriString = String.format(BASE_URI, (String) mStoryId);
		return URI.create(uriString);
	}

	@Override
	protected Comments parseResponseBody(String responseBody) throws Exception {

		JSONArray redditResponse = new JSONArray(responseBody);
		// Comments start at index 1
		JSONObject commentsRoot = (JSONObject) redditResponse.get(1);
		JSONObject comments = commentsRoot.getJSONObject("data");
		JSONArray commentsArray = comments.getJSONArray("children");

		return new Comments(commentsArray);
	}

	@Override
	protected Context getContext() {
		return mContext;
	}

	@Override
	protected boolean requiresAuthorization() {
		return false;
	}

	@Override
	protected String getLogTag() {
		return TAG;
	}

	@Override
	public URI getURI() {
		return mUri;
	}
}
