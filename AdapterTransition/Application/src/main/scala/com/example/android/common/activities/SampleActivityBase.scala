/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.common.activities

import android.os.Bundle
import android.support.v4.app.FragmentActivity

import com.example.android.common.logger.Log
import com.example.android.common.logger.LogWrapper

object SampleActivityBase {
  val TAG = SampleActivityBase.getClass().getName()
}

/**
  * Base launcher activity, to handle most of the common plumbing for samples.
  */
class SampleActivityBase extends FragmentActivity {
  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
  }

  override protected def onStart(): Unit = {
    super.onStart()
    initializeLogging()
  }

  /** Set up targets to receive log data */
  private def initializeLogging(): Unit = {
    // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
    // Wraps Android's native log framework
    val logWrapper = new LogWrapper()
    Log.setLogNode(logWrapper)
    Log.i(SampleActivityBase.TAG, "Ready")
  }
}
