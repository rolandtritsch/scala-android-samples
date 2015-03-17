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

package com.example.android.advancedimmersivemode

import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.view.Menu
import android.view.MenuItem
import android.widget.ViewAnimator

import com.example.android.common.activities.SampleActivityBase
import com.example.android.common.logger.Log
import com.example.android.common.logger.LogFragment
import com.example.android.common.logger.LogWrapper
import com.example.android.common.logger.MessageOnlyLogFilter

object MainActivity {
  val TAG = MainActivity.getClass().getName()
}

/**
  * A simple launcher activity containing a summary sample description, sample log and a custom
  * {@link android.support.v4.app.Fragment} which can display a view.
  * <p>
  * For devices with displays with a width of 720dp or greater, the sample log is always visible,
  * on other devices it's visibility is controlled by an item on the Action Bar.
  */
class MainActivity extends SampleActivityBase {
  private var mLogShown: Boolean = false

  override protected def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if(savedInstanceState == null) {
      val transaction = getSupportFragmentManager().beginTransaction()
      val fragment = new AdvancedImmersiveModeFragment()
      transaction.replace(R.id.sample_content_fragment, fragment)
      transaction.commit()
    }
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater().inflate(R.menu.main, menu)
    true
  }

  override def onPrepareOptionsMenu(menu: Menu): Boolean = {
    val logToggle = menu.findItem(R.id.menu_toggle_log)
    logToggle.setVisible(findViewById(R.id.sample_output).isInstanceOf[ViewAnimator])
    logToggle.setTitle(if(mLogShown) R.string.sample_hide_log else R.string.sample_show_log)

    super.onPrepareOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId() match {
      case R.id.menu_toggle_log => {
        mLogShown = !mLogShown
        val output = findViewById(R.id.sample_output).asInstanceOf[ViewAnimator]
        if(mLogShown) {
          output.setDisplayedChild(1)
        } else {
          output.setDisplayedChild(0)
        }
        supportInvalidateOptionsMenu()
        true
      }
    }
    super.onOptionsItemSelected(item)
  }

  /** Create a chain of targets that will receive log data */
  override def initializeLogging(): Unit = {
    // Wraps Android's native log framework.
    val logWrapper = new LogWrapper()
    // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
    Log.setLogNode(logWrapper)

    // Filter strips out everything except the message text.
    val msgFilter = new MessageOnlyLogFilter()
    logWrapper.setNext(msgFilter)

    // On screen logging via a fragment with a TextView.
    val logFragment = getSupportFragmentManager().findFragmentById(R.id.log_fragment).asInstanceOf[LogFragment]
    msgFilter.setNext(logFragment.getLogView())

    Log.i(MainActivity.TAG, "Ready")
  }
}
