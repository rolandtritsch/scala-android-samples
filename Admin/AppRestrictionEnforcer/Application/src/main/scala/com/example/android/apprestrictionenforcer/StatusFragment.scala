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
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

/**
  * Provides UI for enabling the target app in this profile. The status of the app can be
  * uninstalled, hidden, or enabled depending on the situations. This fragment shows suitable
  * controls for each status.
  */
class StatusFragment extends Fragment with View.OnClickListener {
  private[this] var mTextStatus: TextView = null
  private[this] var mButtonUnhide: Button = null
  private[this] var mListener: StatusUpdatedListener = null

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    inflater.inflate(R.layout.fragment_status, container, false)
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    mTextStatus = view.findViewById(R.id.status).asInstanceOf[TextView]
    mButtonUnhide = view.findViewById(R.id.unhide).asInstanceOf[Button]
    mButtonUnhide.setOnClickListener(this)
  }

  override def onAttach(activity: Activity): Unit = {
    super.onAttach(activity)
    mListener = activity.asInstanceOf[StatusUpdatedListener]
  }

  override def onDetach(): Unit = {
    mListener = null
    super.onDetach()
  }

  override def onResume(): Unit = {
    super.onResume()
    updateUi(getActivity())
  }

  override def onClick(v: View): Unit = {
    v.getId() match {
      case R.id.unhide => {
        unhideApp(getActivity())
      }
    }
  }

  private[this] def updateUi(activity: Activity): Unit = {
    val packageManager = activity.getPackageManager()
    try {
      val info = packageManager.getApplicationInfo(
        Constants.PACKAGE_NAME_APP_RESTRICTION_SCHEMA,
        PackageManager.GET_UNINSTALLED_PACKAGES
      )
      val devicePolicyManager = activity.getSystemService(Context.DEVICE_POLICY_SERVICE).asInstanceOf[DevicePolicyManager]
      if((info.flags & ApplicationInfo.FLAG_INSTALLED) != 0) {
        if(!devicePolicyManager.isApplicationHidden(EnforcerDeviceAdminReceiver.getComponentName(activity), Constants.PACKAGE_NAME_APP_RESTRICTION_SCHEMA)) {
          // The app is ready to enforce restrictions
          // This is unlikely to happen in this sample as unhideApp() handles it.
          mListener.onStatusUpdated()
        } else {
          // The app is installed but hidden in this profile
          mTextStatus.setText(R.string.status_not_activated)
          mButtonUnhide.setVisibility(View.VISIBLE)
        }
      } else {
        // Need to reinstall the sample app
        mTextStatus.setText(R.string.status_need_reinstall)
        mButtonUnhide.setVisibility(View.GONE)
      }
    } catch {
      case e: PackageManager.NameNotFoundException => {
        // Need to reinstall the sample app
        mTextStatus.setText(R.string.status_need_reinstall)
        mButtonUnhide.setVisibility(View.GONE)
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
      Constants.PACKAGE_NAME_APP_RESTRICTION_SCHEMA,
      false
    )
    Toast.makeText(activity, "Enabled the app", Toast.LENGTH_SHORT).show()
    mListener.onStatusUpdated()
  }

  trait StatusUpdatedListener {
    def onStatusUpdated(): Unit
  }
}
