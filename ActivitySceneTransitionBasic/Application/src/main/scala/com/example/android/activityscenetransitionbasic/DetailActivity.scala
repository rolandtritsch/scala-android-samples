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

import com.squareup.picasso.Picasso

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.transition.Transition
import android.widget.ImageView
import android.widget.TextView

import android.util.Log

object DetailActivity {
  // Extra name for the ID parameter
  val EXTRA_PARAM_ID = "detail:_id"

  // View name of the header image. Used for activity scene transitions
  val VIEW_NAME_HEADER_IMAGE = "detail:header:image"

  // View name of the header title. Used for activity scene transitions
  val VIEW_NAME_HEADER_TITLE = "detail:header:title"

  val TAG = DetailActivity.getClass().getName()
}

/**
  * Our secondary Activity which is launched from {@link MainActivity}. Has a simple detail UI
  * which has a large banner image, title and body text.
  */
class DetailActivity extends Activity {
  var mHeaderImageView: ImageView = null
  var mHeaderTitle: TextView = null

  private var mItem: Item = null

  override protected def onCreate(savedInstanceState: Bundle): Unit = {
    Log.d(DetailActivity.TAG, s"Enter onCreate ...")
    super.onCreate(savedInstanceState)
    setContentView(R.layout.details)

    // Retrieve the correct Item instance, using the ID provided in the Intent
    mItem = Item.getItem(getIntent().getIntExtra(DetailActivity.EXTRA_PARAM_ID, 0))

    mHeaderImageView = findViewById(R.id.imageview_header).asInstanceOf[ImageView]
    mHeaderTitle = findViewById(R.id.textview_title).asInstanceOf[TextView]

    // BEGIN_INCLUDE(detail_set_view_name)
    /**
      * Set the name of the view's which will be transition to, using the static values above.
      * This could be done in the layout XML, but exposing it via static variables allows easy
      * querying from other Activities
      */
    ViewCompat.setTransitionName(mHeaderImageView, DetailActivity.VIEW_NAME_HEADER_IMAGE)
    ViewCompat.setTransitionName(mHeaderTitle, DetailActivity.VIEW_NAME_HEADER_TITLE)
    // END_INCLUDE(detail_set_view_name)

    loadItem()
    Log.d(DetailActivity.TAG, s"... leave onCreate!")
  }

  private def loadItem(): Unit = {
    Log.d(DetailActivity.TAG, s"Enter loadItem ...")
    // Set the title TextView to the item's name and author
    mHeaderTitle.setText(getString(R.string.image_header, mItem.mName, mItem.mAuthor))

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && addTransitionListener()) {
      // If we're running on Lollipop and we have added a listener to the shared element
      // transition, load the thumbnail. The listener will load the full-size image when
      // the transition is complete.
      loadThumbnail()
    } else {
      // If all other cases we should just load the full-size image now
      loadFullSizeImage()
    }
    Log.d(DetailActivity.TAG, s"... leave loadItem!")
  }

  /**
    * Load the item's thumbnail image into our {@link ImageView}.
    */
  private def loadThumbnail(): Unit = {
    Picasso.`with`(mHeaderImageView.getContext())
      .load(mItem.getThumbnailUrl())
      .noFade()
      .into(mHeaderImageView)
  }

  /**
    * Load the item's full-size image into our {@link ImageView}.
    */
  private def loadFullSizeImage(): Unit = {
    Picasso.`with`(mHeaderImageView.getContext())
      .load(mItem.getPhotoUrl())
      .noFade()
      .noPlaceholder()
      .into(mHeaderImageView)
  }

  /**
    * Try and add a {@link Transition.TransitionListener} to the entering shared element
    * {@link Transition}. We do this so that we can load the full-size image after the
    * transition has completed.
    *
    * @return true if we were successful in adding a listener to the enter transition
    */
  private def addTransitionListener(): Boolean = {
    val transition = getWindow().getSharedElementEnterTransition()

    if(transition != null) {
      // There is an entering shared element transition so add a listener to it
      transition.addListener(new Transition.TransitionListener() {
        override def onTransitionEnd(transition: Transition): Unit = {
          Log.d(DetailActivity.TAG, s"Enter onTransitionEnd ...")
          // As the transition has ended, we can now load the full-size image
          loadFullSizeImage()

          // Make sure we remove ourselves as a listener
          transition.removeListener(this)
          Log.d(DetailActivity.TAG, s"... leave onTransitionEnd!")
        }

        override def onTransitionStart(transition: Transition): Unit = {
          // No-op
        }

        override def onTransitionCancel(transition: Transition): Unit =  {
          // Make sure we remove ourselves as a listener
          transition.removeListener(this)
        }

        override def onTransitionPause(transition: Transition): Unit = {
          // No-op
        }

        override def onTransitionResume(transition: Transition): Unit = {
          // No-op
        }
      })
      true
    }

    // If we reach here then we have not added a listener
    false
  }
}
