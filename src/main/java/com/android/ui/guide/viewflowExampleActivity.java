package com.android.ui.guide;

import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.android.master.R;
import com.android.ui.guide.ViewFlow.ViewSwitchListener;

/**
 * 
 * 引导页activity 需要写到你自己的项目中 
 * 
 */

public class viewflowExampleActivity extends Activity {

	private ViewFlow viewFlow;
	private ImageAdapter imageAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_viewflow_activity);

		viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		viewFlow.setAdapter(imageAdapter, 0);

		CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		viewFlow.setFlowIndicator(indic);

		viewFlow.setOnViewSwitchListener(new ViewSwitchListener() {

			@Override
			public void onSwitched(View view, int position) {
				if (imageAdapter.getCount() == (position + 1)) {

					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(viewflowExampleActivity.this, "下一步", Toast.LENGTH_LONG).show();
						}
					}, 2000);
				}
			}
		});
	}

	TimerTask timeTask1 = new TimerTask() {

		@Override
		public void run() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(viewflowExampleActivity.this, "下一步", Toast.LENGTH_LONG).show();
				}
			});
		}
	};

}
