import chartOptions from '@/utils/options-chart'
import searchUtil from '../searchUtil'

function initFormBean () {
  return {
    _displayText: '',
    joinIndex: null,
    fieldId: null,
    filterOperator: null,
    filterValue: null,
    timeGranularity: null,
    custom: false,
    customContent: null
  }
}

function initTmp () {
  return {
    // 过滤字段下拉框绑定对象
    tmp1: {
      key: '',
      field: null,
      joinIndex: null
    },
    // 过滤操作符下拉框绑定对象
    tmp2: {
      value: null,
      label: null,
      filterValueType: null,
      matchFieldTypes: [],
      timeGranularity: false
    },
    // 单过滤值绑定对象
    tmp3: [null],
    // 双过滤值绑定对象
    tmp4: [null, null],
    // 多过滤值绑定对象
    tmp5: []
  }
}

/**
 * 从form中抽取数据到tmp
 */
function formToTmp (form, tmp, entityFieldOptions) {
  if (!form.custom) {
    const [joinIndex, entity] = entityFieldOptions.find(
      ([joinIndex, entity]) => joinIndex === form.joinIndex)
    const field = searchUtil.findFieldInEntity(entity, form.fieldId)
    tmp.tmp1 = {
      key: joinIndex + '_' + field.fieldId,
      field: field,
      joinIndex: joinIndex
    }
    tmp.tmp2 = chartOptions.getFilterOperatorOption(form.filterOperator)
    if (tmp.tmp2.filterValueType === 1) {
      tmp.tmp3 = form.filterValue
    } else if (tmp.tmp2.filterValueType === 2) {
      tmp.tmp4 = form.filterValue
    } else if (tmp.tmp2.filterValueType === 3) {
      tmp.tmp5 = form.filterValue
    }
  }
}

/**
 * 从tmp中抽取数据到form
 */
function tmpToForm (tmp, form) {
  if (!form.custom) {
    form.joinIndex = tmp.tmp1.joinIndex
    form.fieldId = tmp.tmp1.field.fieldId
    form.filterOperator = tmp.tmp2.value
    if (tmp.tmp2.filterValueType === 1) {
      form.filterValue = tmp.tmp3
    } else if (tmp.tmp2.filterValueType === 2) {
      form.filterValue = tmp.tmp4
    } else if (tmp.tmp2.filterValueType === 3) {
      form.filterValue = tmp.tmp5
    }
    form._displayText = 't' + form.joinIndex + '.' + tmp.tmp1.field.fieldName +
      tmp.tmp2.display(tmp.tmp1.field.jfieldType, form.filterValue, form.timeGranularity)
  } else {
    form._displayText = '[自定义内容]'
  }
}

/**
 * 表单回显时修复过滤条件数据
 */
function repairWhere (where, sourceForm) {
  const joinIndex = where.joinIndex
  if (where.custom) {
    where._displayText = '[自定义内容]'
  } else {
    const field = searchUtil.findEntityFieldInFormBean(sourceForm, joinIndex, where.fieldId)[1]
    const operatorOption = chartOptions.getFilterOperatorOption(where.filterOperator)
    where._displayText = 't' + where.joinIndex + '.' + field.fieldName +
      operatorOption.display(field.jfieldType, where.filterValue, where.timeGranularity)
  }
}

export default {
  initFormBean,
  initTmp,
  formToTmp,
  tmpToForm,
  repairWhere
}
