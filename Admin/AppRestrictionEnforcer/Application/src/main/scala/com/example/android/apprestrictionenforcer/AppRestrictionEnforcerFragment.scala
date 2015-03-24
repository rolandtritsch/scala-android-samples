/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.example.android.apprestrictionenforcer

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.RestrictionEntry
import android.content.RestrictionsManager
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

import java.util.List

object AppRestrictionEnforcerFragment {
  /**
    * Package name of the AppRestrictionSchema sample.
    */
  private val PACKAGE_NAME_APP_RESTRICTION_SCHEMA = AppRestrictionEnforcerFragment.getClass().getPackage().getName()

  /**
    * Key for {@link SharedPreferences}
    */
  private val PREFS_KEY = AppRestrictionEnforcerFragment.getClass().getName().split('.').last

  /**
    * Key for the boolean restriction in AppRestrictionSchema.
    */
  private val RESTRICTION_KEY_SAY_HELLO = "can_say_hello"
}

/**
  * This fragment provides UI and functionality to set restrictions on the AppRestrictionSchema
  * sample.
  */
class AppRestrictionEnforcerFragment extends Fragment with View.OnClickListener with CompoundButton.OnCheckedChangeListener {
  /**
    * Default boolean value for "can_say_hello" restriction. The actual value is loaded in
    * {@link #loadRestrictions(android.app.Activity)}.
    */
  private[this] var mDefaultValueRestrictionSayHello: Boolean = false

