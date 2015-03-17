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

package com.example.android.actionbarcompat.shareactionprovider.content

import android.content.ContentProvider
import android.content.ContentValues
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.database.Cursor
import android.net.Uri
import android.text.TextUtils

import java.io.FileNotFoundException
import java.io.IOException

object AssetProvider {
  val CONTENT_URI = "com.example.android.actionbarcompat.shareactionprovider"
}

/**
  * A simple ContentProvider which can serve files from this application's assets. The majority of
  * functionality is in {@link #openAssetFile(android.net.Uri, String)}.
  */
class AssetProvider extends ContentProvider {
  override def onCreate(): Boolean = {
    true
  }

  override def delete(uri: Uri, selection: String, selectionArgs: Array[String]): Int = {
    // Do not support delete requests.
    0
  }

  override def getType(uri: Uri): String = {
    // Do not support returning the data type
    null
  }

  override def insert(uri: Uri, values: ContentValues): Uri = {
    // Do not support insert requests.
    null
  }

  override def query(uri: Uri, projection: Array[String], selection: String, selectionArgs: Array[String], sortOrder: String): Cursor = {
    // Do not support query requests.
    null
  }

  override def update(uri: Uri, values: ContentValues, selection: String, selectionArgs: Array[String]): Int = {
    // Do not support update requests.
    return 0
  }

  @throws(classOf[FileNotFoundException])
  override def openAssetFile(uri: Uri, mode: String): AssetFileDescriptor = {
    // The asset file name should be the last path segment
    val assetName = uri.getLastPathSegment()

    // If the given asset name is empty, throw an exception
    if(TextUtils.isEmpty(assetName)) {
      throw new FileNotFoundException()
    }

    try {
      // Try and return a file descriptor for the given asset name
      val am = getContext().getAssets()
      am.openFd(assetName)
    } catch {
      case e: IOException => {
        e.printStackTrace()
        super.openAssetFile(uri, mode)
      }
    }
  }
}
