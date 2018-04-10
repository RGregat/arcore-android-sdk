/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.inio.ar.core.examples.java.helloaroptional;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.ar.core.ArCoreApk;

/**
 * Created by inio on 4/10/18.
 */

public class MainActivity extends Activity {
  private Button mArButton;
  private TextView mTotalCallsText;
  private TextView mTotalTimeText;
  private TextView mLastAvailabilityText;
  private TextView mIsTransientText;
  private TextView mIsSupportedText;
  private long mFirstCallMillis;
  private int mTotalCalls;
  private boolean startedArCheck;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mArButton = findViewById(R.id.start_button);
    mTotalCallsText = findViewById(R.id.total_calls);
    mTotalTimeText = findViewById(R.id.total_latency);
    mLastAvailabilityText = findViewById(R.id.last_availability);
    mIsTransientText = findViewById(R.id.is_transient);
    mIsSupportedText = findViewById(R.id.is_supported);

    mArButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(MainActivity.this, ArActivity.class));
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (!startedArCheck) {
      startedArCheck = true;
      maybeEnableArButton();
    }
  }

  void maybeEnableArButton() {
    if (mFirstCallMillis == 0) {
      mFirstCallMillis = System.currentTimeMillis();
    }
    ++mTotalCalls;
    // Likely called from Activity.onCreate() of an activity with AR buttons.
    ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
    if (availability.isTransient()) {
      // re-query at 5Hz while we check compatibility.
      new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
          maybeEnableArButton();
        }
      }, 200);
    }
    mTotalCallsText.setText(Integer.toString(mTotalCalls));
    mTotalTimeText.setText(Long.toString(System.currentTimeMillis()-mFirstCallMillis) + " ms");
    mLastAvailabilityText.setText(availability.name());
    mIsTransientText.setText(Boolean.toString(availability.isTransient()));
    mIsSupportedText.setText(Boolean.toString(availability.isSupported()));


    if (availability.isSupported()) {
      mArButton.setVisibility(View.VISIBLE);
      mArButton.setEnabled(true);
    } else { // unsupported or unknown
      mArButton.setVisibility(View.INVISIBLE);
      mArButton.setEnabled(false);
    }
  }
}