  // UI Components
  private[this] var mTextStatus: TextView = null
  private[this] var mButtonUnhide: Button = null
  private[this] var mSwitchSayHello: Switch = null

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    inflater.inflate(R.layout.fragment_app_restriction_enforcer, container, false)
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    mTextStatus = view.findViewById(R.id.status).asInstanceOf[TextView]
    mButtonUnhide = view.findViewById(R.id.unhide).asInstanceOf[Button]
    mSwitchSayHello = view.findViewById(R.id.say_hello).asInstanceOf[Switch]
    mButtonUnhide.setOnClickListener(this)
    mSwitchSayHello.setOnCheckedChangeListener(this)
  }

  override def onResume(): Unit = {
    super.onResume()
    updateUi(getActivity())
  }

  override def onClick(view: View): Unit = {
    view.getId() match {
      case R.id.unhide => {
        unhideApp(getActivity())
      }
    }
  }

  override def onCheckedChanged(compoundButton: CompoundButton, checked: Boolean): Unit = {
    compoundButton.getId() match {
      case R.id.say_hello => {
        allowSayHello(getActivity(), checked)
      }
    }
  }

  /**
    * Updates the UI components according to the current status of AppRestrictionSchema and its
    * restriction.
    *
    * @param activity The activity
    */
  private[this] def updateUi(activity: Activity): Unit = {
    val packageManager = activity.getPackageManager()
    try {
      val info = packageManager.getApplicationInfo(
        AppRestrictionEnforcerFragment.PACKAGE_NAME_APP_RESTRICTION_SCHEMA, PackageManager.GET_UNINSTALLED_PACKAGES
      )
      val devicePolicyManager = activity.getSystemService(Context.DEVICE_POLICY_SERVICE).asInstanceOf[DevicePolicyManager]
      if(0 < (info.flags & ApplicationInfo.FLAG_INSTALLED)) {
        if(!devicePolicyManager.isApplicationHidden(EnforcerDeviceAdminReceiver.getComponentName(activity), AppRestrictionEnforcerFragment.PACKAGE_NAME_APP_RESTRICTION_SCHEMA)) {
          // The app is ready
          loadRestrictions(activity)
          mTextStatus.setVisibility(View.GONE)
          mButtonUnhide.setVisibility(View.GONE)
          mSwitchSayHello.setVisibility(View.VISIBLE)
          mSwitchSayHello.setOnCheckedChangeListener(null)
          mSwitchSayHello.setChecked(canSayHello(activity))
          mSwitchSayHello.setOnCheckedChangeListener(this)
        } else {
          // The app is installed but hidden in this profile
          mTextStatus.setText(R.string.status_not_activated)
          mTextStatus.setVisibility(View.VISIBLE)
          mButtonUnhide.setVisibility(View.VISIBLE)
          mSwitchSayHello.setVisibility(View.GONE)
        }
      } else {
        // Need to reinstall the sample app
        mTextStatus.setText(R.string.status_need_reinstall)
        mTextStatus.setVisibility(View.VISIBLE)
        mButtonUnhide.setVisibility(View.GONE)
        mSwitchSayHello.setVisibility(View.GONE)
      }
    } catch {
      case e: PackageManager.NameNotFoundException => {
        mTextStatus.setText(R.string.status_not_installed)
        mTextStatus.setVisibility(View.VISIBLE)
        mButtonUnhide.setVisibility(View.GONE)
        mSwitchSayHello.setVisibility(View.GONE)
      }
    }
  }

  /**
    * Unhides the AppRestrictionSchema sample in case it is hidden in this profile.
    *
    * @param activity The activity
    */
  private[this] def unhideApp(activity: Activity): Unit = {
    val devicePolicyManager = activity.getSystemService(Context.DEVICE_POLICY_SERVICE).asInstanceOf[DevicePolicyManager]
    devicePolicyManager.setApplicationHidden(
      EnforcerDeviceAdminReceiver.getComponentName(activity),
      AppRestrictionEnforcerFragment.PACKAGE_NAME_APP_RESTRICTION_SCHEMA, false)
    Toast.makeText(activity, "Enabled the app", Toast.LENGTH_SHORT).show()
    updateUi(activity)
  }

  /**
    * Loads the restrictions for the AppRestrictionSchema sample. In this implementation, we just
    * read the default value for the "can_say_hello" restriction.
    *
    * @param activity The activity
    */
  private[this] def loadRestrictions(activity: Activity): Unit = {
    val restrictionsManager = activity.getSystemService(Context.RESTRICTIONS_SERVICE).asInstanceOf[RestrictionsManager]
    val restrictions = restrictionsManager.getManifestRestrictions(AppRestrictionEnforcerFragment.PACKAGE_NAME_APP_RESTRICTION_SCHEMA).toArray
    restrictions.foreach(restriction => {
      val r = restriction.asInstanceOf[RestrictionEntry]
      if (AppRestrictionEnforcerFragment.RESTRICTION_KEY_SAY_HELLO.equals(r.getKey())) {
        mDefaultValueRestrictionSayHello = r.getSelectedState()
      }
    })
  }

  /**
    * Returns whether the AppRestrictionSchema is currently allowed to say hello to its user. Note
    * that a profile/device owner needs to remember each restriction value on its own.
    *
    * @param activity The activity
    * @return True if the AppRestrictionSchema is allowed to say hello
    */
  private[this] def canSayHello(activity: Activity): Boolean = {
    val prefs = activity.getSharedPreferences(AppRestrictionEnforcerFragment.PREFS_KEY, Context.MODE_PRIVATE)
    prefs.getBoolean(AppRestrictionEnforcerFragment.RESTRICTION_KEY_SAY_HELLO, mDefaultValueRestrictionSayHello)
  }

  /**
    * Sets the value for the "cay_say_hello" restriction of AppRestrictionSchema.
    *
    * @param activity The activity
    * @param allow    The value to be set for the restriction.
    */
  private[this] def allowSayHello(activity: Activity, allow: Boolean): Unit = {
    val devicePolicyManager = activity.getSystemService(Context.DEVICE_POLICY_SERVICE).asInstanceOf[DevicePolicyManager]
    val restrictions = new Bundle()
    restrictions.putBoolean(AppRestrictionEnforcerFragment.RESTRICTION_KEY_SAY_HELLO, allow)
    devicePolicyManager.setApplicationRestrictions(
      EnforcerDeviceAdminReceiver.getComponentName(activity),
      AppRestrictionEnforcerFragment.PACKAGE_NAME_APP_RESTRICTION_SCHEMA,
      restrictions
    )
    // The profile/device owner needs to remember the current state of restrictions on its own
    activity.getSharedPreferences(
      AppRestrictionEnforcerFragment.PREFS_KEY,
      Context.MODE_PRIVATE
    )
      .edit()
      .putBoolean(AppRestrictionEnforcerFragment.RESTRICTION_KEY_SAY_HELLO, allow)
      .apply()
    Toast.makeText(
      activity,
      if(allow) R.string.allowed else R.string.disallowed,
      Toast.LENGTH_SHORT
    )
      .show()
  }
}
