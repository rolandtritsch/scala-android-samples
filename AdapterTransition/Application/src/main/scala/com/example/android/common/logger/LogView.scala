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
package com.example.android.common.logger

import android.app.Activity
import android.content.Context
import android.util._
import android.widget.TextView

/**
  * Simple TextView which is used to output log data received through the LogNode interface.
  */
class LogView(context: Context, attrs: AttributeSet, defStyle: Int) extends TextView(context, attrs, defStyle) with LogNode {

  def this(context: Context) {
    this(context, null, 0)
  }

  def this(context: Context, attrs: AttributeSet) {
    this(context, attrs, 0)
  }

  // The next LogNode in the chain.
  private var mNext: LogNode = null

  /**
    * Formats the log data and prints it out to the LogView.
    * @param priority Log level of the data being logged.  Verbose, Error, etc.
    * @param tag Tag for for the log data.  Can be used to organize log statements.
    * @param msg The actual message to be logged. The actual message to be logged.
    * @param tr If an exception was thrown, this can be sent along for the logging facilities
    *           to extract and print useful information.
    */
  override def println(priority: Int, tag: String, msg: String, tr: Throwable): Unit = {
    var priorityStr: String = null

    // For the purposes of this View, we want to print the priority as readable text.
    priority match {
      case android.util.Log.VERBOSE => priorityStr = "VERBOSE"
      case android.util.Log.DEBUG => priorityStr = "DEBUG"
      case android.util.Log.INFO => priorityStr = "INFO"
      case android.util.Log.WARN => priorityStr = "WARN"
      case android.util.Log.ERROR => priorityStr = "ERROR"
      case android.util.Log.ASSERT => priorityStr = "ASSERT"
      case _ => priorityStr = "!!! UNKOWN !!!"
    }

    // Handily, the Log class has a facility for converting a stack trace into a usable string.
    var exceptionStr: String = null
    if(tr != null) {
      exceptionStr = android.util.Log.getStackTraceString(tr)
    }

    // Take the priority, tag, message, and exception, and concatenate as necessary
    // into one usable line of text.
    val outputBuilder = new StringBuilder()

    var delimiter = "\t"
    appendIfNotNull(outputBuilder, priorityStr, delimiter)
    appendIfNotNull(outputBuilder, tag, delimiter)
    appendIfNotNull(outputBuilder, msg, delimiter)
    appendIfNotNull(outputBuilder, exceptionStr, delimiter)

    // In case this was originally called from an AsyncTask or some other off-UI thread,
    // make sure the update occurs within the UI thread.
    getContext().asInstanceOf[Activity].runOnUiThread(new Thread(new Runnable() {
      override def run() {
        // Display the text we just generated within the LogView.
        appendToLog(outputBuilder.toString())
      }
    }))

    if(mNext != null) {
      mNext.println(priority, tag, msg, tr)
    }
  }

  def getNext(): LogNode = {
    mNext
  }

  def setNext(node: LogNode): Unit = {
    mNext = node
  }

  /** Takes a string and adds to it, with a separator, if the bit to be added isn't null. Since
    * the logger takes so many arguments that might be null, this method helps cut out some of the
    * agonizing tedium of writing the same 3 lines over and over.
    * @param source StringBuilder containing the text to append to.
    * @param addStr The String to append
    * @param delimiter The String to separate the source and appended strings. A tab or comma,
    *                  for instance.
    * @return The fully concatenated String as a StringBuilder
    */
  private def appendIfNotNull(source: StringBuilder, addStr: String, delimiter: String): StringBuilder = {
    if(addStr != null && addStr.length() > 0) {
      source.append(addStr).append(delimiter)
    }
    source
  }

  /** Outputs the string as a new line of log data in the LogView. */
  def appendToLog(s: String): Unit = {
    append("\n" + s)
  }
}
