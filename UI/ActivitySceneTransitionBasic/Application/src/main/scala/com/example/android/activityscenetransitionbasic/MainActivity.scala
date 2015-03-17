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
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView

import android.util.Log

object MainActivity {
  val TAG = MainActivity.getClass().getName()
}

/**
  * Our main Activity in this sample. Displays a grid of items which an image and title.
  * When the user clicks on an item, {@link DetailActivity} is launched, using the Activity
  * Scene Transitions framework to animatedly do so.
  */
class MainActivity extends Activity with AdapterView.OnItemClickListener {
  var mGridView: GridView= null
  var mAdapter: MainActivity#GridAdapter = null

  override def onCreate(savedInstanceState: Bundle): Unit = {
    Log.d(MainActivity.TAG, "Enter onCreate ...")
    // android.os.Debug.waitForDebugger()
    super.onCreate(savedInstanceState)
    setContentView(R.layout.grid)

    // Setup the GridView and set the adapter
    mGridView = findViewById(R.id.grid).asInstanceOf[GridView]
    mGridView.setOnItemClickListener(this)
    mAdapter = new GridAdapter()
    mGridView.setAdapter(mAdapter)
    Log.d(MainActivity.TAG, "... leave onCreate!")
  }

  /**
    * Called when an item in the {@link android.widget.GridView} is clicked.
    * Here will launch the {@link DetailActivity}, using the Scene Transition
    * animation functionality.
    */
  override def onItemClick(adapterView: AdapterView[_], view: View, position: Int, id: Long): Unit = {
    Log.d(MainActivity.TAG, "Enter onItemClick ...")
    val item = adapterView.getItemAtPosition(position).asInstanceOf[Item]

    // Construct an Intent as normal (Note: Scala decorates the classname with "$",
    // means you can't use DetailActivity.getClass())
    // val intent = new Intent(this, DetailActivity.getClass())
    val intent = new Intent(); intent.setClassName(DetailActivity.getClass().getPackage().getName(), ".DetailActivity")
    intent.putExtra(DetailActivity.EXTRA_PARAM_ID, item.getId())
    Log.i(MainActivity.TAG, s"Intent: ${intent.getComponent().getClassName()}/${intent.getComponent().getPackageName()}/${intent.getComponent().getShortClassName()}")

    // BEGIN_INCLUDE(start_activity)
    /**
      * Now create an {@link android.app.ActivityOptions} instance using the
      * {@link ActivityOptionsCompat#makeSceneTransitionAnimation(Activity, Pair[])}
      * factory method.
      */
    val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
      this,
      // Now we provide a list of Pair items which contain the view we can transitioning
      // from, and the name of the view it is transitioning to, in the launched activity
      new Pair[View, String](view.findViewById(R.id.imageview_item), DetailActivity.VIEW_NAME_HEADER_IMAGE),
      new Pair[View, String](view.findViewById(R.id.textview_name), DetailActivity.VIEW_NAME_HEADER_TITLE))

    // Now we can start the Activity, providing the activity options as a bundle
    ActivityCompat.startActivity(this, intent, activityOptions.toBundle())
    // END_INCLUDE(start_activity)
    Log.d(MainActivity.TAG, "... leave onItemClick!")
  }

  /**
    * {@link android.widget.BaseAdapter} which displays items.
    */
  class GridAdapter extends BaseAdapter {
    override def getCount(): Int = {
      Item.ITEMS.size
    }

    override def getItem(position: Int): Item = {
      Item.ITEMS(position)
    }

    override def getItemId(position: Int): Long = {
      getItem(position).getId()
    }

    override def getView(position: Int, view: View, viewGroup: ViewGroup): View = {
      var v = if(view != null) {
        view
      } else {
        getLayoutInflater().inflate(R.layout.grid_item, viewGroup, false)
      }

      val item = getItem(position)

      // Load the thumbnail image
      val image = v.findViewById(R.id.imageview_item).asInstanceOf[ImageView]
      Picasso.`with`(image.getContext()).load(item.getThumbnailUrl()).into(image)

      // Set the TextView's contents
      val name = v.findViewById(R.id.textview_name).asInstanceOf[TextView]
      name.setText(item.mName)

      v
    }
  }
}
