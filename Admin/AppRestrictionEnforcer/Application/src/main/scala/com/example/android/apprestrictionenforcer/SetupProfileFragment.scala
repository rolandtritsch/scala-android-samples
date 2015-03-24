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
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import android.app.admin.DevicePolicyManager

object SetupProfileFragment {
  private val REQUEST_PROVISION_MANAGED_PROFILE = 1

  def newInstance(): SetupProfileFragment = {
    new SetupProfileFragment()
  }
}

/**
  * This {@link Fragment} handles initiation of managed profile provisioning.
  */
class SetupProfileFragment extends Fragment with View.OnClickListener {
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    inflater.inflate(R.layout.fragment_setup_profile, container, false)
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    view.findViewById(R.id.set_up_profile).setOnClickListener(this)
  }

  override def onClick(view: View): Unit = {
    view.getId() match {
      case R.id.set_up_profile => {
        provisionManagedProfile()
      }
    }
  }

  /**
    * Initiates the managed profile provisioning. If we already have a managed profile set up on
    * this device, we will get an error dialog in the following provisioning phase.
    */
  private[this] def provisionManagedProfile(): Unit = {
    val activity = getActivity()
    if (activity != null) {
      val intent = new Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE)
      intent.putExtra(
        DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME,
        activity.getApplicationContext().getPackageName()
      )
      intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, EnforcerDeviceAdminReceiver.getComponentName(activity))
      if (intent.resolveActivity(activity.getPackageManager()) != null) {
        startActivityForResult(intent, SetupProfileFragment.REQUEST_PROVISION_MANAGED_PROFILE)
        activity.finish()
      } else {
        Toast.makeText(
          activity,
          "Device provisioning is not enabled. Stopping.",
          Toast.LENGTH_SHORT
        ).show()
      }
    }
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    if (requestCode == SetupProfileFragment.REQUEST_PROVISION_MANAGED_PROFILE) {
      if (resultCode == Activity.RESULT_OK) {
        Toast.makeText(getActivity(), "Provisioning done.", Toast.LENGTH_SHORT).show()
      } else {
        Toast.makeText(getActivity(), "Provisioning failed.", Toast.LENGTH_SHORT).show()
      }
      return
    }
    super.onActivityResult(requestCode, resultCode, data)
  }
}
