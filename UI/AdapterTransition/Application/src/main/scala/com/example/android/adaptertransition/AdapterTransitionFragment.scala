/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package com.example.android.adaptertransition

import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.transition.AutoTransition
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.ListView
import android.widget.Toast

object AdapterTransitionFragment {
  /**
    * Since the transition framework requires all relevant views in a view hierarchy
    * to be marked with IDs, we use this ID to mark the root view.
    */
  private val ROOT_ID = 1

  /**
    * A tag for saving state whether the mAbsListView is ListView or GridView.
    */
  private val STATE_IS_LISTVIEW = "is_listview"

  def apply(): AdapterTransitionFragment = {
    new AdapterTransitionFragment()
  }
}

/**
  * Main screen for AdapterTransition sample.
  */
class AdapterTransitionFragment extends Fragment with Transition.TransitionListener {
  /**
    * This is where we place our AdapterView (ListView / GridView).
    */
  private var mContent: FrameLayout = null

  /**
    * This is where we carry out the transition.
    */
  private var mCover: FrameLayout = null

  /**
    * This list shows our contents. It can be ListView or GridView, and we toggle between them
    * using the transition framework.
    */
  private var mAbsListView: AbsListView = null

  /**
    * This is our contents.
    */
  private var mAdapter: MeatAdapter = null

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    // If savedInstanceState is available, we restore the state whether the list is a ListView
    // or a GridView.
    val isListView = if(null == savedInstanceState) true else {
      savedInstanceState.getBoolean(AdapterTransitionFragment.STATE_IS_LISTVIEW, true)
    }
    inflateAbsList(inflater, container, isListView)
    inflater.inflate(R.layout.fragment_adapter_transition, container, false)
  }

  override def onSaveInstanceState(outState: Bundle): Unit = {
    super.onSaveInstanceState(outState)
    outState.putBoolean(AdapterTransitionFragment.STATE_IS_LISTVIEW, mAbsListView.isInstanceOf[ ListView])
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    // Retaining references for FrameLayouts that we use later.
    mContent = view.findViewById(R.id.content).asInstanceOf[FrameLayout]
    mCover = view.findViewById(R.id.cover).asInstanceOf[FrameLayout]
    // We are attaching the list to the screen here.
    mContent.addView(mAbsListView)
  }

  override def onCreateOptionsMenu(menu: Menu, inflater: MenuInflater): Unit = {
    inflater.inflate(R.menu.fragment_adapter_transition, menu)
  }

  override def onPrepareOptionsMenu(menu: Menu): Unit = {
    // We change the look of the icon every time the user toggles between list and grid.
    val item = menu.findItem(R.id.action_toggle)
    if(item != null) {
      if (mAbsListView. isInstanceOf[ListView]) {
        item.setIcon(R.drawable.ic_action_grid)
        item.setTitle(R.string.show_as_grid)
      } else {
        item.setIcon(R.drawable.ic_action_list)
        item.setTitle(R.string.show_as_list)
      }
    }
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId() match {
      case R.id.action_toggle => {
        toggle()
        true
      }
      case _ => {
        false
      }
    }
  }

  override def onTransitionStart(transition: Transition): Unit = {
  }

  // BEGIN_INCLUDE(on_transition_end)
  override def onTransitionEnd(transition: Transition): Unit = {
    // When the transition ends, we remove all the views from the overlay and hide it.
    mCover.removeAllViews()
    mCover.setVisibility(View.INVISIBLE)
  }
  // END_INCLUDE(on_transition_end)

  override def onTransitionCancel(transition: Transition): Unit = {
  }

  override def onTransitionPause(transition: Transition): Unit = {
  }

  override def onTransitionResume(transition: Transition): Unit = {
  }

  /**
    * Inflate a ListView or a GridView with a corresponding ListAdapter.
    *
    * @param inflater The LayoutInflater.
    * @param container The ViewGroup that contains this AbsListView. The AbsListView won't be
    *                  attached to it.
    * @param inflateListView Pass true to inflate a ListView, or false to inflate a GridView.
    */
  private def inflateAbsList(inflater: LayoutInflater, container: ViewGroup, inflateListView: Boolean): Unit = {
    if(inflateListView) {
      mAbsListView = inflater.inflate(R.layout.fragment_meat_list, container, false).asInstanceOf[AbsListView]
      mAdapter = new MeatAdapter(inflater, R.layout.item_meat_list)
    } else {
      mAbsListView = inflater.inflate(R.layout.fragment_meat_grid, container, false).asInstanceOf[AbsListView]
      mAdapter = new MeatAdapter(inflater, R.layout.item_meat_grid)
    }
    mAbsListView.setAdapter(mAdapter)
    mAbsListView.setOnItemClickListener(mAdapter)
  }

  /**
    * Toggle the UI between ListView and GridView.
    */
  private def toggle(): Unit = {
    // We use mCover as the overlay on which we carry out the transition.
    mCover.setVisibility(View.VISIBLE)
    // This FrameLayout holds all the visible views in the current list or grid. We use this as
    // the starting Scene of the Transition later.
    val before = copyVisibleViews()
    val params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    mCover.addView(before, params)
    // Swap the actual list.
    swapAbsListView()
    // We also swap the icon for the toggle button.
    ActivityCompat.invalidateOptionsMenu(getActivity())
    // It is now ready to start the transition.
    mAbsListView.post(new Runnable() {
      override def run(): Unit = {
        // BEGIN_INCLUDE(transition_with_listener)
        val scene = new Scene(mCover, copyVisibleViews())
        val transition = new AutoTransition()
        transition.addListener(AdapterTransitionFragment.this)
        TransitionManager.go(scene, transition)
        // END_INCLUDE(transition_with_listener)
      }
    })
  }

  /**
    * Swap ListView with GridView, or GridView with ListView.
    */
  private def swapAbsListView(): Unit = {
    // We save the current scrolling position before removing the current list.
    val first = mAbsListView.getFirstVisiblePosition()
    // If the current list is a GridView, we replace it with a ListView. If it is a ListView,
    // a GridView.
    val inflater = LayoutInflater.from(getActivity())
    inflateAbsList(inflater, mAbsListView.getParent().asInstanceOf[ViewGroup], mAbsListView.isInstanceOf[GridView])
    mAbsListView.setAdapter(mAdapter)
    // We restore the scrolling position here.
    mAbsListView.setSelection(first)
    // The new list is ready, and we replace the existing one with it.
    mContent.removeAllViews()
    mContent.addView(mAbsListView)
  }

  /**
    * Copy all the visible views in the mAbsListView into a new FrameLayout and return it.
    *
    * @return a FrameLayout with all the visible views inside.
    */
  private def copyVisibleViews(): FrameLayout = {
    // This is the FrameLayout we return afterwards.
    val layout = new FrameLayout(getActivity())
    // The transition framework requires to set ID for all views to be animated.
    layout.setId(AdapterTransitionFragment.ROOT_ID)
    // We only copy visible views.
    val first = mAbsListView.getFirstVisiblePosition()
    var index = 0
    while(true) {
      // This is one of the views that we copy. Note that the argument for getChildAt is a
      // zero-oriented index, and it doesn't usually match with its position in the list.
      val source = mAbsListView.getChildAt(index)
      if(source != null) {
        // This is the copy of the original view.
        val destination = mAdapter.getView(first + index, null, layout)
        assert(destination != null)
        destination.setId(AdapterTransitionFragment.ROOT_ID + first + index)
        val params = new FrameLayout.LayoutParams(source.getWidth(), source.getHeight())
        params.leftMargin = source.getX().toInt
        params.topMargin = source.getY().toInt
        layout.addView(destination, params)
        index += 1
      }
    }
    layout
  }
}
