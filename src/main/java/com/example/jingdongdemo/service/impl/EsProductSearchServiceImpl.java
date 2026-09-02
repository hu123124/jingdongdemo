package com.example.jingdongdemo.service.impl;

import com.example.jingdongdemo.document.ProductDoc;
import com.example.jingdongdemo.dto.ProductPageRequest;
import com.example.jingdongdemo.service.EsProductSearchService;
import com.example.jingdongdemo.vo.PageResultVO;
import com.example.jingdongdemo.vo.ProductVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EsProductSearchServiceImpl implements EsProductSearchService {

    /** Spring Boot 自动配置的 ES 底层 HTTP 客户端（和 curl 直连等价） */
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    /** 索引名与配置一致（本地 jd_product_dev / 生产 jd_product） */
    @Value("${es.product-index:jd_product}")
    private String productIndex;

    private static final int MAX_WINDOW = 10000;

    @Override
    public PageResultVO<ProductVO> search(ProductPageRequest req) {
        int pageNum = req.getPageNum() == null ? 1 : req.getPageNum();
        int pageSize = req.getPageSize() == null ? 10 : req.getPageSize();
        pageSize = Math.min(pageSize, 100);
        int from = (pageNum - 1) * pageSize;
        if (from + pageSize > MAX_WINDOW) {
            throw new IllegalStateException("超过 ES 深分页上限(10000)，请缩小查询范围或改用 search_after");
        }
        String keyword = req.getKeyword().trim();

        // ===== 1) 拼完整 DSL（这份 JSON 与 curl 直连验证过的完全一致）=====
        String kw = keyword.replace("\\", "\\\\").replace("\"", "\\\"");
        StringBuilder dsl = new StringBuilder();
        dsl.append("{\"from\":").append(from)
                .append(",\"size\":").append(pageSize)
                .append(",\"query\":{\"bool\":{\"must\":[{\"multi_match\":{")
                .append("\"query\":\"").append(kw).append("\"")
                .append(",\"fields\":[\"name^3\",\"subtitle\"]")
                .append("}}]");

        // filter：分类 + 价格区间（不参与打分）
        List<String> filters = new ArrayList<>();
        if (req.getCategoryId() != null) {
            filters.add("{\"term\":{\"categoryId\":\"" + req.getCategoryId() + "\"}}");
        }
        if (req.getMinPrice() != null) {
            filters.add("{\"range\":{\"price\":{\"gte\":" + req.getMinPrice() + "}}}");
        }
        if (req.getMaxPrice() != null) {
            filters.add("{\"range\":{\"price\":{\"lte\":" + req.getMaxPrice() + "}}}");
        }
        if (!filters.isEmpty()) {
            dsl.append(",\"filter\":[").append(String.join(",", filters)).append("]");
        }
        dsl.append("}}");

        // 排序
        if ("price_asc".equals(req.getSort())) {
            dsl.append(",\"sort\":[{\"price\":\"asc\"}]");
        } else if ("price_desc".equals(req.getSort())) {
            dsl.append(",\"sort\":[{\"price\":\"desc\"}]");
        } else if ("sales_desc".equals(req.getSort())) {
            dsl.append(",\"sort\":[{\"sales\":\"desc\"}]");
        }

        // 高亮
        dsl.append(",\"highlight\":{\"pre_tags\":[\"<em>\"],\"post_tags\":[\"</em>\"],")
                .append("\"fields\":{\"name\":{},\"subtitle\":{}}}");
        dsl.append("}");

        log.info(">>> ES 搜索 DSL: {}", dsl);
        try {
            // ===== 2) 原样发给 ES（RestClient = Spring Boot 自动配置的底层客户端）=====
            Request esRequest = new Request("POST", "/jd_product/_search");
            esRequest.setJsonEntity(dsl.toString());
            Response esResponse = restClient.performRequest(esRequest);
            JsonNode root = objectMapper.readTree(esResponse.getEntity().getContent());

            // ===== 3) 解析响应：_source -> ProductDoc -> ProductVO，highlight 覆盖 name/subtitle =====
            JsonNode hitsNode = root.path("hits");
            long total = hitsNode.path("total").path("value").asLong();
            List<ProductVO> list = new ArrayList<>();
            for (JsonNode hit : hitsNode.path("hits")) {
                ProductDoc doc = objectMapper.treeToValue(hit.path("_source"), ProductDoc.class);
                ProductVO vo = new ProductVO();
                vo.setId(doc.getId());
                vo.setCategoryId(doc.getCategoryId() == null ? null : Long.valueOf(doc.getCategoryId()));
                vo.setName(doc.getName());
                vo.setSubtitle(doc.getSubtitle());
                vo.setMainImage(doc.getMainImage());
                vo.setPrice(doc.getPrice());
                vo.setSales(doc.getSales());
                vo.setStatus(1);

                JsonNode hl = hit.path("highlight");
                if (hl.path("name").size() > 0) {
                    vo.setName(hl.path("name").get(0).asText());
                }
                if (hl.path("subtitle").size() > 0) {
                    vo.setSubtitle(hl.path("subtitle").get(0).asText());
                }
                list.add(vo);
            }

            PageResultVO<ProductVO> result = new PageResultVO<>();
            result.setList(list);
            result.setTotal(total);
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);
            result.setPages((int) Math.ceil(total / (double) pageSize));
            return result;
        } catch (Exception e) {
            throw new RuntimeException("ES 查询失败: " + e.getMessage(), e);
        }
    }
}