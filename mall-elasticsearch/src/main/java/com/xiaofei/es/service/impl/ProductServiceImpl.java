package com.xiaofei.es.service.impl;

import com.xiaofei.es.constant.EsSearchConstant;
import com.xiaofei.common.dto.SkuESDto;
import com.xiaofei.common.es.vo.SearchVo;
import com.xiaofei.common.vo.PageVo;
import com.xiaofei.es.entity.Product;
import com.xiaofei.es.repository.ProductRepository;
import com.xiaofei.es.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: 李飞
 * Date: 2021/8/4
 * Time: 18:40
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 将商品信息保存到ES中
     *
     * @param skuESDtos 商品信息
     * @return true：保存成功。false：保存失败
     */
    @Override
    public boolean saveProduct(List<SkuESDto> skuESDtos) {
        try {
            List<Product> products = skuESDtos.stream().map(skuESDto -> {
                Product product = new Product();
                BeanUtils.copyProperties(skuESDto, product);
                return product;
            }).collect(Collectors.toList());
            productRepository.saveAll(products);
        } catch (Exception e) {
            log.error("ES保存商品失败，错误信息为：{}", e);
            return false;
        }
        return true;
    }

    /**
     * 根据商家类别和商品类别查询推荐的相关信息，如果两则都为0，则随机推荐
     *
     * @param brandId    品牌id
     * @param categoryId 类别id
     * @return 返回推荐的数据
     */
    @Override
    public List<Product> getProductRandom(Long brandId, Long categoryId) {
        //构建查询条件
        NativeSearchQuery query = new NativeSearchQueryBuilder().build();

        Pageable pageable = PageRequest.of(0, 100);
        Page<Product> page = productRepository.findAll(pageable);
        return page.getContent();
    }

    /**
     * 搜索商品数据
     *
     * @param searchVo 搜索条件
     */
    @Override
    public PageVo<SearchHits<Product>> searchProduct(SearchVo searchVo) {

       /* //构造插叙条件
        Criteria criteria = new Criteria();

        //价格区间判断
        BigDecimal minPrice = searchVo.getMinPrice();
        BigDecimal maxPrice = searchVo.getMaxPrice();
        if (minPrice.compareTo(maxPrice) > 0) {
            BigDecimal t = minPrice;
            minPrice = maxPrice;
            maxPrice = t;
        }
        criteria.and(new Criteria("skuPrice").greaterThanEqual(minPrice).lessThanEqual(maxPrice));

        //判断搜素条件是否为空
        if (!StringUtils.isEmpty(searchVo.getSearchValue())) {
            criteria.and(new Criteria("skuTitle").is(searchVo.getSearchValue()));
        }

        //判断类别id是否为空
        if (searchVo.getCategoryId() != null && searchVo.getCategoryId() > 0) {
            criteria.and(new Criteria("catalogId").is(searchVo.getCategoryId()));
        }

        //库存判断
        if (searchVo.getHasStock() != null) {
            criteria.and(new Criteria("hasStock").is(searchVo.getHasStock() == 1));
        }

        //判断品牌id
        if (searchVo.getBrandId() != null && searchVo.getBrandId().size() > 0) {
            criteria.and(new Criteria("brandId").in(searchVo.getBrandId()));
        }

        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);

        //分页查询
        criteriaQuery.setPageable(PageRequest.of(searchVo.getPageNo() - 1, searchVo.getPageSize()));

        //判断搜索值是否为空，如果不为空，则设置高亮查询
        if (!StringUtils.isEmpty(searchVo.getSearchValue())) {
            HighlightBuilder highlight = new HighlightBuilder();
            highlight.preTags("<b style='color:red'>");//样式前缀
            highlight.postTags("</b>");//样式后缀
            highlight.field("skuTitle");//设置需要高亮的属性
            criteriaQuery.setHighlightQuery(new HighlightQuery(highlight));
        }

        //排序查询


        //TODO 设置聚合查询
        //criteria.matches()


        SearchHits<Product> search = elasticsearchRestTemplate.search(criteriaQuery, Product.class);
*/

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //价格区间判断
        BigDecimal minPrice = searchVo.getMinPrice();
        BigDecimal maxPrice = searchVo.getMaxPrice();
        if (minPrice.compareTo(maxPrice) > 0) {
            BigDecimal t = minPrice;
            minPrice = maxPrice;
            maxPrice = t;
        }
        queryBuilder.withFilter(QueryBuilders.rangeQuery("skuPrice").gte(minPrice).lte(maxPrice));//价格区间

        //判断搜素条件是否为空
        if (!StringUtils.isEmpty(searchVo.getSearchValue())) {
            queryBuilder.withQuery(QueryBuilders.queryStringQuery(searchVo.getSearchValue()).field("skuTitle"));//匹配查询，需要分词和评分
        }

        //判断类别id
        if (searchVo.getCategoryId() != null && searchVo.getCategoryId() > 0) {
            queryBuilder.withFilter(QueryBuilders.termQuery("categoryId", searchVo.getCategoryId()));
        }

        //库存判断
        if (searchVo.getHasStock() != null) {
            queryBuilder.withFilter(QueryBuilders.termQuery("hasStock", searchVo.getHasStock() == 1));
        }

        //判断品牌id
        if (searchVo.getBrandId() != null && searchVo.getBrandId().size() > 0) {
            queryBuilder.withFilter(QueryBuilders.termsQuery("brandId", searchVo.getBrandId()));
        }

        queryBuilder.withPageable(PageRequest.of(searchVo.getPageNo() - 1, searchVo.getPageSize()));//分页查询

        HighlightBuilder highlightBuilder = new HighlightBuilder();//高亮查询构建
        highlightBuilder.preTags("<span style='color:red;'>");//样式前缀
        highlightBuilder.postTags("</span>");//样式后缀
        highlightBuilder.field("skuTitle");//设置需要高亮的属性
        queryBuilder.withHighlightBuilder(highlightBuilder);//设置高亮查询

        //排序查询
        if (searchVo.getSort() != null) {
            EsSearchConstant.SortStatus sortStatus = EsSearchConstant.SortStatus.getSortStatus(searchVo.getSort());
            queryBuilder.withSort(new FieldSortBuilder(sortStatus.getField()).order(sortStatus.getSortOrder()));
        }

        //聚合品牌信息
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brandId");
        brandAgg.field("brandId").size(50);
        brandAgg.subAggregation(AggregationBuilders.terms("brandName").field("brandName.keyword").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brandImg").field("brandImg.keyword").size(1));
        queryBuilder.addAggregation(brandAgg);

        //聚合类别信息
        TermsAggregationBuilder categoryAgg = AggregationBuilders.terms("categoryId");
        categoryAgg.field("categoryId").size(50);
        categoryAgg.subAggregation(AggregationBuilders.terms("categoryName").field("categoryName.keyword")).size(50);
        queryBuilder.addAggregation(categoryAgg);

        //属性聚合
        TermsAggregationBuilder attrsAgg = AggregationBuilders.terms("attrId");
        attrsAgg.field("attrs.attrId").size(100);
        attrsAgg.subAggregation(AggregationBuilders.terms("attrName").field("attrs.attrName.keyword"));
        attrsAgg.subAggregation(AggregationBuilders.terms("attrValue").field("attrs.attrValue.keyword"));
        queryBuilder.addAggregation(attrsAgg);

        Query query = queryBuilder.build();

        SearchHits<Product> search = elasticsearchRestTemplate.search(query, Product.class);

        //遍历聚合信息
        Aggregations aggregations = search.getAggregations();

        System.out.println("======================================");

        //遍历商品信息
        search.getSearchHits().forEach(System.out::println);

        Integer pageTotal = Math.toIntExact(search.getTotalHits() % searchVo.getPageSize() == 0 ?
                search.getTotalHits() / searchVo.getPageSize() :
                search.getTotalHits() / searchVo.getPageSize() + 1);

        return new PageVo<>(searchVo.getPageNo(), searchVo.getPageSize(), pageTotal, search.getTotalHits(), search);

    }

    /**
     * 高亮查询测试1
     */
    @Override
    public List<Product> test1(String skuTitle) {
        return productRepository.queryBySkuTitle(skuTitle);
    }

    /**
     * 高亮查询测试1
     */
    @Override
    public SearchHits<Product> test2(String searchValue) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                //查询条件
                .withQuery(QueryBuilders.queryStringQuery(searchValue).defaultField("skuTitle"))
                //分页
                .withPageable(PageRequest.of(0, 5))
                .addAggregation(AggregationBuilders.terms("price"))
                //高亮字段显示
                .withHighlightFields(new HighlightBuilder.Field("skuTitle"))
                .build();
        return elasticsearchRestTemplate.search(nativeSearchQuery, Product.class);
    }


}