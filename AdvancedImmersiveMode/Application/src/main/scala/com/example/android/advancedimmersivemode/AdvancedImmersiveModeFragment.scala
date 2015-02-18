/*
 * Copyright (C) 2012 The Android Open Source Project
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
package com.example.android.advancedimmersivemode

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox

import com.example.android.common.logger.Log

object AdvancedImmersiveModeFragment {
  val TAG = AdvancedImmersiveModeFragment.getClass().getName()
}

/**
  * Demonstrates how to update the app's UI by toggling immersive mode.
  * Checkboxes are also made available for toggling other UI flags which can
  * alter the behavior of immersive mode.
  */
class AdvancedImmersiveModeFragment extends Fragment {
  private var mHideNavCheckBox: CheckBox = null
  private var mHideStatusBarCheckBox: CheckBox = null
  private var mImmersiveModeCheckBox: CheckBox = null
  private var mImmersiveModeStickyCheckBox: CheckBox = null
  private var mLowProfileCheckBox: CheckBox = null

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, state: Bundle): View = {
    val flagsView = inflater.inflate(R.layout.fragment_flags, container, false)
    mLowProfileCheckBox = flagsView.findViewById(R.id.flag_enable_lowprof).asInstanceOf[CheckBox]
    mHideNavCheckBox = flagsView.findViewById(R.id.flag_hide_navbar).asInstanceOf[CheckBox]
    mHideStatusBarCheckBox = flagsView.findViewById(R.id.flag_hide_statbar).asInstanceOf[CheckBox]
    mImmersiveModeCheckBox = flagsView.findViewById(R.id.flag_enable_immersive).asInstanceOf[CheckBox]
    mImmersiveModeStickyCheckBox = flagsView.findViewById(R.id.flag_enable_immersive_sticky).asInstanceOf[CheckBox]

    val toggleFlagsButton = flagsView.findViewById(R.id.btn_changeFlags).asInstanceOf[Button]
    toggleFlagsButton.setOnClickListener(new View.OnClickListener() {
      override def onClick(view: View): Unit = {
        toggleUiFlags()
      }
    })

    val presetsImmersiveModeButton = flagsView.findViewById(R.id.btn_immersive).asInstanceOf[Button]
    presetsImmersiveModeButton.setOnClickListener(new View.OnClickListener() {
      override def onClick(view: View): Unit = {

        // BEGIN_INCLUDE(immersive_presets)
        // For immersive mode, the FULLSCREEN, HIDE_HAVIGATION and IMMERSIVE
        // flags should be set (you can use IMMERSIVE_STICKY instead of IMMERSIVE
        // as appropriate for your app).  The LOW_PROFILE flag should be cleared.

        // Immersive mode is primarily for situations where the user will be
        // interacting with the screen, like games or reading books.
        var uiOptions = flagsView.getSystemUiVisibility()
        uiOptions = uiOptions & ~View.SYSTEM_UI_FLAG_LOW_PROFILE
        uiOptions = uiOptions | View.SYSTEM_UI_FLAG_FULLSCREEN
        uiOptions = uiOptions | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        uiOptions = uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE
        uiOptions = uiOptions & ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        flagsView.setSystemUiVisibility(uiOptions)
        // END_INCLUDE(immersive_presets)

        dumpFlagStateToLog(uiOptions)

        // The below code just updates the checkboxes to reflect which flags have been set.
        mLowProfileCheckBox.setChecked(false)
        mHideNavCheckBox.setChecked(true)
        mHideStatusBarCheckBox.setChecked(true)
        mImmersiveModeCheckBox.setChecked(true)
        mImmersiveModeStickyCheckBox.setChecked(false)
      }
    })


    val presetsLeanbackModeButton = flagsView.findViewById(R.id.btn_leanback).asInstanceOf[Button]
    presetsLeanbackModeButton.setOnClickListener(new View.OnClickListener() {
      override def onClick(view: View): Unit = {
        // BEGIN_INCLUDE(leanback_presets)
        // For leanback mode, only the HIDE_NAVE and HIDE_STATUSBAR flags
        // should be checked.  In this case IMMERSIVE should *not* be set,
        // since this mode is left as soon as the user touches the screen.
        var uiOptions = flagsView.getSystemUiVisibility()
        uiOptions = uiOptions & ~View.SYSTEM_UI_FLAG_LOW_PROFILE
        uiOptions = uiOptions | View.SYSTEM_UI_FLAG_FULLSCREEN
        uiOptions = uiOptions | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        uiOptions = uiOptions & ~View.SYSTEM_UI_FLAG_IMMERSIVE
        uiOptions = uiOptions & ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        flagsView.setSystemUiVisibility(uiOptions)
        // END_INCLUDE(leanback_presets)

        dumpFlagStateToLog(uiOptions)

        // The below code just updates the checkboxes to reflect which flags have been set.
        mLowProfileCheckBox.setChecked(false)
        mHideNavCheckBox.setChecked(true)
        mHideStatusBarCheckBox.setChecked(true)
        mImmersiveModeCheckBox.setChecked(false)
        mImmersiveModeStickyCheckBox.setChecked(false)
      }
    })

    // Setting these flags makes the content appear under the navigation
    // bars, so that showing/hiding the nav bars doesn't resize the content
    // window, which can be jarring.
    var uiOptions = flagsView.getSystemUiVisibility()
    uiOptions = uiOptions | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    uiOptions = uiOptions | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    uiOptions = uiOptions | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    flagsView.setSystemUiVisibility(uiOptions)

    flagsView
  }

  /**
    * Helper method to dump flag state to the log.
    * @param uiFlags Set of UI flags to inspect
    */
  def dumpFlagStateToLog(uiFlags: Int): Unit = {
    if((uiFlags & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
      Log.i(AdvancedImmersiveModeFragment.TAG, "SYSTEM_UI_FLAG_LOW_PROFILE is set")
    } else {
      Log.i(AdvancedImmersiveModeFragment.TAG, "SYSTEM_UI_FLAG_LOW_PROFILE is unset")
    }

    if((uiFlags & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0) {
      Log.i(AdvancedImmersiveModeFragment.TAG, "SYSTEM_UI_FLAG_FULLSCREEN is set")
    } else {
      Log.i(AdvancedImmersiveModeFragment.TAG, "SYSTEM_UI_FLAG_FULLSCREEN is unset")
    }

    if((uiFlags & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0) {
      Log.i(AdvancedImmersiveModeFragment.TAG, "SYSTEM_UI_FLAG_HIDE_NAVIGATION is set")
    } else {
      Log.i(AdvancedImmersiveModeFragment.TAG, "SYSTEM_UI_FLAG_HIDE_NAVIGATION is unset")
    }

    if((uiFlags & View.SYSTEM_UI_FLAG_IMMERSIVE) != 0) {
      Log.i(AdvancedImmersiveModeFragment.TAG, "SYSTEM_UI_FLAG_IMMERSIVE is set")
    } else {
      Log.i(AdvancedImmersiveModeFragment.TAG, "SYSTEM_UI_FLAG_IMMERSIVE is unset")
    }

    if((uiFlags & View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) != 0) {
      Log.i(AdvancedImmersiveModeFragment.TAG, "SYSTEM_UI_FLAG_IMMERSIVE_STICKY is set")
    } else {
      Log.i(AdvancedImmersiveModeFragment.TAG, "SYSTEM_UI_FLAG_IMMERSIVE_STICKY is unset")
    }
  }

  /**
    * Detects and toggles immersive mode (also known as "hidey bar" mode).
    */
  def toggleUiFlags(): Unit = {
    // BEGIN_INCLUDE (get_current_ui_flags)
    // The "Decor View" is the parent view of the Activity.  It's also conveniently the easiest
    // one to find from within a fragment, since there's a handy helper method to pull it, and
    // we don't have to bother with picking a view somewhere deeper in the hierarchy and calling
    // "findViewById" on it.
    val decorView = getActivity().getWindow().getDecorView()
    val uiOptions = decorView.getSystemUiVisibility()
    var newUiOptions = uiOptions
    // END_INCLUDE (get_current_ui_flags)

    // BEGIN_INCLUDE (toggle_lowprofile_mode)
    // Low profile mode doesn't resize the screen at all, but it covers the nav & status bar
    // icons with black so they're less distracting.  Unlike "full screen" and "hide nav bar,"
    // this mode doesn't interact with immersive mode at all, but it's instructive when running
    // this sample to observe the differences in behavior.
    if(mLowProfileCheckBox.isChecked()) {
      newUiOptions = newUiOptions | View.SYSTEM_UI_FLAG_LOW_PROFILE
    } else {
      newUiOptions = newUiOptions & ~View.SYSTEM_UI_FLAG_LOW_PROFILE
    }
    // END_INCLUDE (toggle_lowprofile_mode)

    // BEGIN_INCLUDE (toggle_fullscreen_mode)
    // When enabled, this flag hides non-critical UI, such as the status bar,
    // which usually shows notification icons, battery life, etc
    // on phone-sized devices.  The bar reappears when the user swipes it down.  When immersive
    // mode is also enabled, the app-drawable area expands, and when the status bar is swiped
    // down, it appears semi-transparently and slides in over the app, instead of pushing it
    // down.
    if (mHideStatusBarCheckBox.isChecked()) {
      newUiOptions = newUiOptions | View.SYSTEM_UI_FLAG_FULLSCREEN
    } else {
      newUiOptions = newUiOptions & ~View.SYSTEM_UI_FLAG_FULLSCREEN
    }
    // END_INCLUDE (toggle_fullscreen_mode)

    // BEGIN_INCLUDE (toggle_hidenav_mode)
    // When enabled, this flag hides the black nav bar along the bottom,
    // where the home/back buttons are.  The nav bar normally instantly reappears
    // when the user touches the screen.  When immersive mode is also enabled, the nav bar
    // stays hidden until the user swipes it back.
    if(mHideNavCheckBox.isChecked()) {
      newUiOptions = newUiOptions | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    } else {
      newUiOptions = newUiOptions & ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    // END_INCLUDE (toggle_hidenav_mode)

    // BEGIN_INCLUDE (toggle_immersive_mode)
    // Immersive mode doesn't do anything without at least one of the previous flags
    // enabled.  When enabled, it allows the user to swipe the status and/or nav bars
    // off-screen.  When the user swipes the bars back onto the screen, the flags are cleared
    // and immersive mode is automatically disabled.
    if(mImmersiveModeCheckBox.isChecked()) {
      newUiOptions = newUiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE
    } else {
      newUiOptions = newUiOptions & ~View.SYSTEM_UI_FLAG_IMMERSIVE
    }
    // END_INCLUDE (toggle_immersive_mode)

    // BEGIN_INCLUDE (toggle_immersive_mode_sticky)
    // There's actually two forms of immersive mode, normal and "sticky".  Sticky immersive mode
    // is different in 2 key ways:
    //
    // * Uses semi-transparent bars for the nav and status bars
    // * This UI flag will *not* be cleared when the user interacts with the UI.
    //   When the user swipes, the bars will temporarily appear for a few seconds and then
    //   disappear again.
    if(mImmersiveModeStickyCheckBox.isChecked()) {
      newUiOptions = newUiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    } else {
      newUiOptions = newUiOptions & ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
    // END_INCLUDE (toggle_immersive_mode_sticky)

    // BEGIN_INCLUDE (set_ui_flags)
    //Set the new UI flags.
    decorView.setSystemUiVisibility(newUiOptions)
    // END_INCLUDE (set_ui_flags)

    dumpFlagStateToLog(uiOptions)
  }
}
