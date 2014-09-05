package kr.ewhapp.kjw.soundcam.camera;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {

	private final String TAG = "Preview";

	private SurfaceView surfaceView;
	private SurfaceHolder mHolder;
	private Camera mCamera;

	private int cameraMode;

	// private OnSurfaceCreated listener;

	private static Size mPreviewSize;
	private List<Size> mSupportedPreviewSizes;
	
	public CameraPreview(Context context, SurfaceView view) {
		
		super(context);

		this.surfaceView = view;
		mHolder = surfaceView.getHolder();
		mHolder.addCallback(this);
		
//		Log.i(TAG, "»ý¼ºÀÚ");
	}

	public void setCamera(int mode) {

		if (mCamera != null) {
			Log.i(TAG, "Camera not null");
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
		
		try {
			mCamera = Camera.open(mode);
			mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
			requestLayout();

			// get Camera parameters
			Camera.Parameters params = mCamera.getParameters();

			List<String> focusModes = params.getSupportedFocusModes();
			if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				// set the focus mode
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				// set Camera parameters
				mCamera.setParameters(params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setRelease(Camera camera) {
		mCamera = camera;
		mCamera.release();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i("SurfaceView", "Create");

		try {
			if (mCamera != null) {
				mCamera.setPreviewDisplay(holder);
			}
		} catch (IOException exception) {
			Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// We purposely disregard child measurements because act as a
		// wrapper to a SurfaceView that centers the camera preview instead
		// of stretching it.
		final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		setMeasuredDimension(width, height);

		if (mSupportedPreviewSizes != null) {
			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
		}
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed && getChildCount() > 0) {
			final View child = getChildAt(0);

			final int width = r - l;
			final int height = b - t;

			int previewWidth = width;
			int previewHeight = height;
			if (mPreviewSize != null) {
				previewWidth = mPreviewSize.width;
				previewHeight = mPreviewSize.height;
			}

			// Center the child SurfaceView within the parent.
			if (width * previewHeight > height * previewWidth) {
				final int scaledChildWidth = previewWidth * height / previewHeight;
				child.layout((width - scaledChildWidth) / 2, 0, (width + scaledChildWidth) / 2, height);
			} else {
				final int scaledChildHeight = previewHeight * width / previewWidth;
				child.layout(0, (height - scaledChildHeight) / 2, width, (height + scaledChildHeight) / 2);
			}
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (mCamera != null) {
			Log.i(TAG, "Changed");
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			requestLayout();

			mCamera.setParameters(parameters);
			mCamera.startPreview();
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			Log.d("CameraSurfaceView", "Destroy!!!!!");
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	public boolean capture(Camera.PictureCallback handler) {
		if (mCamera != null) {
			mCamera.takePicture(null, null, null, handler);
			return true;
		} else
			return false;
	}

	public SurfaceHolder getmHolder() {
		return mHolder;
	}

	public Camera getCamera() {
		return mCamera;
	}	

}
