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

package com.example.android.apprestrictionenforcer

import android.text.TextWatcher

/**
  * This is a wrapper around {@link TextWatcher} that overrides
  * {@link TextWatcher#beforeTextChanged(CharSequence, int, int, int)} and
  * {@link TextWatcher#onTextChanged(CharSequence, int, int, int)} with empty bodies.
  */
abstract class EasyTextWatcher extends TextWatcher {
  override def beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int): Unit = {
    // Do nothing
  }

  override def onTextChanged(s: CharSequence, start: Int, before: Int, count: Int): Unit = {
    // Do nothing
  }
}
