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

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView

object SlidingTabLayout {
  private val TITLE_OFFSET_DIPS = 24
  private val TAB_VIEW_PADDING_DIPS = 16
  private val TAB_VIEW_TEXT_SIZE_SP = 12

  /**
    * Allows complete control over the colors drawn in the tab layout. Set with
    * {@link #setCustomTabColorizer(TabColorizer)}.
    */
  trait TabColorizer {
    /**
      * @return return the color of the indicator used when {@code position} is selected.
      */
    def getIndicatorColor(position: Int): Int

    /**
      * @return return the color of the divider drawn to the right of {@code position}.
      */
    def getDividerColor(position: Int): Int
  }
}

/**
  * To be used with ViewPager to provide a tab indicator component which give
  * constant feedback as to the user's scroll progress.
  * <p>
  * To use the component, simply add it to your view hierarchy. Then in your
  * {@link android.app.Activity} or {@link android.support.v4.app.Fragment} call
  * {@link #setViewPager(ViewPager)} providing it the ViewPager this layout is
  * being used for.
  * <p>
  * The colors can be customized in two ways. The first and simplest is to provide
  * an array of colors via {@link #setSelectedIndicatorColors(int...)} and {@link
  * #setDividerColors(int...)}. The alternative is via the {@link TabColorizer}
  * interface which provides you complete control over which color is used for
  * any individual position.
  * <p>
  * The views used as tabs can be customized by calling {@link #setCustomTabView(int, int)},
  * providing the layout ID of your custom layout.
  */
class SlidingTabLayout(context: Context, attrs: AttributeSet, defStyle: Int) extends HorizontalScrollView(context, attrs, defStyle) {
  private var mTitleOffset: Int = 0

  private var mTabViewLayoutId: Int = 0
  private var mTabViewTextViewId: Int = 0

  private var mViewPager: ViewPager = null
  private var mViewPagerPageChangeListener: ViewPager.OnPageChangeListener = null

  private var mTabStrip: SlidingTabStrip = null

  // Disable the Scroll Bar
  setHorizontalScrollBarEnabled(false)
  // Make sure that the Tab Strips fills this View
  setFillViewport(true)

  mTitleOffset = (SlidingTabLayout.TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density).toInt

