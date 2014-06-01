package org.kiji.schema;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import org.kiji.schema.layout.ColumnReaderSpec;

public class KijiCell2<T> implements Comparable<KijiCell2<T>> {

//  private final EntityId mEntityId; TODO should this have an eid, should it be part of the comparison?
  private final KijiColumnName mColumn;
  private final Long mTimestamp;
  private final T mValue;
  private final ColumnReaderSpec mReaderSpec;
  // TODO is some form of writer schema necessary?

  public KijiCell2(
//      final EntityId entityId,
      final KijiColumnName column,
      final long timestamp,
      final T value,
      final ColumnReaderSpec readerSpec
  ) {
//    mEntityId = Preconditions.checkNotNull(entityId);
    mColumn = Preconditions.checkNotNull(column);
    mTimestamp = Preconditions.checkNotNull(timestamp);
    mValue = value;
    mReaderSpec = Preconditions.checkNotNull(readerSpec);
    Preconditions.checkArgument(column.isFullyQualified(),
        "Cannot create a %s without a fully qualified column. Found column: %s",
        getClass().getName(),
        column.getName());
  }

  /**
   * Compares only key elements (entity id, column, and timestamp).
   *
   * {@inheritDoc}
   */
  @Override
  public int compareTo(
      final KijiCell2<T> that
  ) {
    final int column = this.mColumn.compareTo(that.mColumn);
    if (column != 0) {
      return column;
    } else {
      return this.mTimestamp.compareTo(that.mTimestamp);
    }
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(getClass())
//        .add("entity-id", mEntityId)
        .add("column", mColumn.getName())
        .add("timestamp", mTimestamp)
        .add("value", mValue.toString())
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
//        mEntityId,
        mColumn,
        mTimestamp,
        mValue);
  }

  @Override
  public boolean equals(
      final Object obj
  ) {
    if ((null == obj) || !Objects.equal(this.getClass(), obj.getClass())) {
      return false;
    } else {
      final KijiCell2 that = (KijiCell2) obj;
//      return Objects.equal(this.mEntityId, that.mEntityId)
      return Objects.equal(this.mColumn, that.mColumn)
          && Objects.equal(this.mTimestamp, that.mTimestamp)
          && Objects.equal(this.mValue, that.mValue);
    }
  }
}
