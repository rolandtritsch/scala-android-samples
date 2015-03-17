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

package com.example.android.actionbarcompat.styled

import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarActivity
import android.view.Menu
import android.widget.Toast

/**
  * This sample shows you how to use ActionBarCompat with a customized theme. It utilizes a split
  * action bar when running on a device with a narrow display, and show three tabs.
  *
  * This Activity extends from {@link ActionBarActivity}, which provides all of the function
  * necessary to display a compatible Action Bar on devices running Android v2.1+.
  *
  * The interesting bits of this sample start in the theme files
  * ('res/values/styles.xml' and 'res/values-v14</styles.xml').
  *
  * Many of the drawables used in this sample were generated with the
  * 'Android Action Bar Style Generator': http://jgilfelt.github.io/android-actionbarstylegenerator
  */
class MainActivity extends ActionBarActivity with ActionBar.TabListener {

  override protected def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.sample_main)

    // Set the Action Bar to use tabs for navigation
    val ab = getSupportActionBar()
    ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS)

    // Add three tabs to the Action Bar for display
    ab.addTab(ab.newTab().setText("Tab 1").setTabListener(this))
    ab.addTab(ab.newTab().setText("Tab 2").setTabListener(this))
    ab.addTab(ab.newTab().setText("Tab 3").setTabListener(this))
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    // Inflate menu from menu resource (res/menu/main)
    getMenuInflater().inflate(R.menu.main, menu)
    super.onCreateOptionsMenu(menu)
  }

  // Implemented from ActionBar.TabListener
  override def onTabSelected(tab: ActionBar.Tab, fragmentTransaction: FragmentTransaction): Unit = {
    // This is called when a tab is selected.
    Toast.makeText(getApplicationContext(), s">${tab.getText()}< selected",  Toast.LENGTH_SHORT)
  }

  // Implemented from ActionBar.TabListener
  override def onTabUnselected(tab: ActionBar.Tab, fragmentTransaction: FragmentTransaction): Unit = {
    // This is called when a previously selected tab is unselected.
    Toast.makeText(getApplicationContext(), s">${tab.getText()}< unselected",  Toast.LENGTH_SHORT)
  }

  // Implemented from ActionBar.TabListener
  override def onTabReselected(tab: ActionBar.Tab, fragmentTransaction: FragmentTransaction): Unit = {
    // This is called when a previously selected tab is selected again.
    Toast.makeText(getApplicationContext(), s">${tab.getText()}< reselected",  Toast.LENGTH_SHORT)
  }
}
