/**
 * Copyright 2011 multibit.org
 *
 * <p>Licensed under the MIT license (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * <p>http://opensource.org/licenses/mit-license.php
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.multibit.platform.handler;

import org.multibit.platform.listener.GenericQuitEvent;
import org.multibit.platform.listener.GenericQuitResponse;

/**
 * Generic handler to provide the following to {@link org.multibit.platform.GenericApplication}:
 *
 * <ul>
 *   <li>Provision of application specific handling code
 * </ul>
 *
 * @since 0.3.0
 */
public interface GenericQuitHandler extends GenericHandler {
  /**
   * Called in response to receiving a Quit event
   *
   * @param event The generic Quit event
   * @param response The response containing the methods to call if quit can continue or not
   */
  void handleQuitRequestWith(GenericQuitEvent event, GenericQuitResponse response);
}
