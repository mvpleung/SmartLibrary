/*
 *  Copyright 2010 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.smart.library.widget.wheel;

/**
 * Wheel changed listener interface.
 * The currentItemChanged() method is called whenever mCurrent wheel positions is changed:
 * <ul>
 * <li> New Wheel position is set</li>
 * <li> Wheel view is scrolled</li>
 * </ul>
 */
public interface OnWheelChangedListener {
    /**
     * Callback method to be invoked when mCurrent item changed
     *
     * @param wheel    the wheel view whose state has changed
     * @param oldValue the old value of mCurrent item
     * @param newValue the new value of mCurrent item
     */
    void onChanged(WheelView wheel, int oldValue, int newValue);
}
