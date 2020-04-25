package com.youran.generate.template.context;

import com.youran.common.constant.ErrorCode;
import com.youran.common.exception.BusinessException;
import com.youran.common.util.DateUtil;
import com.youran.generate.constant.JFieldType;
import com.youran.generate.exception.SkipCurrentException;
import com.youran.generate.pojo.dto.MetaProjectFeatureDTO;
import com.youran.generate.pojo.mapper.FeatureMapper;
import com.youran.generate.pojo.po.*;
import com.youran.generate.pojo.po.chart.MetaChartPO;
import com.youran.generate.util.SwitchCaseUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 基本信息上下文对象
 * <p> 包含项目的基本信息
 *
 * @author cbb
 * @date 2018/8/3
 */
public class BaseContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseContext.class);
    /**
     * 当前渲染文件目录
     * 从外部只能赋值一次
     */
    private String currentDir;
    /**
     * 实体列表
     */
    protected List<MetaEntityPO> metaEntities;
    /**
     * 常量列表
     */
    protected List<MetaConstPO> metaConsts;
    /**
     * 多对多列表
     */
    protected List<MetaManyToManyPO> mtms;
    /**
     * 所有图表
     */
    protected List<MetaChartPO> charts;
    /**
     * 包名
     */
    protected String packageName;
    /**
     * common包名
     */
    protected String commonPackage;
    /**
     * 项目标识-驼峰格式-首字母小写
     */
    protected String projectName;
    /**
     * 项目标识-驼峰格式-首字母大写
     */
    protected String projectNameUpper;
    /**
     * 项目标识-短横杠分割
     */
    protected String projectNameSplit;
    /**
     * 项目名称
     */
    protected String projectDesc;
    /**
     * 原始模块名
     */
    protected String originProjectName;
    /**
     * maven的groupId
     */
    protected String groupId;
    /**
     * 作者
     */
    protected String author;
    /**
     * 创建时间
     */
    protected Date createdTime;
    /**
     * 导入模块
     */
    protected Set<String> imports;
    /**
     * 静态导入模块
     */
    protected Set<String> staticImports;
    /**
     * spring注入bean
     */
    protected Set<String> autowired;
    /**
     * spring-boot版本
     */
    protected MetaProjectFeatureDTO projectFeature;


    public BaseContext(MetaProjectPO project) {
        //所有实体
        this.metaEntities = project.getEntities();
        //所有常量
        this.metaConsts = project.getConsts();
        //包名
        this.packageName = project.getPackageName();
        //通用模块名
        this.commonPackage = project.fetchCommonPackageName();
        //项目标识：驼峰格式-首字母小写
        this.projectName = project.fetchNormalProjectName();
        //项目标识：驼峰格式-首字母大写
        this.projectNameUpper = StringUtils.capitalize(this.projectName);
        //项目标识：短横杠分割
        this.projectNameSplit = project.getProjectName();
        //原始模块名
        this.originProjectName = project.getProjectName();
        //项目名称
        this.projectDesc = project.getProjectDesc();
        //groupId
        this.groupId = project.getGroupId();
        //作者
        this.author = project.getAuthor();
        //创建时间
        this.createdTime = project.getCreatedTime();
        //多对多关联
        this.mtms = project.getMtms();
        //所有图表
        this.charts = project.getCharts();
        //项目特性
        this.projectFeature = FeatureMapper.asProjectFeatureDTO(project.getFeature());
        //初始化java依赖
        this.imports = new TreeSet<>();
        this.staticImports = new TreeSet<>();
        this.autowired = new TreeSet<>();

    }

    /**
     * 设置当前目录
     * 只有第一次设置才有效
     *
     * @param currentDir 当前目录
     */
    public void setCurrentDirOnce(String currentDir) {
        if (this.currentDir == null) {
            this.currentDir = currentDir;
        }
    }

    /**
     * 往当前目录输出额外代码文件
     *
     * @param fileName 文件名
     * @param content 文件内容
     */
    public void writeAdditionalFile(String fileName, String content) {
        File target = new File(currentDir, fileName);
        LOGGER.debug("输出额外代码文件：{}", target.getPath());
        try {
            FileUtils.write(target, content, "UTF-8");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "写文件异常");
        }
    }


    /**
     * 跳过当前文件
     */
    public void skipCurrent() {
        throw new SkipCurrentException();
    }

    /**
     * 添加依赖
     *
     * @param classPath 类全路径
     */
    public void addImport(String classPath) {
        this.imports.add(classPath);
    }

    /**
     * 根据字段类型添加依赖
     *
     * @param field
     */
    public void addFieldTypeImport(MetaFieldPO field) {
        if (Objects.equals(field.getJfieldType(), JFieldType.LOCALDATE.getJavaType())) {
            this.addImport("java.time.LocalDate");
        } else if (Objects.equals(field.getJfieldType(), JFieldType.LOCALDATETIME.getJavaType())) {
            this.addImport("java.time.LocalDateTime");
        } else if (Objects.equals(field.getJfieldType(), JFieldType.DATE.getJavaType())) {
            this.addImport("java.util.Date");
        } else if (Objects.equals(field.getJfieldType(), JFieldType.BIGDECIMAL.getJavaType())) {
            this.addImport("java.math.BigDecimal");
        }
    }

    /**
     * 添加静态依赖
     *
     * @param classPath 类全路径
     */
    public void addStaticImport(String classPath) {
        this.staticImports.add(classPath);
    }

    /**
     * 获取常量类的全路径
     *
     * @param constName
     * @return
     */
    public String getConstFullClassPath(String constName) {
        return this.packageName + ".constant." + constName;
    }

    /**
     * 导入常量依赖
     *
     * @param constName
     */
    public void addConstImport(String constName) {
        this.addImport(this.getConstFullClassPath(constName));
    }


    /**
     * 添加spring bean注入
     *
     * @param packageName 包名
     * @param className   类名
     */
    public void addAutowired(String packageName, String className) {
        this.addImport("org.springframework.beans.factory.annotation.Autowired");
        this.addImport(packageName + "." + className);
        this.autowired.add(className);
    }


    /**
     * 打印import依赖
     *
     * @return
     */
    public String printImport() {
        // 打印外部依赖
        StringBuilder sb1 = new StringBuilder();
        // 打印java内建依赖
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();
        for (String imp : imports) {
            if (imp.startsWith("javax.")) {
                sb2.append("import ").append(imp).append(";\n");
            } else if (imp.startsWith("java.")) {
                sb3.append("import ").append(imp).append(";\n");
            } else {
                sb1.append("import ").append(imp).append(";\n");
            }
        }
        // 打印静态外部依赖
        StringBuilder sb4 = new StringBuilder();
        // 打印静态java内建依赖
        StringBuilder sb5 = new StringBuilder();
        StringBuilder sb6 = new StringBuilder();
        for (String imp : staticImports) {
            if (imp.startsWith("javax.")) {
                sb5.append("import static ").append(imp).append(";\n");
            } else if (imp.startsWith("java.")) {
                sb6.append("import static ").append(imp).append(";\n");
            } else {
                sb4.append("import static ").append(imp).append(";\n");
            }
        }
        StringBuilder sb = new StringBuilder();
        if (sb1.length() > 0) {
            sb.append("\n").append(sb1);
        }
        if (sb2.length() > 0 || sb3.length() > 0) {
            sb.append("\n");
            if (sb2.length() > 0) {
                sb.append(sb2);
            }
            if (sb3.length() > 0) {
                sb.append(sb3);
            }
        }
        if (sb4.length() > 0) {
            sb.append("\n").append(sb4);
        }
        if (sb5.length() > 0 || sb6.length() > 0) {
            sb.append("\n");
            if (sb5.length() > 0) {
                sb.append(sb5);
            }
            if (sb6.length() > 0) {
                sb.append(sb6);
            }
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(0);
        }

        return sb.toString();
    }


    /**
     * 打印依赖注入
     *
     * @return
     */
    public String printAutowired() {
        StringBuilder sb = new StringBuilder();
        for (String aw : autowired) {
            sb.append("    @Autowired\n")
                .append("    private ").append(aw).append(" ")
                .append(SwitchCaseUtil.lowerFirstWord(aw)).append(";\n");
        }
        return sb.toString();
    }


    /**
     * 打印类注释
     *
     * @return
     */
    public String printClassCom(String title) {
        return this.printClassCom(title, "");
    }

    /**
     * 打印类注释
     *
     * @return
     */
    public String printClassCom(String title, String desc) {
        StringBuilder sb = new StringBuilder();
        sb.append("/**\n")
            .append(" * ").append(title).append("\n");
        if (StringUtils.isNotBlank(desc)) {
            sb.append(" * <p>").append(desc).append("\n");
        }
        sb.append(" *\n")
            .append(" * @author ").append(this.author).append("\n")
            .append(" * @date ").append(DateUtil.getDateStr(this.createdTime, "yyyy/MM/dd")).append("\n")
            .append(" */").append("\n");
        return sb.toString();
    }


    public List<MetaEntityPO> getMetaEntities() {
        return metaEntities;
    }

    public void setMetaEntities(List<MetaEntityPO> metaEntities) {
        this.metaEntities = metaEntities;
    }

    public List<MetaConstPO> getMetaConsts() {
        return metaConsts;
    }

    public void setMetaConsts(List<MetaConstPO> metaConsts) {
        this.metaConsts = metaConsts;
    }

    public List<MetaManyToManyPO> getMtms() {
        return mtms;
    }

    public void setMtms(List<MetaManyToManyPO> mtms) {
        this.mtms = mtms;
    }

    public List<MetaChartPO> getCharts() {
        return charts;
    }

    public void setCharts(List<MetaChartPO> charts) {
        this.charts = charts;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getCommonPackage() {
        return commonPackage;
    }

    public void setCommonPackage(String commonPackage) {
        this.commonPackage = commonPackage;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectNameUpper() {
        return projectNameUpper;
    }

    public void setProjectNameUpper(String projectNameUpper) {
        this.projectNameUpper = projectNameUpper;
    }

    public String getProjectNameSplit() {
        return projectNameSplit;
    }

    public void setProjectNameSplit(String projectNameSplit) {
        this.projectNameSplit = projectNameSplit;
    }

    public String getOriginProjectName() {
        return originProjectName;
    }

    public void setOriginProjectName(String originProjectName) {
        this.originProjectName = originProjectName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }

    public Set<String> getImports() {
        return imports;
    }

    public void setImports(Set<String> imports) {
        this.imports = imports;
    }

    public Set<String> getStaticImports() {
        return staticImports;
    }

    public void setStaticImports(Set<String> staticImports) {
        this.staticImports = staticImports;
    }

    public Set<String> getAutowired() {
        return autowired;
    }

    public void setAutowired(Set<String> autowired) {
        this.autowired = autowired;
    }

    public MetaProjectFeatureDTO getProjectFeature() {
        return projectFeature;
    }

    public void setProjectFeature(MetaProjectFeatureDTO projectFeature) {
        this.projectFeature = projectFeature;
    }

    @Override
    public String toString() {
        return "Project:" + projectDesc;
    }
}
