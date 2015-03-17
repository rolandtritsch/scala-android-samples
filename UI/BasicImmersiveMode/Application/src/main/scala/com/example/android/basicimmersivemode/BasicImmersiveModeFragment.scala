/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package com.example.android.basicimmersivemode

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.MenuItem
import android.view.View

import com.example.android.common.logger.Log

object BasicImmersiveModeFragment {
  private val TAG = BasicImmersiveModeFragment.getClass().getName()
}

class BasicImmersiveModeFragment extends Fragment {
  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override def onActivityCreated(savedInstanceState: Bundle): Unit = {
    super.onActivityCreated(savedInstanceState)
    val decorView = getActivity().getWindow().getDecorView()
    decorView.setOnSystemUiVisibilityChangeListener(
      new View.OnSystemUiVisibilityChangeListener() {
        override def onSystemUiVisibilityChange(i: Int): Unit = {
          val height = decorView.getHeight()
          Log.i(BasicImmersiveModeFragment.TAG, s"Current height: ${height}")
        }
      }
    )
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    if(item.getItemId() == R.id.sample_action) {
      toggleHideyBar()
    }
    true
  }

  /**
    * Detects and toggles immersive mode.
    */
  private[this] def toggleHideyBar(): Unit =  {
    // BEGIN_INCLUDE (get_current_ui_flags)
    // The UI options currently enabled are represented by a bitfield.
    // getSystemUiVisibility() gives us that bitfield.
    val uiOptions = getActivity().getWindow().getDecorView().getSystemUiVisibility()
    var newUiOptions = uiOptions
    // END_INCLUDE (get_current_ui_flags)
    // BEGIN_INCLUDE (toggle_ui_flags)
    val isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions)
    if (isImmersiveModeEnabled) {
      Log.i(BasicImmersiveModeFragment.TAG, "Turning immersive mode mode off.")
    } else {
      Log.i(BasicImmersiveModeFragment.TAG, "Turning immersive mode mode on.")
    }

    // Immersive mode: Backward compatible to KitKat (API 19).
    // Note that this flag doesn't do anything by itself, it only augments the behavior
    // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
    // all three flags are being toggled together.
    // This sample uses the "sticky" form of immersive mode, which will let the user swipe
    // the bars back in again, but will automatically make them disappear a few seconds later.
    newUiOptions = newUiOptions ^ View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    newUiOptions = newUiOptions ^ View.SYSTEM_UI_FLAG_FULLSCREEN
    newUiOptions = newUiOptions ^ View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    getActivity().getWindow().getDecorView().setSystemUiVisibility(newUiOptions)
    //END_INCLUDE (set_ui_flags)
  }
}
