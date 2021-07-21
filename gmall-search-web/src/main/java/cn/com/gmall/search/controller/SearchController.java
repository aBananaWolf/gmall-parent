package cn.com.gmall.search.controller;

import cn.com.gmall.anno.AuthorityLogin;
import cn.com.gmall.beans.*;
import cn.com.gmall.search.constants.SearchUrlUnit;
import cn.com.gmall.service.search.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
public class SearchController {

    @Reference
    private SearchService searchService;

    @RequestMapping("/list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap) {
        // 搜索catalog3Id 和 keyword  根据属性过滤， 聚合出平台属性
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.searchByParam(pmsSearchParam);
        // 查询出平台属性与值
        int size = pmsSearchSkuInfos.size();
        size--;

        // 根据获取的valueId，查询出所有关联平台属性/值
        List<PmsBaseAttrInfo> attrInfos = searchService.searchByBaseAttrInfo(pmsSearchSkuInfos.get(size).getAggregationValueId());
        pmsSearchSkuInfos.remove(size);

        ArrayList<HashMap<String, String>> crumbs = new ArrayList<>();
        if (pmsSearchParam.getValueId() != null) {
            // 匹配删除，会删除多次，删除的都是点击过的过滤条,点击过多少次，就生成多少面包屑，传过来的valueId是有序的，所以需要作外层循环
            // 做出面包屑,点击的过滤条的所属行数应该被消除，所以每匹配消除一行，就break当前循环
            searchValueId:
            for (Long delValueId : pmsSearchParam.getValueId()) {
                Iterator<PmsBaseAttrInfo> matchDelIterator = attrInfos.iterator();
                // 台属性循环
                while (matchDelIterator.hasNext()) {
                    PmsBaseAttrInfo attrInfo = matchDelIterator.next();
                    // 属性值循环
                    for (PmsBaseAttrValue pmsBaseAttrValue : attrInfo.getAttrValueList()) {
                        if (delValueId.equals(pmsBaseAttrValue.getId())) {
                            // 这个根据这个集合生成多个面包屑
                            // 根据过滤条目，进行生成crumbs ，面包屑材料 ： urlParam valueName
                            HashMap<String, String> crumb = this.getCrumb(pmsSearchParam, delValueId, attrInfo.getAttrName() + SearchUrlUnit.CRUMB_COLOR + pmsBaseAttrValue.getValueName());
                            crumbs.add(crumb);
                            matchDelIterator.remove();
                            // 面包屑并不是有序的，不需要考虑更多的性能问题，continue
                            continue searchValueId;
                        }
                    }
                }
            }
        }
        if (attrInfos != null) {
            Iterator<PmsBaseAttrInfo> checkIterator = attrInfos.iterator();
            boolean flag = false;
            while (checkIterator.hasNext()) {
                if (checkIterator.next().getAttrValueList().size() != 1) {
                    // 只要有一个列表值大于1，那就不做处理
                    flag = true;
                    break;
                }
            }
            if (flag) {
                modelMap.addAttribute("attrList", attrInfos);
            } else {
                PmsBaseAttrInfo attrInfo = attrInfos.get(0);
                attrInfo.setAttrName("过滤完了哦！");
                attrInfo.setAttrValueList(null);
                modelMap.addAttribute("attrList", attrInfo);
            }
        }
        modelMap.addAttribute("skuLsInfoList", pmsSearchSkuInfos);
        modelMap.addAttribute("urlParam", this.getUrlParam(pmsSearchParam));
        modelMap.addAttribute("keyword", pmsSearchParam.getKeyword());
        modelMap.addAttribute("attrValueSelectedList", crumbs);
        return "list";
    }

    /**
     * 获取面包屑
     */
    private HashMap<String, String> getCrumb(PmsSearchParam pmsSearchParam, Long currentValueId, String valueName) {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(pmsSearchParam.getKeyword())) {
            stringBuilder.append(SearchUrlUnit.SEARCH_PARAM_KEYWORD);
            stringBuilder.append(pmsSearchParam.getKeyword());
        }
        if (pmsSearchParam.getCatalog3Id() != null) {
            if (!StringUtils.isBlank(stringBuilder.toString())) {
                stringBuilder.append("&");
            }
            stringBuilder.append(SearchUrlUnit.SEARCH_PARAM_CATALOG3_ID);
            stringBuilder.append(pmsSearchParam.getCatalog3Id());
        }
        stringBuilder.append(SearchUrlUnit.Search_PARAM_VALUE_ID);

        HashMap<String, String> crumb = new HashMap<>();
        if (pmsSearchParam.getValueId() != null) {
            List<Long> valueIds = pmsSearchParam.getValueId();
            int size = valueIds.size();
            for (int i = 0; i < size; i++) {
                Long searchValueId = valueIds.get(i);
                // 如果当前值等于面包屑值，则跳过本次循环
                if (searchValueId.equals(currentValueId)) {
                    continue;
                } else {
                    stringBuilder.append(searchValueId);
                }
                // 如果有下一个值 ，并且如果下一个值不是列表中的最后一个值 等于面包屑值
                int index;
                if ((index = i + 1) == size - 1 && valueIds.get(index).equals(currentValueId)) {
                    break;
                }
                if (index < size) {
                    stringBuilder.append(",");
                }
            }
        }
        crumb.put(SearchUrlUnit.CRUMB_URL_PARAM, stringBuilder.toString());//urlParam valueName
        crumb.put(SearchUrlUnit.CRUMB_VALUE_NAME, valueName);
        return crumb;
    }

    private String getUrlParam(PmsSearchParam pmsSearchParam) {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(pmsSearchParam.getKeyword())) {
            stringBuilder.append(SearchUrlUnit.SEARCH_PARAM_KEYWORD);
            stringBuilder.append(pmsSearchParam.getKeyword());
        }
        if (pmsSearchParam.getCatalog3Id() != null) {
            if (!StringUtils.isBlank(stringBuilder.toString())) {
                stringBuilder.append("&");
            }
            stringBuilder.append(SearchUrlUnit.SEARCH_PARAM_CATALOG3_ID);
            stringBuilder.append(pmsSearchParam.getCatalog3Id());
        }
        stringBuilder.append(SearchUrlUnit.Search_PARAM_VALUE_ID);
        if (pmsSearchParam.getValueId() != null) {
            for (Long valueId : pmsSearchParam.getValueId()) {
                stringBuilder.append(valueId);
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    @AuthorityLogin()
    @RequestMapping("/index")
    public String index() {
        return "index";
    }
}
