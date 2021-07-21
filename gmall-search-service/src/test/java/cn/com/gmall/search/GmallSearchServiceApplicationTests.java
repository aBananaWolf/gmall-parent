package cn.com.gmall.search;

import cn.com.gmall.beans.PmsSearchSkuInfo;
import cn.com.gmall.beans.PmsSkuInfo;
import cn.com.gmall.service.manage.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class GmallSearchServiceApplicationTests {
    @Reference
    private SkuService skuService;

    @Autowired
    private JestClient jestClient;

    @Test
    void addIndexData() throws InvocationTargetException, IllegalAccessException, IOException {
        List<PmsSkuInfo> totalSku = skuService.getAllSkuInfo();
        ArrayList<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        for (PmsSkuInfo skuInfo : totalSku) {
            PmsSearchSkuInfo searchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(searchSkuInfo, skuInfo);
            pmsSearchSkuInfos.add(searchSkuInfo);
        }
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            Index index = new Index.Builder(pmsSearchSkuInfo).index("gmall").type("PmsSkuInfo").id(String.valueOf(pmsSearchSkuInfo.getId())).build();
            jestClient.execute(index);
        }
        System.out.println(jestClient);
    }

    @Test
    public void searchTest() throws IOException {
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        // searchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        // terms
        TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder("skuAttrValueList.valueId", new int[]{83, 79});
        // filter
        boolQueryBuilder.filter(termsQueryBuilder);
        // must
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", "华为");
        MatchQueryBuilder matchQueryBuilder1 = new MatchQueryBuilder("skuName", "麒麟");
        boolQueryBuilder.must(matchQueryBuilder);
        boolQueryBuilder.must(matchQueryBuilder1);

        // 执行
        searchSourceBuilder.query(boolQueryBuilder);
        System.err.println(searchSourceBuilder.toString());

        Search build = new Search.Builder(searchSourceBuilder.toString()).addIndex("gmall").addType("PmsSkuInfo").build();
        SearchResult execute = jestClient.execute(build);
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo source = hit.source;
            pmsSearchSkuInfos.add(source);
        }
    }

}
