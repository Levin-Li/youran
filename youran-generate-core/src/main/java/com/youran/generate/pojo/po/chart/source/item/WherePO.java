package com.youran.generate.pojo.po.chart.source.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.youran.common.constant.ErrorCode;
import com.youran.common.exception.BusinessException;
import com.youran.generate.constant.SourceItemType;
import com.youran.generate.pojo.dto.chart.source.item.ChartSourceItemFeatureDTO;
import com.youran.generate.pojo.mapper.FeatureMapper;
import com.youran.generate.pojo.mapper.chart.MetaChartSourceItemMapper;
import com.youran.generate.pojo.po.MetaEntityPO;
import com.youran.generate.pojo.po.MetaFieldPO;
import com.youran.generate.pojo.po.chart.source.MetaChartSourcePO;

/**
 * where条件
 *
 * @author: cbb
 * @date: 2020-04-05
 */
public class WherePO extends MetaChartSourceItemPO {

    /**
     * 字段id
     */
    private Integer fieldId;

    /**
     * 过滤运算符
     */
    private Integer filterOperator;

    /**
     * 过滤值
     */
    private String[] filterValue;

    /**
     * 时间粒度
     */
    private Integer timeGranularity;

    /**
     * 是否自定义
     */
    private Boolean custom;

    /**
     * 自定义内容
     */
    private String customContent;

    /**
     * 对应实体
     */
    @JsonIgnore
    private transient MetaEntityPO entity;
    /**
     * 对应字段
     */
    @JsonIgnore
    private transient MetaFieldPO field;

    public WherePO() {
        this.setType(SourceItemType.WHERE.getValue());
    }


    @Override
    public void assembleItem(MetaChartSourcePO chartSource) {
        super.assembleItem(chartSource);
        // 非自定义的情况下，需要装配实体和字段对象
        if(!custom) {
            if (this.getJoinIndex() == 0) {
                this.entity = chartSource.getEntity();
            } else {
                this.entity = this.getJoin().getRightEntity();
            }
            if (this.entity == null) {
                throw new BusinessException(ErrorCode.INNER_DATA_ERROR, "图表where条件异常，所属实体不存在");
            }
            this.field = this.entity.getFields().get(this.fieldId);
            if (this.field == null) {
                throw new BusinessException(ErrorCode.INNER_DATA_ERROR, "图表where条件异常，所属字段不存在");
            }
        }
    }


    /**
     * 将超类转成当前类型
     *
     * @param superPO
     * @param featureDeserialize
     * @return
     */
    public static WherePO fromSuperType(MetaChartSourceItemPO superPO,
                                        boolean featureDeserialize) {
        if (!SourceItemType.WHERE.getValue().equals(superPO.getType())) {
            throw new BusinessException(ErrorCode.INNER_DATA_ERROR, "类型转换异常");
        }
        WherePO po = new WherePO();
        MetaChartSourceItemMapper.INSTANCE.copyProperties(po, superPO);
        if (featureDeserialize) {
            po.featureDeserialize();
        }
        return po;
    }

    @Override
    public void featureDeserialize() {
        ChartSourceItemFeatureDTO featureDTO = FeatureMapper.asChartSourceItemFeatureDTO(this.getFeature());
        this.fieldId = featureDTO.getFieldId();
        this.filterOperator = featureDTO.getFilterOperator();
        this.filterValue = featureDTO.getFilterValue();
        this.timeGranularity = featureDTO.getTimeGranularity();
        this.custom = featureDTO.getCustom();
        this.customContent = featureDTO.getCustomContent();
    }

    @Override
    public void featureSerialize() {
        ChartSourceItemFeatureDTO featureDTO = new ChartSourceItemFeatureDTO();
        featureDTO.setFieldId(this.fieldId);
        featureDTO.setFilterOperator(this.filterOperator);
        featureDTO.setFilterValue(this.filterValue);
        featureDTO.setTimeGranularity(this.timeGranularity);
        featureDTO.setCustom(this.custom);
        featureDTO.setCustomContent(this.customContent);
        this.setFeature(FeatureMapper.asString(featureDTO));
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public Integer getFilterOperator() {
        return filterOperator;
    }

    public void setFilterOperator(Integer filterOperator) {
        this.filterOperator = filterOperator;
    }

    public String[] getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String[] filterValue) {
        this.filterValue = filterValue;
    }

    public Boolean getCustom() {
        return custom;
    }

    public void setCustom(Boolean custom) {
        this.custom = custom;
    }

    public String getCustomContent() {
        return customContent;
    }

    public void setCustomContent(String customContent) {
        this.customContent = customContent;
    }

    public MetaEntityPO getEntity() {
        return entity;
    }

    public void setEntity(MetaEntityPO entity) {
        this.entity = entity;
    }

    public MetaFieldPO getField() {
        return field;
    }

    public void setField(MetaFieldPO field) {
        this.field = field;
    }

    public Integer getTimeGranularity() {
        return timeGranularity;
    }

    public void setTimeGranularity(Integer timeGranularity) {
        this.timeGranularity = timeGranularity;
    }
}
