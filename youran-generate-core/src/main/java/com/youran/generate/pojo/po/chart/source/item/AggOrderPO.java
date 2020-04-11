package com.youran.generate.pojo.po.chart.source.item;

import com.youran.generate.constant.SourceItemType;
import com.youran.generate.pojo.dto.chart.source.item.ChartSourceItemFeatureDTO;
import com.youran.generate.pojo.mapper.FeatureMapper;
import com.youran.generate.pojo.po.chart.source.MetaChartSourcePO;

import java.util.List;

/**
 * 聚合排序
 *
 * @author: cbb
 * @date: 2020-04-05
 */
public class AggOrderPO extends MetaChartSourceItemPO {

    /**
     * 排序方式
     */
    private Integer sortType;


    public AggOrderPO() {
        this.setType(SourceItemType.AGG_ORDER.getValue());
    }

    @Override
    public void assembleItem(MetaChartSourcePO chartSource) {
        super.assembleItem(chartSource);
        List<MetricsPO> metricsList = chartSource.getMetricsList();
        metricsList.stream()
            .filter(metricsPO -> metricsPO.getSourceItemId().equals(this.getParentId()))
            .findFirst()
            .ifPresent(metricsPO -> this.setParent(metricsPO));
    }

    @Override
    public void featureDeserialize() {
        ChartSourceItemFeatureDTO featureDTO = FeatureMapper.asChartSourceItemFeatureDTO(this.getFeature());
        this.sortType = featureDTO.getSortType();
    }

    @Override
    public void featureSerialize() {
        ChartSourceItemFeatureDTO featureDTO = new ChartSourceItemFeatureDTO();
        featureDTO.setSortType(this.sortType);
        this.setFeature(FeatureMapper.asString(featureDTO));
    }

    public Integer getSortType() {
        return sortType;
    }

    public void setSortType(Integer sortType) {
        this.sortType = sortType;
    }
}
