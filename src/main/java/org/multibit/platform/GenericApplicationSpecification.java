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
package org.multibit.platform;

import java.util.HashSet;
import java.util.Set;
import org.multibit.platform.listener.GenericAboutEventListener;
import org.multibit.platform.listener.GenericOpenURIEventListener;
import org.multibit.platform.listener.GenericPreferencesEventListener;
import org.multibit.platform.listener.GenericQuitEventListener;

/**
 * Specification value object to provide the following to {@link GenericApplicationFactory}:
 *
 * <ul>
 *   <li>Provision of required references to external objects
 * </ul>
 *
 * @since 0.3.0
 */
public class GenericApplicationSpecification {

  private Set<GenericOpenURIEventListener> openURIEventListeners =
      new HashSet<GenericOpenURIEventListener>();
  private Set<GenericPreferencesEventListener> preferencesEventListeners =
      new HashSet<GenericPreferencesEventListener>();
  private Set<GenericAboutEventListener> aboutEventListeners =
      new HashSet<GenericAboutEventListener>();
  private Set<GenericQuitEventListener> quitEventListeners =
      new HashSet<GenericQuitEventListener>();

  public Set<GenericOpenURIEventListener> getOpenURIEventListeners() {
    return openURIEventListeners;
  }

  public Set<GenericPreferencesEventListener> getPreferencesEventListeners() {
    return preferencesEventListeners;
  }

  public Set<GenericAboutEventListener> getAboutEventListeners() {
    return aboutEventListeners;
  }

  public Set<GenericQuitEventListener> getQuitEventListeners() {
    return quitEventListeners;
  }
}
