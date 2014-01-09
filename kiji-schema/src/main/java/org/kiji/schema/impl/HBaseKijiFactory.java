/**
 * (c) Copyright 2013 WibiData, Inc.
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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

package org.kiji.schema.impl;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

import org.kiji.annotations.ApiAudience;
import org.kiji.delegation.Priority;
import org.kiji.schema.Kiji;
import org.kiji.schema.KijiFactory;
import org.kiji.schema.KijiIOException;
import org.kiji.schema.KijiURI;
import org.kiji.schema.RuntimeInterruptedException;
import org.kiji.schema.hbase.HBaseFactory;

/** Factory for constructing instances of HBaseKiji. */
@ApiAudience.Private
public final class HBaseKijiFactory implements KijiFactory {

  /** Map of interned HBaseKiji instances. */
  private static final Map<String, HBaseKiji> INTERN_MAP = Maps.newHashMap();

  static {
    final Thread mainThread = Thread.currentThread();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        for (HBaseKiji kiji : INTERN_MAP.values()) {
          try {
            kiji.release();
          } catch (IOException ioe) {
            throw new KijiIOException(ioe);
          }
        }
        try {
          mainThread.join();
        } catch (InterruptedException ie) {
          throw new RuntimeInterruptedException(ie);
        }
      }
    });
  }

  /**
   * Get the key to the intern map for the given KijiURI and Configuration.
   *
   * @param kijiURI KijiURI of the instance for which to get the intern map key.
   * @param conf Configuration of the instance for which to get the intern map key.
   * @return key to the intern map for the given Kiji instance.
   */
  private static String getInternMapKey(
      final KijiURI kijiURI,
      final Configuration conf
  ) {
    final KijiURI instanceURI = KijiURI.newBuilder()
        .withZookeeperQuorum(kijiURI.getZookeeperQuorum())
        .withZookeeperClientPort(kijiURI.getZookeeperClientPort())
        .withInstanceName(kijiURI.getInstance())
        .build();
    return String.format("%s:%s", instanceURI, (null == conf) ? "" : conf.toString());
  }

  /**
   * Create a new HBaseKiji instance for storage in the intern map.
   *
   * @param kijiURI KijiURI of the instance to create.
   * @param conf Configuration with which to open the instance or null to use
   *     HBaseConfiguration.create()
   * @return a new HBaseKiji instance.
   * @throws IOException in case of an error getting a LockFactory for the new Kiji instance.
   */
  private static HBaseKiji createKiji(
      final KijiURI kijiURI,
      final Configuration conf
  ) throws IOException {
    final HBaseFactory hbaseFactory = HBaseFactory.Provider.get();
    final Configuration confCopy =
        (null == conf) ? HBaseConfiguration.create() : new Configuration(conf);
    return new HBaseKiji(
        kijiURI,
        confCopy,
        hbaseFactory.getHTableInterfaceFactory(kijiURI),
        hbaseFactory.getLockFactory(kijiURI, confCopy));
  }

  /**
   * Get the interned Kiji instance from the intern map if it is available, or create a new Kiji
   * instance and add it to the map before returning it.
   *
   * @param kijiURI KijiURI of the instance to get or create.
   * @param conf Configuration with which to open the instance or null to use
   *     HBaseConfiguration.create()
   * @return the interned HBaseKiji instance.
   * @throws IOException in case of an error getting a LockFactory for a new Kiji instance.
   */
  private static HBaseKiji getOrCreateKiji(
      final KijiURI kijiURI,
      final Configuration conf
  ) throws IOException {
    final String internKey = getInternMapKey(kijiURI, conf);
    if (!INTERN_MAP.containsKey(internKey)) {
      synchronized (INTERN_MAP) {
        HBaseKiji kiji = INTERN_MAP.get(internKey);
        if (null == kiji) {
          kiji = createKiji(kijiURI, conf);
          kiji.retain();
          INTERN_MAP.put(internKey, kiji);
        }
        return kiji;
      }
    } else {
      return INTERN_MAP.get(internKey);
    }
  }

  /** {@inheritDoc} */
  @Override
  public Kiji open(KijiURI uri) throws IOException {
    return getOrCreateKiji(uri, null);
  }

  /** {@inheritDoc} */
  @Override
  public Kiji open(KijiURI uri, Configuration conf) throws IOException {
    return getOrCreateKiji(uri, conf);
  }

  /** {@inheritDoc} */
  @Override
  public int getPriority(Map<String, String> runtimeHints) {
    // Default priority; should be used unless overridden by tests.
    return Priority.NORMAL;
  }
}
