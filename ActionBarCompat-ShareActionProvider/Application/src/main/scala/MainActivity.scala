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

package com.example.android.actionbarcompat.shareactionprovider

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarActivity
import android.support.v7.widget.ShareActionProvider
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.example.android.actionbarcompat.shareactionprovider.content.ContentItem

import java.util.ArrayList

object MainActivity {
  /**
    * @return An ArrayList of ContentItem's to be displayed in this sample
    */
  def getSampleContent(): ArrayList[ContentItem] = {
    val items = new ArrayList[ContentItem]()

    items.add(new ContentItem(ContentItem.CONTENT_TYPE_IMAGE, "photo_1.jpg"))
    items.add(new ContentItem(ContentItem.CONTENT_TYPE_TEXT, R.string.quote_1))
    items.add(new ContentItem(ContentItem.CONTENT_TYPE_TEXT, R.string.quote_2))
    items.add(new ContentItem(ContentItem.CONTENT_TYPE_IMAGE, "photo_2.jpg"))
    items.add(new ContentItem(ContentItem.CONTENT_TYPE_TEXT, R.string.quote_3))
    items.add(new ContentItem(ContentItem.CONTENT_TYPE_IMAGE, "photo_3.jpg"))

    items
  }
}

/**
  * This sample shows you how a provide a {@link ShareActionProvider} with ActionBarCompat,
  * backwards compatible to API v7.
  * <p>
  * The sample contains a {@link ViewPager} which displays content of differing types: image and
  * text. When a new item is selected in the ViewPager, the ShareActionProvider is updated with
  * a share intent specific to that content.
  * <p>
  * This Activity extends from {@link ActionBarActivity}, which provides all of the function
  * necessary to display a compatible Action Bar on devices running Android v2.1+.
  */
class MainActivity extends ActionBarActivity {
  // The items to be displayed in the ViewPager
  val  mItems = MainActivity.getSampleContent()

  // Keep reference to the ShareActionProvider from the menu
  var mShareActionProvider: ShareActionProvider = null

  override protected def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    // Set content view (which contains a CheeseListFragment)
    setContentView(R.layout.sample_main)

    // Retrieve the ViewPager from the content view
    val vp = findViewById(R.id.viewpager).asInstanceOf[ViewPager]

    // Set an OnPageChangeListener so we are notified when a new item is selected
    vp.setOnPageChangeListener(mOnPageChangeListener)

    // Finally set the adapter so the ViewPager can display items
    vp.setAdapter(mPagerAdapter)
  }

  // BEGIN_INCLUDE(get_sap)
  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    // Inflate the menu resource
    getMenuInflater().inflate(R.menu.main_menu, menu)

    // Retrieve the share menu item
    val shareItem = menu.findItem(R.id.menu_share)

    // Now get the ShareActionProvider from the item
    mShareActionProvider = MenuItemCompat.getActionProvider(shareItem).asInstanceOf[ShareActionProvider]

    // Get the ViewPager's current item position and set its ShareIntent.
    val currentViewPagerItem = (findViewById(R.id.viewpager).asInstanceOf[ViewPager]).getCurrentItem()
    setShareIntent(currentViewPagerItem)

    super.onCreateOptionsMenu(menu)
  }
  // END_INCLUDE(get_sap)

  /**
    * A PagerAdapter which instantiates views based on the ContentItem's content type.
    */
  val mPagerAdapter = new PagerAdapter() {
    var mInflater: LayoutInflater = null

    override def getCount(): Int = {
      mItems.size()
    }

    override def isViewFromObject(v: View, o: Object): Boolean = {
      v == o
    }
/*
    override def destroyItem(container: ViewGroup, position: Int, object: Object): Unit = {
      // Just remove the view from the ViewPager
      container.removeView((View) object)
    }
*/
    override def instantiateItem(container: ViewGroup, position: Int): Object = {
      // Ensure that the LayoutInflater is instantiated
      if (mInflater == null) {
        mInflater = LayoutInflater.from(MainActivity.this)
      }

      // Get the item for the requested position
      val item = mItems.get(position)

      // The view we need to inflate changes based on the type of content
      item.contentType match {
        case ContentItem.CONTENT_TYPE_TEXT => {
          // Inflate item layout for text
          val tv = mInflater.inflate(R.layout.item_text, container, false).asInstanceOf[TextView]

          // Set text content using it's resource id
          tv.setText(item.contentResourceId)

          // Add the view to the ViewPager
          container.addView(tv)
          tv
        }

        case ContentItem.CONTENT_TYPE_IMAGE => {
          // Inflate item layout for images
          val iv = mInflater.inflate(R.layout.item_image, container, false).asInstanceOf[ImageView]

          // Load the image from it's content URI
          iv.setImageURI(item.getContentUri())

          // Add the view to the ViewPager
          container.addView(iv)
          iv
        }
      }

      null
    }
  }

  private def setShareIntent(position: Int): Unit = {
    // BEGIN_INCLUDE(update_sap)
    if(mShareActionProvider != null) {
      // Get the currently selected item, and retrieve it's share intent
      val item = mItems.get(position)
      val shareIntent = item.getShareIntent(MainActivity.this)

      // Now update the ShareActionProvider with the new share intent
      mShareActionProvider.setShareIntent(shareIntent)
    }
    // END_INCLUDE(update_sap)
  }

  /**
    * A OnPageChangeListener used to update the ShareActionProvider's share intent
    * when a new item is selected in the ViewPager.
    */
  private val mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
    override def onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int): Unit =  {
      // NO-OP
    }

    override def onPageSelected(position: Int): Unit = {
      setShareIntent(position)
    }

    override def onPageScrollStateChanged(state: Int): Unit = {
      // NO-OP
    }
  }
}
