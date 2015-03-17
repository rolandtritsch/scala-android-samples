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

package com.example.android.common.view

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout

import scala.collection.mutable

object SlidingTabStrip {
  private val DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS = 2
  private val DEFAULT_BOTTOM_BORDER_COLOR_ALPHA = 0x26.toByte
  private val SELECTED_INDICATOR_THICKNESS_DIPS = 8
  private val DEFAULT_SELECTED_INDICATOR_COLOR = 0xFF33B5E5.toInt

  private val DEFAULT_DIVIDER_THICKNESS_DIPS = 1
  private val DEFAULT_DIVIDER_COLOR_ALPHA = 0x20.toByte
  private val DEFAULT_DIVIDER_HEIGHT = 0.5f

  /**
    * Set the alpha value of the {@code color} to be the given {@code alpha} value.
    */
  private def setColorAlpha(color: Int, alpha: Byte): Int = {
    Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
  }

  /**
    * Blend {@code color1} and {@code color2} using the given ratio.
    *
    * @param ratio of which to blend. 1.0 will return {@code color1}, 0.5 will give an even blend,
    *              0.0 will return {@code color2}.
    */
  private def blendColors(color1: Int, color2: Int, ratio: Float): Int = {
    val inverseRation = 1f - ratio
    val r = ((Color.red(color1) * ratio) + (Color.red(color2) * inverseRation)).toInt
    val g = ((Color.green(color1) * ratio) + (Color.green(color2) * inverseRation)).toInt
    val b = ((Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation)).toInt
    Color.rgb(r, g, b)
  }
}

class SlidingTabStrip(context: Context, attrs: AttributeSet) extends LinearLayout(context, attrs) {
  setWillNotDraw(false)

  val density = getResources().getDisplayMetrics().density

  val outValue = new TypedValue()
  context.getTheme().resolveAttribute(R.attr.colorForeground, outValue, true)
  val themeForegroundColor =  outValue.data

  mDefaultBottomBorderColor = SlidingTabStrip.setColorAlpha(themeForegroundColor, SlidingTabStrip.DEFAULT_BOTTOM_BORDER_COLOR_ALPHA)

  mDefaultTabColorizer = new SimpleTabColorizer()
  mDefaultTabColorizer.setIndicatorColors(SlidingTabStrip.DEFAULT_SELECTED_INDICATOR_COLOR)
  mDefaultTabColorizer.setDividerColors(SlidingTabStrip.setColorAlpha(themeForegroundColor, SlidingTabStrip.DEFAULT_DIVIDER_COLOR_ALPHA))

  mBottomBorderThickness = (SlidingTabStrip.DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS * density).toInt
  mBottomBorderPaint = new Paint()
  mBottomBorderPaint.setColor(mDefaultBottomBorderColor)

  mSelectedIndicatorThickness = (SlidingTabStrip.SELECTED_INDICATOR_THICKNESS_DIPS * density).toInt
  mSelectedIndicatorPaint = new Paint()

  mDividerHeight = SlidingTabStrip.DEFAULT_DIVIDER_HEIGHT
  mDividerPaint = new Paint()
  mDividerPaint.setStrokeWidth((SlidingTabStrip.DEFAULT_DIVIDER_THICKNESS_DIPS * density).toInt)

  private var mBottomBorderThickness: Int = 0
  private var mBottomBorderPaint: Paint = null

  private var mSelectedIndicatorThickness: Int = 0
  private var mSelectedIndicatorPaint: Paint = null

  private var mDefaultBottomBorderColor: Int = 0

  private var mDividerPaint: Paint = null
  private var mDividerHeight: Float = 0.0f

  private var mSelectedPosition = 0
  private var mSelectionOffset = 0.0f

  private var mCustomTabColorizer: SlidingTabLayout.TabColorizer = null
  private var mDefaultTabColorizer: SimpleTabColorizer = null

  def this(context: Context) {
    this(context, null)
  }

  def setCustomTabColorizer(customTabColorizer: SlidingTabLayout.TabColorizer): Unit = {
    mCustomTabColorizer = customTabColorizer
    invalidate()
  }

  def setSelectedIndicatorColors(colors: Int*): Unit = {
    // Make sure that the custom colorizer is removed
    mCustomTabColorizer = null
    mDefaultTabColorizer.setIndicatorColors(colors:_*)
    invalidate()
  }

  def setDividerColors(colors: Int*): Unit = {
    // Make sure that the custom colorizer is removed
    mCustomTabColorizer = null
    mDefaultTabColorizer.setDividerColors(colors:_*)
    invalidate()
  }

  def onViewPagerPageChanged(position: Int, positionOffset: Float): Unit = {
    mSelectedPosition = position
    mSelectionOffset = positionOffset
    invalidate()
  }

  override protected def onDraw(canvas: Canvas): Unit = {
    val height = getHeight()
    val childCount = getChildCount()
    val dividerHeightPx = (Math.min(Math.max(0f, mDividerHeight), 1f) * height).toInt
    val tabColorizer = if(mCustomTabColorizer != null) mCustomTabColorizer else mDefaultTabColorizer

    // Thick colored underline below the current selection
    if(childCount > 0) {
      val selectedTitle = getChildAt(mSelectedPosition)
      var left = selectedTitle.getLeft()
      var right = selectedTitle.getRight()
      var color = tabColorizer.getIndicatorColor(mSelectedPosition)

      if(mSelectionOffset > 0f && mSelectedPosition < (getChildCount() - 1)) {
        val nextColor = tabColorizer.getIndicatorColor(mSelectedPosition + 1)
        if(color != nextColor) {
          color = SlidingTabStrip.blendColors(nextColor, color, mSelectionOffset)
        }

        // Draw the selection partway between the tabs
        val nextTitle = getChildAt(mSelectedPosition + 1)
        left = (mSelectionOffset * nextTitle.getLeft() + (1.0f - mSelectionOffset) * left).toInt
        right = (mSelectionOffset * nextTitle.getRight() + (1.0f - mSelectionOffset) * right).toInt
      }

      mSelectedIndicatorPaint.setColor(color)

      canvas.drawRect(left, height - mSelectedIndicatorThickness, right, height, mSelectedIndicatorPaint)
    }

    // Thin underline along the entire bottom edge
    canvas.drawRect(0, height - mBottomBorderThickness, getWidth(), height, mBottomBorderPaint)

    // Vertical separators between the titles
    val separatorTop = (height - dividerHeightPx) / 2
    for(i <- 0 until childCount) {
      val child = getChildAt(i)
      mDividerPaint.setColor(tabColorizer.getDividerColor(i))
      canvas.drawLine(child.getRight(), separatorTop, child.getRight(), separatorTop + dividerHeightPx, mDividerPaint)
    }
  }

  private class SimpleTabColorizer extends SlidingTabLayout.TabColorizer {
    private val mIndicatorColors = mutable.ArrayBuffer[Int]()
    private val mDividerColors = mutable.ArrayBuffer[Int]()

    override def getIndicatorColor(position: Int): Int = {
      mIndicatorColors(position % mIndicatorColors.length)
    }

    override def getDividerColor(position: Int): Int = {
      mDividerColors(position % mDividerColors.length)
    }

    def setIndicatorColors(colors: Int*): Unit = {
      mIndicatorColors.clear
      colors.foreach(mIndicatorColors.append(_))
    }

    def setDividerColors(colors: Int*): Unit = {
      mDividerColors.clear
      colors.foreach(mDividerColors.append(_))
    }
  }
}
