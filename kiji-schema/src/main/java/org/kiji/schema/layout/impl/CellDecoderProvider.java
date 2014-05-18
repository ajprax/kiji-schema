package org.kiji.schema.layout.impl;

import java.io.IOException;

import org.kiji.schema.KijiCellDecoder;
import org.kiji.schema.KijiColumnName;
import org.kiji.schema.impl.BoundColumnReaderSpec;

public interface CellDecoderProvider {
  public <T> KijiCellDecoder<T> getDecoder(KijiColumnName column);

  public <T> KijiCellDecoder<T> getDecoder(BoundColumnReaderSpec spec) throws IOException;
}
