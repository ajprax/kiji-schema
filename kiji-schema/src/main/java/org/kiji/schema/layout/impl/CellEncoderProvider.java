package org.kiji.schema.layout.impl;

import org.kiji.schema.KijiCellEncoder;
import org.kiji.schema.KijiColumnName;

public interface CellEncoderProvider {

  /**
   * Gets a cell encoder for the specified column or (map-type) family.
   *
   * <p>
   *   When requesting a encoder for a column within a map-type family, the encoder for the
   *   entire map-type family will be returned.
   * </p>
   *
   * @param column The column for which to get a cell encoder.
   * @return a cell encoder for the specified column.
   *     Null if the column does not exist or if a family level encoder is requested from a group
   *     type family.
   */
  KijiCellEncoder getEncoder(KijiColumnName column);
}
