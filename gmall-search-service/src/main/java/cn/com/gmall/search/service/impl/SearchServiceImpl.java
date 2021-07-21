package cn.com.gmall.search.service.impl;

import cn.com.gmall.beans.PmsBaseAttrInfo;
import cn.com.gmall.beans.PmsSearchParam;
import cn.com.gmall.beans.PmsSearchSkuInfo;
import cn.com.gmall.constants.CacheUnit;
import cn.com.gmall.search.constants.SearchUnit;
import cn.com.gmall.search.mapper.SearchByBaseAttrInfoMapper;
import cn.com.gmall.service.search.SearchService;
import cn.com.gmall.util.CacheUtil;
import cn.com.gmall.util.JedisUtil;
import com.alibaba.fastjson.JSON;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private JestClient jestClient;
    @Autowired
    private SearchByBaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private JedisUtil jedisUtil;

    @Override
    public List<PmsSearchSkuInfo> searchByParam(PmsSearchParam pmsSearchParam) {
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        // searchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if (StringUtils.isNotBlank(pmsSearchParam.getKeyword())) {
            // must
            MatchQueryBuilder matchQueryBySkuName = new MatchQueryBuilder(SearchUnit.GMALL_SKU_NAME, pmsSearchParam.getKeyword());
            MatchQueryBuilder matchQueryBySkuDesc = new MatchQueryBuilder(SearchUnit.GMALL_SKU_DESC, pmsSearchParam.getKeyword());
            // should
            boolQueryBuilder.should(matchQueryBySkuName);
            boolQueryBuilder.should(matchQueryBySkuDesc);
        }
        if (pmsSearchParam.getValueId() != null) {
            for (Long pmsSkuAttrValue : pmsSearchParam.getValueId()) {
                // terms SearchUnit."skuAttrValueList.valueId"
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder(SearchUnit.GMALL_SKU_ATTR_VALUE_LIST_SUB_ATTR_VALUE_ID, pmsSkuAttrValue);
                // filter
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        if (pmsSearchParam.getCatalog3Id() != null) {// SearchUnit catalog3Id
            MatchQueryBuilder matchCatalog3Id = new MatchQueryBuilder(SearchUnit.GMALL_CATALOG3_ID, pmsSearchParam.getCatalog3Id());
            boolQueryBuilder.must(matchCatalog3Id);
        }

        // 执行
        searchSourceBuilder.query(boolQueryBuilder);
        // 分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);
        searchSourceBuilder.sort("id", SortOrder.DESC);
        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        List<HighlightBuilder.Field> fields = highlightBuilder.fields();//SearchUnit SearchUnit.GMALL_SKU_NAME
        HighlightBuilder.Field field = new HighlightBuilder.Field(SearchUnit.GMALL_SKU_DESC);
        HighlightBuilder.Field field1 = new HighlightBuilder.Field(SearchUnit.GMALL_SKU_NAME);
        fields.add(field);
        fields.add(field1);
        highlightBuilder.fields();
        highlightBuilder.preTags(SearchUnit.GMALL_HIGH_LIGHT_PRETAG);
        highlightBuilder.postTags(SearchUnit.GMALL_HIGH_LIGHT_POSTTAG);
        searchSourceBuilder.highlighter(highlightBuilder);
        // 聚合
        TermsAggregationBuilder terms = AggregationBuilders.terms(SearchUnit.GMALL_GROUP_BY_ATTR).field(SearchUnit.GMALL_SKU_ATTR_VALUE_LIST_SUB_ATTR_VALUE_ID);
        terms.size(999);
        searchSourceBuilder.aggregation(terms);
        Search build = new Search.Builder(searchSourceBuilder.toString()).addIndex(SearchUnit.GMALL_INDEX).addType(SearchUnit.GMALL_TYPE).build();
        SearchResult execute = null;
        try {
            execute = jestClient.execute(build);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (execute != null) {
            List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
            for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
                PmsSearchSkuInfo source = hit.source;
                Map<String, List<String>> highlight = hit.highlight;
                if (highlight != null) {
                    List<String> skuName = highlight.get(SearchUnit.GMALL_SKU_NAME);
                    if (skuName != null) {
                        source.setSkuName(skuName.get(0));
                    }
                    List<String> skuDesc = highlight.get(SearchUnit.GMALL_SKU_DESC);
                    if (skuDesc != null) {
                        source.setSkuDesc(skuDesc.get(0));
                    }
                }
                pmsSearchSkuInfos.add(source);
            }
            MetricAggregation aggregations = execute.getAggregations();
            TermsAggregation aggregation = aggregations.getAggregation(SearchUnit.GMALL_GROUP_BY_ATTR, TermsAggregation.class);
            ArrayList<Long> longs = new ArrayList<>();
            for (TermsAggregation.Entry bucket : aggregation.getBuckets()) {
                longs.add(Long.parseLong(bucket.getKey()));
            }
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            pmsSearchSkuInfo.setAggregationValueId(longs);
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }
        return pmsSearchSkuInfos;
    }

    @Override
    public List<PmsBaseAttrInfo> searchByBaseAttrInfo(List<Long> finalValueIds) {
        List<PmsBaseAttrInfo> baseAttrInfosFromDB = null;
        try (Jedis jedis = jedisUtil.getJedis()) {
            if (jedis != null) {
                String cacheKey = SearchUnit.BASE_ATTR_INFO_PREFIX + JSON.toJSONString(finalValueIds) + SearchUnit.BASE_ATTR_INFO_SUFFIX;
                String cacheValue = jedis.get(cacheKey);
                if (cacheValue != null) {
                    baseAttrInfosFromDB = JSON.parseArray(cacheValue, PmsBaseAttrInfo.class);
                } else {
                    // jedis 不为空则可缓存 *
                    this.searchByBaseAttrInfoAccessDB(true, jedis, baseAttrInfosFromDB, finalValueIds, cacheKey);
                }
            } else {
                // redis宕掉了 假装我们有作为锁的jedis
                this.searchByBaseAttrInfoAccessDB(false, jedis, baseAttrInfosFromDB, finalValueIds, null);
            }
        }
        return baseAttrInfosFromDB;
    }


    private List<PmsBaseAttrInfo> searchByBaseAttrInfoAccessDB(boolean isCache, Jedis jedis, List<PmsBaseAttrInfo> baseAttrInfosFromDB, List<Long> finalValueIds, String cacheKey) {
        String lockKey = cacheKey + CacheUnit.LOCK_SUFFIX;
        String lockValue = UUID.randomUUID().toString();
        String ok = jedis.set(lockKey, lockValue, CacheUnit.LOCK_NX_XX, CacheUnit.LOCK_EXPIRE_UNIT, CacheUnit.LOCK_EXPIRE_TIME);
        if ("OK".equals(ok)) {
            baseAttrInfosFromDB = baseAttrInfoMapper.selectByBaseAttrInfo(finalValueIds);
            baseAttrInfosFromDB = CacheUtil.distributedLockByList(baseAttrInfosFromDB, lockKey, lockValue, jedis, cacheKey, isCache);
        } else {
            CacheUtil.spinSleep();
            return this.searchByBaseAttrInfo(finalValueIds);
        }
        return baseAttrInfosFromDB;
    }

}
