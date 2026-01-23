package top.mushanyu.web.util;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用于数据填充的工具类，可从数据源中提取数据并填充到目标对象中。
 * 建议在对应viewer中添加fill xxx方法，如：
 * static void fillResourceTypeName(List&lt;PurchaseFormItemViewer&gt; list, Function&lt;List&lt;Long&gt;, List&lt;ResourceTypeDTO&gt;&gt; dataFetcher)
 * <p>
 * 需要填充target中对象的单个字段使用示例：
 * <p>
 * <blockquote><pre>
 *  DataPopulate.with(list)
 *              .fetch(RoomViewer::getCreateId, userService::findByIds)
 *              .toMap(UserDTO::getId, UserDTO::getName)
 *              .set(RoomViewer::setCreateName);
 * </pre></blockquote>
 * <p>
 * 需要填充target中对象的多个字段使用示例：
 * <p>
 * <blockquote><pre>
 *  DataPopulate.with(list)
 *              .fetch(RoomViewer::getStockId, stockService::findByIds)
 *              //.fetch(stockLocationViewer -> stockLocationViewer.getStockId(), stockIds -> stockService.findByIds(stockIds))
 *              .toMap(StockDTO::getId, Function.identity())
 *              .set((viewer, stockDTO) -> {
 *                   viewer.setStockName(dto.getName());
 *                   viewer.setStockCode(dto.getCode());
 *                   viewer.setStockClassId(dto.getStockClassId());
 *               });
 * </pre></blockquote>
 * <p>
 * 多相同含义key使用示例：
 * <p>
 * 需要填充target中的对象的多个字段，
 * 这多个字段表示同样的含义如createId, modifyId都是表示userId，可以参考以下使用示例，
 * 可以通过set的函数式编程实现更复杂的映射逻辑。
 * <blockquote><pre>
 * DataPopulate.with(list)
 *             .multiFetch(userIds -> Arrays.asList(getCreateId(), getModifyId()), userService::findByIds)
 *             .toMap(UserDTO::getId, UserDTO::getUsername)
 *             .set((viewer, userMap) -> {
 *                     viewer.setCreateCode(map.get(viewer.getCreateId()));
 *                     viewer.setModifyCode(map.get(viewer.getModifyId()));
 *                 });
 * </pre></blockquote>
 * <p>
 * 需要对fetch的数据进行分组然后填充的示例：
 * <p>
 * <blockquote><pre>
 * DataPopulate.with(list)
 *             .fetch(ResourceAttrViewer::getId, resourceAttrOptionsService::findByResourceAttrIds)
 *             .groupingBy(ResourceAttrOptionsDTO::getResourceAttrId)
 *             .set(ResourceAttrViewer::setOptions);
 * </pre></blockquote>
 * <p>
 * key数据类型不一致处理示例：
 * <p>
 * <blockquote><pre>
 * DataPopulate.with(conditions)
 *      .fetch(spuConditionDTO -> Long.parseLong(spuConditionDTO.getValue()),
 *              channelTypeService::findByIds)
 *      .toMap(ChannelTypeDTO::getId, ChannelTypeDTO::getName)
 *      .set(SpuConditionDTO::setValueName);
 * </pre></blockquote>
 */
public class DataPopulateUtil<T> {
    private final List<T> sources;

    /**
     * 构造函数，初始化数据源。
     *
     * @param sources 数据源列表
     */
    private DataPopulateUtil(List<T> sources) {
        this.sources = sources;
    }

    /**
     * 创建一个 DataPopulate 实例。
     *
     * @param sources 数据源列表
     * @param <T>     数据源对象的类型
     * @return DataPopulate 实例
     */
    public static <T> DataPopulateUtil<T> with(List<T> sources) {
        return new DataPopulateUtil<>(sources);
    }

    /**
     * 从数据源中提取键，并通过数据获取器获取对应的数据。
     *
     * @param keyExtractor 用于从数据源对象中提取键的函数
     * @param dataFetcher  用于根据键列表获取数据列表的函数
     * @param <K>          键的类型
     * @param <D>          数据的类型
     * @return FetchedDataSet 实例
     */
    public <K, D> FetchedDataSet<K, D> fetch(Function<T, K> keyExtractor, Function<List<K>, List<D>> dataFetcher) {
        Objects.requireNonNull(keyExtractor, "Key extractor must be set");
        Objects.requireNonNull(dataFetcher, "Data fetcher must be set");
        List<D> data = null;
        if (!CollectionUtils.isEmpty(sources)) {
            List<K> keys = sources.stream()
                    .map(keyExtractor)
                    .filter(Objects::nonNull)
                    .distinct().toList();
            if (!CollectionUtils.isEmpty(keys)) {
                data = dataFetcher.apply(keys);
            }
        }
        return new FetchedDataSet<>(sources, keyExtractor, data);
    }

    /**
     * 从数据源中提取键，并通过数据获取器获取对应的数据。
     *
     * @param keyExtractors 用于从数据源对象中提取键的函数
     * @param dataFetcher   用于根据键列表获取数据列表的函数
     * @param <K>           键的类型
     * @param <D>           数据的类型
     * @return FetchedDataSet 实例
     */
    public <K, D> MultiFetchedDataSet<D> multiFetch(Function<T, List<K>> keyExtractors, Function<List<K>, List<D>> dataFetcher) {
        Objects.requireNonNull(keyExtractors, "Key extractor must be set");
        Objects.requireNonNull(dataFetcher, "Data fetcher must be set");
        List<D> data = null;
        if (!CollectionUtils.isEmpty(sources)) {
            List<K> keys = sources.stream()
                    .map(keyExtractors)
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .distinct().toList();
            if (!CollectionUtils.isEmpty(keys)) {
                data = dataFetcher.apply(keys);
            }
        }
        return new MultiFetchedDataSet<>(sources, data);
    }

