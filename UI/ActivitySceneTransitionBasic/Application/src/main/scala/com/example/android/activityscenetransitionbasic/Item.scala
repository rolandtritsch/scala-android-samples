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

object Item {
  val LARGE_BASE_URL = "http://storage.googleapis.com/androiddevelopers/sample_data/activity_transition/large/"
  val THUMB_BASE_URL = "http://storage.googleapis.com/androiddevelopers/sample_data/activity_transition/thumbs/"

  val ITEMS = Array[Item](
    new Item("Flying in the Light", "Romain Guy", "flying_in_the_light.jpg"),
    new Item("Caterpillar", "Romain Guy", "caterpillar.jpg"),
    new Item("Look Me in the Eye", "Romain Guy", "look_me_in_the_eye.jpg"),
    new Item("Flamingo", "Romain Guy", "flamingo.jpg"),
    new Item("Rainbow", "Romain Guy", "rainbow.jpg"),
    new Item("Over there", "Romain Guy", "over_there.jpg"),
    new Item("Jelly Fish 2", "Romain Guy", "jelly_fish_2.jpg"),
    new Item("Lone Pine Sunset", "Romain Guy", "lone_pine_sunset.jpg")
  )

  def getItem(id: Long): Item = {
    ITEMS.find(e => e.getId() == id).get
  }
}

/**
  * Represents an Item in our application. Each item has a name, id, full size image url and
  * thumbnail url.
  */
class Item(val mName: String, val mAuthor: String, val mFileName: String) {
  def getId(): Long = {
    mName.hashCode + mFileName.hashCode
  }

  def getPhotoUrl(): String = {
    Item.LARGE_BASE_URL + mFileName
  }

  def getThumbnailUrl(): String = {
    Item.THUMB_BASE_URL + mFileName
  }

  override def toString(): String = {
    s"${mName}:${mAuthor}:${mFileName}"
  }
}
