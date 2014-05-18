package org.kiji.schema.layout.impl;

import org.kiji.schema.KijiCellEncoder;
import org.kiji.schema.KijiColumnName;

/**
 * Overlays encoder overrides onto a base cell encoder provider.
 *
 * <p>
 *   Currently there are no valid encoder overrides, so this is just a pass-through layer to a base
 *   provider.
 * </p>
 */
public final class OverlayedCellEncoderProvider implements CellEncoderProvider {
  /**
   * Create a new OverlayedCellEncoderProvider from the given base cell encoder provider.
   *
   * @param baseCellEncoderProvider base provider on which to build an overlayed provider.
   * @return a new OverlayedCellEncoderProvider.
   */
  public static OverlayedCellEncoderProvider create(
      final BaseCellEncoderProvider baseCellEncoderProvider
  ) {
    return new OverlayedCellEncoderProvider(baseCellEncoderProvider);
  }

  private final BaseCellEncoderProvider mBaseCellEncoderProvider;

  /**
   * Initialize a new OverlayedCellEncoderProvider.
   *
   * @param baseCellEncoderProvider base provider on which to build an overlayed provider.
   */
  private OverlayedCellEncoderProvider(
      final BaseCellEncoderProvider baseCellEncoderProvider
  ) {
    mBaseCellEncoderProvider = baseCellEncoderProvider;
  }

  /** {@inheritDoc} */
  @Override
  public KijiCellEncoder getEncoder(
      final KijiColumnName column
  ) {
    return mBaseCellEncoderProvider.getEncoder(column);
  }
}
