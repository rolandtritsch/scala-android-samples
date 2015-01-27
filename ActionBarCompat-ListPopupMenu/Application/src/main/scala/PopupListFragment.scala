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
package com.example.android.actionbarcompat.listpopupmenu

import android.os.Bundle
import android.support.v4.app.ListFragment
import android.support.v7.widget.PopupMenu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast

import java.util.ArrayList

/**
  * This ListFragment displays a list of cheeses, with a clickable view on each item whichs
  * displays a [[android.support.v7.widget.PopupMenu]] when clicked, allowing the user to
  * remove the item from the list.
  */
class PopupListFragment extends ListFragment with View.OnClickListener {
  override def onActivityCreated(savedInstanceState: Bundle): Unit = {
    super.onActivityCreated(savedInstanceState)

    // We want to allow modifications to the list so copy the dummy data array into a mutable array
    var items = new ArrayList[String](); Cheeses.CHEESES.foreach(items.add(_))

    // Set the ListAdapter
    setListAdapter(new PopupAdapter(items))
  }

  override def onListItemClick(listView: ListView, v: View, position: Int, id: Long): Unit = {
    val item = listView.getItemAtPosition(position).toString

    // Show a toast if the user clicks on an item
    Toast.makeText(getActivity(), s"Item Clicked: ${item}", Toast.LENGTH_SHORT).show()
  }

  override def onClick(view: View): Unit = {
    // We need to post a Runnable to show the popup to make sure that the PopupMenu is
    // correctly positioned. The reason being that the view may change position before the
    // PopupMenu is shown.
    view.post(new Runnable() {
      override def run() {
        showPopupMenu(view)
      }
    })
  }

  // BEGIN_INCLUDE(show_popup)
  private def showPopupMenu(view: View): Unit = {
    val adapter = getListAdapter().asInstanceOf[PopupAdapter]

    // Retrieve the clicked item from view's tag
    val item = view.getTag().asInstanceOf[String]

    // Create a PopupMenu, giving it the clicked view for an anchor
    val popup = new PopupMenu(getActivity(), view)

    // Inflate our menu resource into the PopupMenu's Menu
    popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu())

    // Set a listener so we are notified if a menu item is clicked
    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
      override def onMenuItemClick(menuItem: MenuItem): Boolean = {
        menuItem.getItemId() match {
          case R.id.menu_remove => {
            adapter.remove(item)
            true
          }
          case _ => {
            false
          }
        }
      }
    })

    // Finally show the PopupMenu
    popup.show()
  }
  // END_INCLUDE(show_popup)

  /**
    * A simple array adapter that creates a list of cheeses.
    */
  class PopupAdapter(items: ArrayList[String])
  extends ArrayAdapter[String](
    getActivity(),
    R.layout.list_item,
    android.R.id.text1,
    items
  ) {
    override def getView(position: Int, convertView: View, container: ViewGroup): View = {
      // Let ArrayAdapter inflate the layout and set the text
      val view = super.getView(position, convertView, container)

      // BEGIN_INCLUDE(button_popup)
      // Retrieve the popup button from the inflated view
      val popupButton = view.findViewById(R.id.button_popup)

      // Set the item as the button's tag so it can be retrieved later
      popupButton.setTag(getItem(position))

      // Set the fragment instance as the OnClickListener
      popupButton.setOnClickListener(PopupListFragment.this)
      // END_INCLUDE(button_popup)

      // Finally return the view to be displayed
      view
    }
  }
}
