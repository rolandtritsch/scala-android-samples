/*
 * Copyright 2014 The Android Open Source Project
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

package com.example.android.adaptertransition

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

/**
  * This class provides data as Views. It is designed to support both ListView and GridView by
  * changing a layout resource file to inflate.
  */
class MeatAdapter(mLayoutInflater: LayoutInflater, mResourceId: Int) extends BaseAdapter with AdapterView.OnItemClickListener {
  override def getCount(): Int = {
    Meat.MEATS.length
  }

  override def getItem(position: Int): Meat = {
    Meat.MEATS(position)
  }

  override def getItemId(position: Int): Long = {
    Meat.MEATS(position).resourceId
  }

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    var view: View = null
    var holder: ViewHolder = null
    if(convertView == null) {
      view = mLayoutInflater.inflate(mResourceId, parent, false)
      holder = ViewHolder()
      assert(view != null)
      holder.image = view.findViewById(R.id.meat_image).asInstanceOf[ImageView]
      holder.title = view.findViewById(R.id.meat_title).asInstanceOf[TextView]
      view.setTag(holder)
    } else {
      view = convertView
      holder = view.getTag().asInstanceOf[ViewHolder]
    }
    val meat = getItem(position)
    holder.image.setImageResource(meat.resourceId)
    holder.title.setText(meat.title)
    view
  }

  override def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long): Unit = {
    val holder = view.getTag().asInstanceOf[ViewHolder]
    val context = view.getContext()
    if(holder != null && holder.title != null && context != null) {
      Toast
        .makeText(
          context,
          context.getString(
            R.string.item_clicked,
            holder.title.getText()
          ), Toast.LENGTH_SHORT)
        .show()
    }
  }

  private case class ViewHolder(var image: ImageView = null, var title: TextView = null)
}
