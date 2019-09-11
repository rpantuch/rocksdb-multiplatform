package maryk.rocksdb

actual enum class TickerType(
    private val value: Byte
) {
    BLOCK_CACHE_MISS(0x0),
    BLOCK_CACHE_HIT(0x1),
    BLOCK_CACHE_ADD(0x2),
    BLOCK_CACHE_ADD_FAILURES(0x3),
    BLOCK_CACHE_INDEX_MISS(0x4),
    BLOCK_CACHE_INDEX_HIT(0x5),
    BLOCK_CACHE_INDEX_ADD(0x6),
    BLOCK_CACHE_INDEX_BYTES_INSERT(0x7),
    BLOCK_CACHE_INDEX_BYTES_EVICT(0x8),
    BLOCK_CACHE_FILTER_MISS(0x9),
    BLOCK_CACHE_FILTER_HIT(0xA),
    BLOCK_CACHE_FILTER_ADD(0xB),
    BLOCK_CACHE_FILTER_BYTES_INSERT(0xC),
    BLOCK_CACHE_FILTER_BYTES_EVICT(0xD),
    BLOCK_CACHE_DATA_MISS(0xE),
    BLOCK_CACHE_DATA_HIT(0xF),
    BLOCK_CACHE_DATA_ADD(0x10),
    BLOCK_CACHE_DATA_BYTES_INSERT(0x11),
    BLOCK_CACHE_BYTES_READ(0x12),
    BLOCK_CACHE_BYTES_WRITE(0x13),
    BLOOM_FILTER_USEFUL(0x14),
    PERSISTENT_CACHE_HIT(0x15),
    PERSISTENT_CACHE_MISS(0x16),
    SIM_BLOCK_CACHE_HIT(0x17),
    SIM_BLOCK_CACHE_MISS(0x18),
    MEMTABLE_HIT(0x19),
    MEMTABLE_MISS(0x1A),
    GET_HIT_L0(0x1B),
    GET_HIT_L1(0x1C),
    GET_HIT_L2_AND_UP(0x1D),
    COMPACTION_KEY_DROP_NEWER_ENTRY(0x1E),
    COMPACTION_KEY_DROP_OBSOLETE(0x1F),
    COMPACTION_KEY_DROP_RANGE_DEL(0x20),
    COMPACTION_KEY_DROP_USER(0x21),
    COMPACTION_RANGE_DEL_DROP_OBSOLETE(0x22),
    NUMBER_KEYS_WRITTEN(0x23),
    NUMBER_KEYS_READ(0x24),
    NUMBER_KEYS_UPDATED(0x25),
    BYTES_WRITTEN(0x26),
    BYTES_READ(0x27),
    NUMBER_DB_SEEK(0x28),
    NUMBER_DB_NEXT(0x29),
    NUMBER_DB_PREV(0x2A),
    NUMBER_DB_SEEK_FOUND(0x2B),
    NUMBER_DB_NEXT_FOUND(0x2C),
    NUMBER_DB_PREV_FOUND(0x2D),
    ITER_BYTES_READ(0x2E),
    NO_FILE_CLOSES(0x2F),
    NO_FILE_OPENS(0x30),
    NO_FILE_ERRORS(0x31),
    STALL_MICROS(0x35),
    DB_MUTEX_WAIT_MICROS(0x36),
    RATE_LIMIT_DELAY_MILLIS(0x37),
    NO_ITERATORS(0x38),
    NUMBER_MULTIGET_CALLS(0x39),
    NUMBER_MULTIGET_KEYS_READ(0x3A),
    NUMBER_MULTIGET_BYTES_READ(0x3B),
    NUMBER_FILTERED_DELETES(0x3C),
    NUMBER_MERGE_FAILURES(0x3D),
    BLOOM_FILTER_PREFIX_CHECKED(0x3E),
    BLOOM_FILTER_PREFIX_USEFUL(0x3F),
    NUMBER_OF_RESEEKS_IN_ITERATION(0x40),
    GET_UPDATES_SINCE_CALLS(0x41),
    BLOCK_CACHE_COMPRESSED_MISS(0x42),
    BLOCK_CACHE_COMPRESSED_HIT(0x43),
    BLOCK_CACHE_COMPRESSED_ADD(0x44),
    BLOCK_CACHE_COMPRESSED_ADD_FAILURES(0x45),
    WAL_FILE_SYNCED(0x46),
    WAL_FILE_BYTES(0x47),
    WRITE_DONE_BY_SELF(0x48),
    WRITE_DONE_BY_OTHER(0x49),
    WRITE_TIMEDOUT(0x4A),
    WRITE_WITH_WAL(0x4B),
    COMPACT_READ_BYTES(0x4C),
    COMPACT_WRITE_BYTES(0x4D),
    FLUSH_WRITE_BYTES(0x4E),
    NUMBER_DIRECT_LOAD_TABLE_PROPERTIES(0x4F),
    NUMBER_SUPERVERSION_ACQUIRES(0x50),
    NUMBER_SUPERVERSION_RELEASES(0x51),
    NUMBER_SUPERVERSION_CLEANUPS(0x52),
    NUMBER_BLOCK_COMPRESSED(0x53),
    NUMBER_BLOCK_DECOMPRESSED(0x54),
    NUMBER_BLOCK_NOT_COMPRESSED(0x55),
    MERGE_OPERATION_TOTAL_TIME(0x56),
    FILTER_OPERATION_TOTAL_TIME(0x57),
    ROW_CACHE_HIT(0x58),
    ROW_CACHE_MISS(0x59),
    READ_AMP_ESTIMATE_USEFUL_BYTES(0x5A),
    READ_AMP_TOTAL_READ_BYTES(0x5B),
    NUMBER_RATE_LIMITER_DRAINS(0x5C),
    NUMBER_ITER_SKIP(0x5D),
    NUMBER_MULTIGET_KEYS_FOUND(0x5E),
    // -0x01 to fixate the new value that incorrectly changed TICKER_ENUM_MAX
    NO_ITERATOR_CREATED(-0x01),
    NO_ITERATOR_DELETED(0x60),
    COMPACTION_OPTIMIZED_DEL_DROP_OBSOLETE(0x61),
    COMPACTION_CANCELLED(0x62),
    BLOOM_FILTER_FULL_POSITIVE(0x63),
    BLOOM_FILTER_FULL_TRUE_POSITIVE(0x64),
    BLOB_DB_NUM_PUT(0x65),
    BLOB_DB_NUM_WRITE(0x66),
    BLOB_DB_NUM_GET(0x67),
    BLOB_DB_NUM_MULTIGET(0x68),
    BLOB_DB_NUM_SEEK(0x69),
    BLOB_DB_NUM_NEXT(0x6A),
    BLOB_DB_NUM_PREV(0x6B),
    BLOB_DB_NUM_KEYS_WRITTEN(0x6C),
    BLOB_DB_NUM_KEYS_READ(0x6D),
    BLOB_DB_BYTES_WRITTEN(0x6E),
    BLOB_DB_BYTES_READ(0x6F),
    BLOB_DB_WRITE_INLINED(0x70),
    BLOB_DB_WRITE_INLINED_TTL(0x71),
    BLOB_DB_WRITE_BLOB(0x72),
    BLOB_DB_WRITE_BLOB_TTL(0x73),
    BLOB_DB_BLOB_FILE_BYTES_WRITTEN(0x74),
    BLOB_DB_BLOB_FILE_BYTES_READ(0x75),
    BLOB_DB_BLOB_FILE_SYNCED(0x76),
    BLOB_DB_BLOB_INDEX_EXPIRED_COUNT(0x77),
    BLOB_DB_BLOB_INDEX_EXPIRED_SIZE(0x78),
    BLOB_DB_BLOB_INDEX_EVICTED_COUNT(0x79),
    BLOB_DB_BLOB_INDEX_EVICTED_SIZE(0x7A),
    BLOB_DB_GC_NUM_FILES(0x7B),
    BLOB_DB_GC_NUM_NEW_FILES(0x7C),
    BLOB_DB_GC_FAILURES(0x7D),
    BLOB_DB_GC_NUM_KEYS_OVERWRITTEN(0x7E),
    BLOB_DB_GC_NUM_KEYS_EXPIRED(0x7F),
    BLOB_DB_GC_NUM_KEYS_RELOCATED(-0x02),
    BLOB_DB_GC_BYTES_OVERWRITTEN(-0x03),
    BLOB_DB_GC_BYTES_EXPIRED(-0x04),
    BLOB_DB_GC_BYTES_RELOCATED(-0x05),
    BLOB_DB_FIFO_NUM_FILES_EVICTED(-0x06),
    BLOB_DB_FIFO_NUM_KEYS_EVICTED(-0x07),
    BLOB_DB_FIFO_BYTES_EVICTED(-0x08),
    TXN_PREPARE_MUTEX_OVERHEAD(-0x09),
    TXN_OLD_COMMIT_MAP_MUTEX_OVERHEAD(-0x0A),
    TXN_DUPLICATE_KEY_OVERHEAD(-0x0B),
    TXN_SNAPSHOT_MUTEX_OVERHEAD(-0x0C),
    TICKER_ENUM_MAX(0x5F)
}
