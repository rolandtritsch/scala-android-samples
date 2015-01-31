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

import android.content.{Context, Intent}
import android.net.Uri
import android.text.TextUtils

object ContentItem {
  // Used to signify an image content type
  val CONTENT_TYPE_IMAGE = 0

  // Used to signify a text/string content type
  val CONTENT_TYPE_TEXT = 1
}

/**
  * This class encapsulates a content item. Referencing the content's type, and the differing way
  * to reference the content (asset URI or resource id).
  */
class ContentItem(val contentType: Int = 0, val contentResourceId: Int = 0, contentAssetFilePath: String = "") {
  def this(contentType: Int, contentAssetFilePath: String) = {
    this(contentType, 0, contentAssetFilePath)
  }

  def getContentUri(): Uri = {
    assert(!TextUtils.isEmpty(contentAssetFilePath), "getContentUri: contentAssetFilePath cannot be empty")
    Uri.parse(s"content://${AssetProvider.CONTENT_URI}/${contentAssetFilePath}")
  }

  /**
    * Returns an {@link android.content.Intent} which can be used to share this item's content with other
    * applications.
    *
    * @param context - Context to be used for fetching resources if needed
    * @return Intent to be given to a ShareActionProvider.
    */
  def getShareIntent(context: Context): Intent = {
    val intent = new Intent(Intent.ACTION_SEND)

    contentType match {
      case ContentItem.CONTENT_TYPE_IMAGE => {
        intent.setType("image/jpg")
        // Bundle the asset content uri as the EXTRA_STREAM uri
        intent.putExtra(Intent.EXTRA_STREAM, getContentUri())
      }

      case ContentItem.CONTENT_TYPE_TEXT => {
        intent.setType("text/plain")
        // Get the string resource and bundle it as an intent extra
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(contentResourceId))
      }
    }

    intent
  }
}
