package com.example.jingdongdemo.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setting(shards = 1, replicas = 0)
@Document(indexName = "jd_product")
public class ProductDoc {

    /** 商品 ID（与 MySQL t_product.id 一一对应，同步/更新的主键） */
    @Id
    private Long id;

    /** 商品名 —— 中文全文检索核心字段 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String name;

    /** 副标题/卖点，一并参与检索 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String subtitle;

    /** 分类 ID —— 精确过滤/聚合用 keyword（注意用 String 表达，见下方讲解） */
    @Field(type = FieldType.Keyword)
    private String categoryId;

    /** 价格（SKU 最低价）—— 区间过滤/排序用 */
    @Field(type = FieldType.Float)
    private BigDecimal price;

    /** 主图 —— 只随结果返回给前端展示，不参与检索 */
    @Field(type = FieldType.Keyword, index = false)
    private String mainImage;

    /** 销量 —— 列表页展示/排序用 */
    @Field(type = FieldType.Integer)
    private Integer sales;
}