  mTabStrip = new SlidingTabStrip(context)
  addView(mTabStrip, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

  def this(context: Context, attrs: AttributeSet) {
    this(context, attrs, 0)
  }

  def this(context: Context) {
    this(context, null)
  }

  /**
    * Set the custom {@link TabColorizer} to be used.
    *
    * If you only require simple custmisation then you can use
    * {@link #setSelectedIndicatorColors(int...)} and {@link #setDividerColors(int...)}
    * to achieve similar effects.
    */
  def setCustomTabColorizer(tabColorizer: SlidingTabLayout.TabColorizer): Unit = {
    mTabStrip.setCustomTabColorizer(tabColorizer)
  }

  /**
    * Sets the colors to be used for indicating the selected tab. These colors are treated as a
    * circular array. Providing one color will mean that all tabs are indicated with the same
    * color.
    */
  def setSelectedIndicatorColors(colors: Int*): Unit = {
    mTabStrip.setSelectedIndicatorColors(colors:_*)
  }

  /**
    * Sets the colors to be used for tab dividers. These colors are treated as a circular
    * array. Providing one color will mean that all tabs are indicated with the same color.
    */
  def setDividerColors(colors: Int*): Unit = {
    mTabStrip.setDividerColors(colors:_*)
  }

  /**
    * Set the {@link ViewPager.OnPageChangeListener}. When using {@link SlidingTabLayout} you
    * are required to set any {@link ViewPager.OnPageChangeListener} through this method.
    * This is so that the layout can update it's scroll position correctly.
    *
    * @see ViewPager#setOnPageChangeListener(ViewPager.OnPageChangeListener)
    */
  def setOnPageChangeListener(listener: ViewPager.OnPageChangeListener): Unit = {
    mViewPagerPageChangeListener = listener
  }

  /**
    * Set the custom layout to be inflated for the tab views.
    *
    * @param layoutResId Layout id to be inflated
    * @param textViewId id of the {@link TextView} in the inflated view
    */
  def setCustomTabView(layoutResId: Int, textViewId: Int): Unit = {
    mTabViewLayoutId = layoutResId
    mTabViewTextViewId = textViewId
  }

  /**
    * Sets the associated view pager. Note that the assumption here is that the pager content
    * (number of tabs and tab titles) does not change after this call has been made.
    */
  def setViewPager(viewPager: ViewPager): Unit = {
    mTabStrip.removeAllViews()

    mViewPager = viewPager
    if(viewPager != null) {
      viewPager.setOnPageChangeListener(new InternalViewPagerListener())
      populateTabStrip()
    }
  }

  /**
    * Create a default view to be used for tabs. This is called if a custom tab view is
    * not set via {@link #setCustomTabView(int, int)}.
    */
  protected def createDefaultTabView(context: Context): TextView = {
    val textView = new TextView(context)
    textView.setGravity(Gravity.CENTER)
    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SlidingTabLayout.TAB_VIEW_TEXT_SIZE_SP)
    textView.setTypeface(Typeface.DEFAULT_BOLD)

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      // If we're running on Honeycomb or newer, then we can use the Theme's
      // selectableItemBackground to ensure that the View has a pressed state
      val outValue = new TypedValue()
      getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
      textView.setBackgroundResource(outValue.resourceId)
    }

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      // If we're running on ICS or newer, enable all-caps to match the Action Bar tab style
      textView.setAllCaps(true)
    }

    val padding = (SlidingTabLayout.TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density).toInt
    textView.setPadding(padding, padding, padding, padding)

    textView
  }

  private def populateTabStrip(): Unit = {
    val adapter = mViewPager.getAdapter()
    val tabClickListener = new TabClickListener()

    for(i <- 0 until adapter.getCount()) {
      var tabView: View = null
      var tabTitleView: TextView = null

      if(mTabViewLayoutId != 0) {
        // If there is a custom tab view layout id set, try and inflate it
        tabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mTabStrip, false)
        tabTitleView = tabView.findViewById(mTabViewTextViewId).asInstanceOf[TextView]
      }

      if(tabView == null) {
        tabView = createDefaultTabView(getContext())
      }

      if(tabTitleView == null && tabView.isInstanceOf[TextView]) {
        tabTitleView = tabView.asInstanceOf[TextView]
      }

      tabTitleView.setText(adapter.getPageTitle(i))
      tabView.setOnClickListener(tabClickListener)

      mTabStrip.addView(tabView)
    }
  }

  override protected def onAttachedToWindow(): Unit = {
    super.onAttachedToWindow()

    if(mViewPager != null) {
      scrollToTab(mViewPager.getCurrentItem(), 0)
    }
  }

  private def scrollToTab(tabIndex: Int, positionOffset: Int): Unit = {
    val tabStripChildCount = mTabStrip.getChildCount()
    if(tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
      return
    }

    val selectedChild = mTabStrip.getChildAt(tabIndex)
    if(selectedChild != null) {
      var targetScrollX = selectedChild.getLeft() + positionOffset

      if(tabIndex > 0 || positionOffset > 0) {
        // If we're not at the first child and are mid-scroll, make sure we obey the offset
        targetScrollX -= mTitleOffset
      }

      scrollTo(targetScrollX, 0)
    }
  }

  private class InternalViewPagerListener extends ViewPager.OnPageChangeListener {
    private var mScrollState: Int = 0

    override def onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int): Unit = {
      val tabStripChildCount = mTabStrip.getChildCount()
      if((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
        return
      }

      mTabStrip.onViewPagerPageChanged(position, positionOffset)

      val selectedTitle = mTabStrip.getChildAt(position)
      val extraOffset = if(selectedTitle != null) (positionOffset * selectedTitle.getWidth()).toInt else 0
      scrollToTab(position, extraOffset)

      if(mViewPagerPageChangeListener != null) {
        mViewPagerPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels)
      }
    }

    override def onPageScrollStateChanged(state: Int): Unit = {
      mScrollState = state

      if(mViewPagerPageChangeListener != null) {
        mViewPagerPageChangeListener.onPageScrollStateChanged(state)
      }
    }

    override def onPageSelected(position: Int): Unit = {
      if(mScrollState == ViewPager.SCROLL_STATE_IDLE) {
        mTabStrip.onViewPagerPageChanged(position, 0f)
        scrollToTab(position, 0)
      }

      if(mViewPagerPageChangeListener != null) {
        mViewPagerPageChangeListener.onPageSelected(position)
      }
    }
  }

  private class TabClickListener extends View.OnClickListener {
    override def onClick(v: View): Unit = {
      for(i <- 0 until mTabStrip.getChildCount()) {
        if(v == mTabStrip.getChildAt(i)) {
          mViewPager.setCurrentItem(i)
          return
        }
      }
    }
  }
}
