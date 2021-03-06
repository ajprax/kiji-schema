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

package org.kiji.schema.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class TestProtocolVersion {
  @Test
  public void testMainVersion() {
    ProtocolVersion pv = ProtocolVersion.parse("proto-1.2.3");
    assertEquals("proto", pv.getProtocolName());
    assertEquals(1, pv.getMajorVersion());
    assertEquals(2, pv.getMinorVersion());
    assertEquals(3, pv.getRevision());
  }

  @Test
  public void testNoRev() {
    ProtocolVersion pv = ProtocolVersion.parse("proto-1.2");
    assertEquals("proto", pv.getProtocolName());
    assertEquals(1, pv.getMajorVersion());
    assertEquals(2, pv.getMinorVersion());
    assertEquals(0, pv.getRevision());
  }

  @Test
  public void testNoMinor() {
    ProtocolVersion pv = ProtocolVersion.parse("proto-1");
    assertEquals("proto", pv.getProtocolName());
    assertEquals(1, pv.getMajorVersion());
    assertEquals(0, pv.getMinorVersion());
    assertEquals(0, pv.getRevision());
  }

  @Test
  public void testNoProto() {
    ProtocolVersion pv = ProtocolVersion.parse("1.2.3");
    assertNull(pv.getProtocolName());
    assertEquals(1, pv.getMajorVersion());
    assertEquals(2, pv.getMinorVersion());
    assertEquals(3, pv.getRevision());
  }

  @Test
  public void testInt() {
    ProtocolVersion pv = ProtocolVersion.parse("1");
    assertNull(pv.getProtocolName());
    assertEquals(1, pv.getMajorVersion());
    assertEquals(0, pv.getMinorVersion());
    assertEquals(0, pv.getRevision());
  }

  @Test
  public void testTwoInts() {
    ProtocolVersion pv = ProtocolVersion.parse("1.2");
    assertNull(pv.getProtocolName());
    assertEquals(1, pv.getMajorVersion());
    assertEquals(2, pv.getMinorVersion());
    assertEquals(0, pv.getRevision());
  }

  @Test
  public void testToString() {
    ProtocolVersion pv1 = ProtocolVersion.parse("proto-1.2");
    ProtocolVersion pv2 = ProtocolVersion.parse("proto-1.2.0");

    assertEquals("proto-1.2", pv1.toString());
    assertEquals("proto-1.2.0", pv2.toString());
    assertTrue(pv1.equals(pv2));
    assertEquals("proto-1.2.0", pv1.toCanonicalString());
    assertEquals("proto-1.2.0", pv2.toCanonicalString());
  }

  @Test
  public void testEquals() {
    ProtocolVersion pv1 = ProtocolVersion.parse("proto-1.2.3");
    ProtocolVersion pv2 = ProtocolVersion.parse("proto-1.2.3");
    assertTrue(pv1.equals(pv2));
    assertTrue(pv2.equals(pv1));
    assertEquals(pv1.hashCode(), pv2.hashCode());
    assertEquals(0, pv1.compareTo(pv2));
    assertEquals(0, pv2.compareTo(pv1));

    // Required for our equals() method to imply the same hash code.
    assertEquals(0, Integer.valueOf(0).hashCode());
  }

  @Test
  public void testCompare() {
    List<ProtocolVersion> list = Arrays.asList(
        ProtocolVersion.parse("proto-1.10.0"),
        ProtocolVersion.parse("proto-1.3.1"),
        ProtocolVersion.parse("proto-2.1"),
        ProtocolVersion.parse("proto-0.4"),
        ProtocolVersion.parse("proto-2.0.0"),
        ProtocolVersion.parse("proto-1"),
        ProtocolVersion.parse("proto-1.1"),
        ProtocolVersion.parse("other-0.9"));

    List<ProtocolVersion> expected = Arrays.asList(
        ProtocolVersion.parse("other-0.9"),
        ProtocolVersion.parse("proto-0.4"),
        ProtocolVersion.parse("proto-1"),
        ProtocolVersion.parse("proto-1.1"),
        ProtocolVersion.parse("proto-1.3.1"),
        ProtocolVersion.parse("proto-1.10.0"),
        ProtocolVersion.parse("proto-2.0.0"),
        ProtocolVersion.parse("proto-2.1"));

    Collections.sort(list);
    assertEquals(expected, list);
  }

  @Test
  public void testCheckEquality() {
    assertTrue(ProtocolVersion.checkEquality(null, null));
    assertTrue(ProtocolVersion.checkEquality("hi", "hi"));
    assertFalse(ProtocolVersion.checkEquality("hi", null));
    assertFalse(ProtocolVersion.checkEquality(null, "hi"));
    assertTrue(ProtocolVersion.checkEquality(Integer.valueOf(32), Integer.valueOf(32)));
    assertFalse(ProtocolVersion.checkEquality(Integer.valueOf(32), Integer.valueOf(7)));
    assertFalse(ProtocolVersion.checkEquality(Integer.valueOf(32), "ohai"));
    assertFalse(ProtocolVersion.checkEquality("meep", "ohai"));
  }

  @Test
  public void testEqualsNoRev() {
    ProtocolVersion pv1 = ProtocolVersion.parse("proto-1.2.0");
    ProtocolVersion pv2 = ProtocolVersion.parse("proto-1.2");
    assertTrue(pv1.equals(pv2));
    assertTrue(pv2.equals(pv1));
    assertEquals(pv1.hashCode(), pv2.hashCode());
    assertEquals(0, pv1.compareTo(pv2));
    assertEquals(0, pv2.compareTo(pv1));
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoJustProto() {
    ProtocolVersion.parse("proto");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoJustProto2() {
    ProtocolVersion.parse("proto-");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testDashRequired() {
    ProtocolVersion.parse("foo1.4");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoLeadingDash() {
    ProtocolVersion.parse("-foo-1.4");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoMoreThan3() {
    ProtocolVersion.parse("1.2.3.4");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoMoreThan3withProto() {
    ProtocolVersion.parse("proto-1.2.3.4");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoLeadingDashWithoutProto() {
    ProtocolVersion.parse("-1.2.3");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoLeadingDashes() {
    ProtocolVersion.parse("--1.2.3");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoMultiDash() {
    ProtocolVersion.parse("proto--1.2.3");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoDashInProto() {
    ProtocolVersion.parse("proto-col-1.2.3");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoNegativeMinor() {
    ProtocolVersion.parse("1.-2");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoNegativeRev() {
    ProtocolVersion.parse("1.2.-3");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoMultiDots() {
    ProtocolVersion.parse("1..2");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoMultiDots2() {
    ProtocolVersion.parse("1..2.3");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoNamedMinor() {
    ProtocolVersion.parse("1.x");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoNamedMinorWithProto() {
    ProtocolVersion.parse("proto-1.x");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoLeadingNumber() {
    ProtocolVersion.parse("2foo-1.3");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoEmptyString() {
    ProtocolVersion.parse("");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testNoNull() {
    ProtocolVersion.parse(null);
  }
}