    public class FetchedDataSet<K, D> {
        private final List<T> sources;
        private final Function<T, K> keyExtractor;
        private final List<D> data;

        /**
         * 构造函数，初始化数据源、键提取器和数据列表。
         *
         * @param sources      数据源列表
         * @param keyExtractor 用于从数据源对象中提取键的函数
         * @param data         数据列表
         */
        private FetchedDataSet(List<T> sources, Function<T, K> keyExtractor, List<D> data) {
            this.sources = sources;
            this.keyExtractor = keyExtractor;
            this.data = data;
        }

        /**
         * 将数据列表转换为键值对映射。
         *
         * @param dataKeyMapper 用于从数据对象中提取键的函数
         * @param valueMapper   用于从数据对象中提取值的函数
         * @param <V>           值的类型
         * @return PopulateFinalizer 实例
         */
        public <V> PopulateFinalizer<K, V> toMap(Function<D, K> dataKeyMapper, Function<D, V> valueMapper) {
            Objects.requireNonNull(dataKeyMapper, "Data key mapper must be set");
            Objects.requireNonNull(valueMapper, "Value mapper must be set");
            Map<K, V> valueMap = null;
            if (data != null && !CollectionUtils.isEmpty(data)) {
                valueMap = data.stream()
                        .collect(Collectors.toMap(
                                dataKeyMapper,
                                valueMapper,
                                (existing, replacement) -> existing
                        ));
            }
            return new PopulateFinalizer<>(sources, keyExtractor, valueMap);
        }

        /**
         * 将数据列表转换为键值对映射。
         *
         * @param dataKeyMapper 用于从数据对象中提取键的函数
         * @return PopulateFinalizer 实例
         */
        public PopulateFinalizer<K, List<D>> groupingBy(Function<D, K> dataKeyMapper) {
            Objects.requireNonNull(dataKeyMapper, "Data key mapper must be set");
            Map<K, List<D>> valueMap = null;
            if (data != null && !CollectionUtils.isEmpty(data)) {
                valueMap = data.stream().collect(Collectors.groupingBy(dataKeyMapper));
            }
            return new PopulateFinalizer<>(sources, keyExtractor, valueMap);
        }
    }

    public class MultiFetchedDataSet<D> {
        private final List<T> sources;
        private final List<D> data;

        /**
         * 构造函数，初始化数据源、键提取器和数据列表。
         *
         * @param sources 数据源列表
         * @param data    数据列表
         */
        private MultiFetchedDataSet(List<T> sources, List<D> data) {
            this.sources = sources;
            this.data = data;
        }

        /**
         * 将数据列表转换为键值对映射。
         *
         * @param dataKeyMapper 用于从数据对象中提取键的函数
         * @param valueMapper   用于从数据对象中提取值的函数
         * @param <V>           值的类型
         * @return PopulateFinalizer 实例
         */
        public <K, V> MultiPopulateFinalizer<K, V> toMap(Function<D, K> dataKeyMapper, Function<D, V> valueMapper) {
            Objects.requireNonNull(dataKeyMapper, "Data key mapper must be set");
            Objects.requireNonNull(valueMapper, "Value mapper must be set");
            Map<K, V> valueMap = null;
            if (data != null && !CollectionUtils.isEmpty(data)) {
                valueMap = data.stream()
                        .collect(Collectors.toMap(
                                dataKeyMapper,
                                valueMapper,
                                (existing, replacement) -> existing
                        ));
            }
            return new MultiPopulateFinalizer<>(sources, valueMap);
        }
    }

    public class PopulateFinalizer<K, V> {
        private final List<T> sources;
        private final Function<T, K> keyExtractor;
        private final Map<K, V> valueMap;

        /**
         * 构造函数，初始化数据源、键提取器和键值对映射。
         *
         * @param sources      数据源列表
         * @param keyExtractor 用于从数据源对象中提取键的函数
         * @param valueMap     键值对映射
         */
        private PopulateFinalizer(List<T> sources, Function<T, K> keyExtractor, Map<K, V> valueMap) {
            this.sources = sources;
            this.keyExtractor = keyExtractor;
            this.valueMap = valueMap;
        }

        /**
         * 将键值对映射中的值设置到数据源对象中。
         *
         * @param valueSetter 用于将值设置到数据源对象中的函数
         */
        public void set(BiConsumer<T, V> valueSetter) {
            Objects.requireNonNull(valueSetter, "Value setter must be set");
            if (valueMap != null) {
                sources.forEach(source -> {
                    K key = keyExtractor.apply(source);
                    if (key != null && valueMap.containsKey(key)) {
                        valueSetter.accept(source, valueMap.get(key));
                    }
                });
            }
        }
    }

    public class MultiPopulateFinalizer<K, V> {
        private final List<T> sources;
        private final Map<K, V> valueMap;

        /**
         * 构造函数，初始化数据源、键提取器和键值对映射。
         *
         * @param sources  数据源列表
         * @param valueMap 键值对映射
         */
        private MultiPopulateFinalizer(List<T> sources, Map<K, V> valueMap) {
            this.sources = sources;
            this.valueMap = valueMap;
        }

        /**
         * 将键值对映射中的值设置到数据源对象中。
         *
         * @param valueSetter 用于将值设置到数据源对象中的函数
         */
        public void set(BiConsumer<T, Map<K, V>> valueSetter) {
            Objects.requireNonNull(valueSetter, "Value setter must be set");
            if (valueMap != null) {
                sources.forEach(source -> valueSetter.accept(source, valueMap));
            }
        }
    }
}