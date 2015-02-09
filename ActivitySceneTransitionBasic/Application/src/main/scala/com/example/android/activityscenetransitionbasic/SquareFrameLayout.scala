/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.example.android.activityscenetransitionbasic

import android.content.Context
import android.util.AttributeSet
import android.view.View.MeasureSpec
import android.widget.FrameLayout

/**
  * {@link android.widget.FrameLayout} which forces itself to be laid out as square.
  */
class SquareFrameLayout(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) extends FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

  def this(context: Context) {
    this(context, null, 0, 0)
  }

  def this(context: Context, attrs: AttributeSet) {
    this(context, attrs, 0, 0)
  }

  def this(context: Context, attrs: AttributeSet, defStyleAttr: Int) {
    this(context, attrs, defStyleAttr, 0)
  }

  override protected def onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int): Unit = {
    val widthSize = MeasureSpec.getSize(widthMeasureSpec)
    val heightSize = MeasureSpec.getSize(heightMeasureSpec)

    if(widthSize == 0 && heightSize == 0) {
      // If there are no constraints on size, let FrameLayout measure
      super.onMeasure(widthMeasureSpec, heightMeasureSpec)

      // Now use the smallest of the measured dimensions for both dimensions
     val minSize = Math.min(getMeasuredWidth(), getMeasuredHeight())
      setMeasuredDimension(minSize, minSize)
    } else {
      var size = 0
      if (widthSize == 0 || heightSize == 0) {
        // If one of the dimensions has no restriction on size, set both dimensions to be the
        // on that does
        size = Math.max(widthSize, heightSize)
      } else {
        // Both dimensions have restrictions on size, set both dimensions to be the
        // smallest of the two
        size = Math.min(widthSize, heightSize)
      }

      val newMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
      super.onMeasure(newMeasureSpec, newMeasureSpec)
    }
  }
}
