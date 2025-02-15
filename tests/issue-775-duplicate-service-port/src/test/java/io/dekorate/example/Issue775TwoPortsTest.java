/**
 * Copyright 2018 The original authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package io.dekorate.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import io.dekorate.utils.Serialization;
import io.fabric8.kubernetes.api.model.*;

@TestPropertySource(locations = "./application2.properties")
public class Issue775TwoPortsTest {

  @Test
  public void shouldHaveUniquePortNumberForTwoPorts() {
    KubernetesList list = Serialization
        .unmarshalAsList(getClass().getClassLoader().getResourceAsStream("META-INF/dekorate/kubernetes.yml"));
    assertNotNull(list);
    Service s = findFirst(list, Service.class).orElseThrow(() -> new IllegalStateException());
    assertNotNull(s);

    List<ServicePort> p = s.getSpec().getPorts();
    assertEquals(2, p.size());

    ServicePort servicePort1 = p.get(0);
    assertEquals("http", servicePort1.getName());
    assertEquals(80, servicePort1.getPort());
    assertEquals(8080, servicePort1.getTargetPort().getIntVal());

    ServicePort servicePort2 = p.get(1);
    assertEquals("https", servicePort2.getName());
    assertEquals(443, servicePort2.getPort());
    assertEquals(8443, servicePort2.getTargetPort().getIntVal());
  }

  <T extends HasMetadata> Optional<T> findFirst(KubernetesList list, Class<T> t) {
    return (Optional<T>) list.getItems().stream()
        .filter(i -> t.isInstance(i))
        .findFirst();
  }

}
