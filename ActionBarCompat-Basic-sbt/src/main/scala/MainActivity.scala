/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.example.android.actionbarcompat.basic

import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.ActionBarActivity
import android.view.{Menu, MenuItem}
import android.widget.Toast

/**
  * This sample shows you how to use ActionBarCompat to create a basic Activity which displays
  * action items. It covers inflating items from a menu resource, as well as adding an item in code.
  *
  * This Activity extends from {@link ActionBarActivity}, which provides all of the function
  * necessary to display a compatible Action Bar on devices running Android v2.1+.
  */
class MainActivity extends ActionBarActivity {
  override protected def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.sample_main)
  }

  // BEGIN_INCLUDE(create_menu)
  /**
    * Use this method to instantiate your menu, and add your items to it. You
    * should return true if you have added items to it and want the menu to be displayed.
    */
  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    // Inflate our menu from the resources by using the menu inflater.
    getMenuInflater().inflate(R.menu.main, menu)

    // It is also possible add items here. Use a generated id from
    // resources (ids.xml) to ensure that all menu ids are distinct.
    val locationItem = menu.add(0, R.id.menu_location, 0, R.string.menu_location)
    locationItem.setIcon(R.drawable.ic_action_location)

    // Need to use MenuItemCompat methods to call any action item related methods
    MenuItemCompat.setShowAsAction(locationItem, MenuItem.SHOW_AS_ACTION_IF_ROOM)

    true
  }
  // END_INCLUDE(create_menu)

  // BEGIN_INCLUDE(menu_item_selected)
  /**
    * This method is called when one of the menu items to selected. These items
    * can be on the Action Bar, the overflow menu, or the standard options menu. You
    * should return true if you handle the selection.
    */
  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId() match {
      case R.id.menu_refresh => {
        // Here we might start a background refresh task
        Toast.makeText(getApplicationContext(), "Refresh!", Toast.LENGTH_SHORT).show()
        true
      }
      case R.id.menu_location => {
        // Here we might call LocationManager.requestLocationUpdates()
        Toast.makeText(getApplicationContext(), "Location!", Toast.LENGTH_SHORT).show()
        true
      }
      case R.id.menu_settings => {
        // Here we would open up our settings activity
        Toast.makeText(getApplicationContext(), "Settings!", Toast.LENGTH_SHORT).show()
        true
      }
    }

    super.onOptionsItemSelected(item)
  }
  // END_INCLUDE(menu_item_selected)
}
