package com.simonalong.mikilin.log;

import com.alibaba.fastjson.JSON;
import com.simonalong.mikilin.util.ExcelUtil;
import com.simonalong.mikilin.util.FileUtil;
import com.simonalong.mikilin.util.LocalDateTimeUtil;
import com.simonalong.neo.NeoMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shizi
 * @since 2021-04-14 09:37:41
 */
public class LogTest {



    @Test
    public void test21() {

        HashMap<String, String> fieldMap = new HashMap<>();
        fieldMap.put("name","测试");

        List<DataEntity> dataList = new ArrayList<>();
        dataList.add(new DataEntity("数据1"));
        dataList.add(new DataEntity("数据2"));
        dataList.add(new DataEntity("数据3"));
        ExcelUtil.export("分页1", dataList, fieldMap);
    }

    @Data
    @AllArgsConstructor
    private class DataEntity {
        private String name;
    }

    @Test
    @SneakyThrows
    public void test() {
        Map<String, List> contentMap = new HashMap<>();
        String[] txts = FileUtil.readFromResource(LogTest.class, "/log/text.txt").split("\n");
        for (int i = 0; i < txts.length; i++) {
            NeoMap dataMap = getDataMap(txts[i], i);
            if (!NeoMap.isEmpty(dataMap)) {
                String deviceCode = dataMap.getString("deviceCode");
                List<NeoMap> metricList = dataMap.getList(NeoMap.class, "metaList");
                for (NeoMap metricMap : metricList) {
                    show(change(metricMap.getInteger("type")));
                    int typeIndex = Integer.parseInt(metricMap.getString("type"));

                    String dataJsonStr = metricMap.getString("data");

                    print(typeIndex, dataJsonStr);

                    if(typeIndex == 2) {
                        contentMap.compute(change(typeIndex), (k, v)->{
                            if(null == v) {
                                List<AuthEntity> innerList = new ArrayList<>();
                                if(dataJsonStr.startsWith("[")) {
                                    innerList.addAll(JSON.parseArray(dataJsonStr, AuthEntity.class));
                                } else {
                                    innerList.add(JSON.parseObject(dataJsonStr, AuthEntity.class));
                                }

                                return innerList;
                            } else {
                                if(dataJsonStr.startsWith("[")) {
                                    v.addAll(JSON.parseArray(dataJsonStr, AuthEntity.class));
                                } else {
                                    v.add(JSON.parseObject(dataJsonStr, AuthEntity.class));
                                }
                                return v;
                            }
                        });
                    }

                    // os总况
                    if(typeIndex == 1001) {
                        List<OsReviewEntity> authList = new ArrayList<>();
                        contentMap.compute(change(typeIndex), (k, v)->{
                            if(null == v) {
                                List<OsReviewEntity> innerList = new ArrayList<>();
                                if(dataJsonStr.startsWith("[")) {
                                    innerList.addAll(JSON.parseArray(dataJsonStr, OsReviewEntity.class).stream().peek(e->e.setDeviceCode(deviceCode)).collect(Collectors.toList()));
                                } else {
                                    OsReviewEntity osReviewEntity = JSON.parseObject(dataJsonStr, OsReviewEntity.class);
                                    osReviewEntity.setDeviceCode(deviceCode);
                                    innerList.add(osReviewEntity);
                                }

                                return innerList;
                            } else {
                                if(dataJsonStr.startsWith("[")) {
                                    v.addAll(JSON.parseArray(dataJsonStr, OsReviewEntity.class).stream().peek(e->e.setDeviceCode(deviceCode)).collect(Collectors.toList()));
                                } else {
                                    OsReviewEntity osReviewEntity = JSON.parseObject(dataJsonStr, OsReviewEntity.class);
                                    osReviewEntity.setDeviceCode(deviceCode);
                                    v.add(osReviewEntity);
                                }
                                return v;
                            }
                        });
                    }

                    // OS异常接口
                    if(typeIndex == 1002) {
                        List<OsExceptionEntity> authList = new ArrayList<>();
                        contentMap.compute(change(typeIndex), (k, v)->{
                            if(null == v) {
                                List<OsExceptionEntity> innerList = new ArrayList<>();
                                if(dataJsonStr.startsWith("[")) {
                                    innerList.addAll(JSON.parseArray(dataJsonStr, OsExceptionEntity.class).stream().peek(e->e.setDeviceCode(deviceCode)).collect(Collectors.toList()));
                                } else {
                                    OsExceptionEntity osReviewEntity = JSON.parseObject(dataJsonStr, OsExceptionEntity.class);
                                    osReviewEntity.setDeviceCode(deviceCode);
                                    innerList.add(osReviewEntity);
                                }

                                return innerList;
                            } else {
                                if(dataJsonStr.startsWith("[")) {
                                    v.addAll(JSON.parseArray(dataJsonStr, OsExceptionEntity.class).stream().peek(e->e.setDeviceCode(deviceCode)).collect(Collectors.toList()));
                                } else {
                                    OsExceptionEntity osReviewEntity = JSON.parseObject(dataJsonStr, OsExceptionEntity.class);
                                    osReviewEntity.setDeviceCode(deviceCode);
                                    v.add(osReviewEntity);
                                }
                                return v;
                            }
                        });
                    }

                    // OS异常接口
                    if(typeIndex == 1003) {
                        List<OsHealthyEntity> authList = new ArrayList<>();
                        contentMap.compute(change(typeIndex), (k, v)->{
                            if(null == v) {
                                List<OsHealthyEntity> innerList = new ArrayList<>();
                                if(dataJsonStr.startsWith("[")) {
                                    innerList.addAll(JSON.parseArray(dataJsonStr, OsHealthyEntity.class).stream().peek(e->e.setDeviceCode(deviceCode)).collect(Collectors.toList()));
                                } else {
                                    OsHealthyEntity osReviewEntity = JSON.parseObject(dataJsonStr, OsHealthyEntity.class);
                                    osReviewEntity.setDeviceCode(deviceCode);
                                    innerList.add(osReviewEntity);
                                }

                                return innerList;
                            } else {
                                if(dataJsonStr.startsWith("[")) {
                                    v.addAll(JSON.parseArray(dataJsonStr, OsHealthyEntity.class).stream().peek(e->e.setDeviceCode(deviceCode)).collect(Collectors.toList()));
                                } else {
                                    OsHealthyEntity osReviewEntity = JSON.parseObject(dataJsonStr, OsHealthyEntity.class);
                                    osReviewEntity.setDeviceCode(deviceCode);
                                    v.add(osReviewEntity);
                                }
                                return v;
                            }
                        });
                    }
                }
            }
        }

        List<AuthEntity> authEntityList = contentMap.get(change(2));
        LinkedHashMap<String, String> fieldMap = new LinkedHashMap<>();
        fieldMap.put("deviceCode", "操作系统code");
        fieldMap.put("os", "os系统版本");
        fieldMap.put("availableDays", "可用天数");
        export(change(2), authEntityList, fieldMap);

        fieldMap.clear();

        List<OsReviewEntity> osReviewEntityList = contentMap.get(change(1001));
        fieldMap.put("deviceCode", "操作系统code");
        fieldMap.put("exception", "异常个数");
        fieldMap.put("success", "成功个数");
        fieldMap.put("namespace", "命名空间");
        fieldMap.put("avgRt", "平均响应时长");
        export(change(1001), osReviewEntityList, fieldMap);
        fieldMap.clear();

        List<OsExceptionEntity> osExceptionEntityList = contentMap.get(change(1002));
        fieldMap.put("deviceCode", "操作系统code");
        fieldMap.put("exception", "异常个数");
        fieldMap.put("minRt", "成功个数");
        fieldMap.put("appName", "命名空间");
        fieldMap.put("success", "平均响应时长");
        fieldMap.put("avgRt", "平均响应时长");
        fieldMap.put("resourceName", "平均响应时长");
        fieldMap.put("maxRt", "平均响应时长");
        export(change(1002), osExceptionEntityList, fieldMap);
        fieldMap.clear();

        List<OsHealthyEntity> osHealthyEntityList = contentMap.get(change(1003));
        List<OsHealthyFinalEntity> osHealthyFinalEntityList = osHealthyEntityList.stream().flatMap(e-> {
            return JSON.parseArray(e.getDetail(), OsHealthyDetailEntity.class).stream().map(inner-> {
                OsHealthyFinalEntity osHealthyFinalEntity = new OsHealthyFinalEntity();
                osHealthyFinalEntity.setDeviceCode(e.getDeviceCode());
                osHealthyFinalEntity.setScore(e.getScore());
                osHealthyFinalEntity.setTimestamp(LocalDateTimeUtil.longToString(e.getTimestamp()));
                osHealthyFinalEntity.setName(inner.getName());
                osHealthyFinalEntity.setPercent(inner.getPercent());
                osHealthyFinalEntity.setScoreInner(inner.getScore());
                return osHealthyFinalEntity;
            });
        }).collect(Collectors.toList());
        fieldMap.put("deviceCode", "操作系统code");
        fieldMap.put("timestamp", "时间");
        fieldMap.put("score", "总分");
        fieldMap.put("name", "子项");
        fieldMap.put("percent", "子项百分比");
        fieldMap.put("scoreInner", "子项评分");
        export(change(1003), osHealthyFinalEntityList, fieldMap);
        fieldMap.clear();
    }

    private void export(String sheetName, List dataList, Map<String, String> fieldMap) {
        ExcelUtil.export(sheetName, dataList, fieldMap);
    }

    private void print(Integer typeIndex, String dataJsonStr) {
        if (typeIndex == 1) {
            show("版本");
            doPrint(typeIndex, dataJsonStr);
        } else if (typeIndex == 2) {
            show("授权");
            // {"os":"iSysCore OS v2.6.1.20210406.beta","deviceCode":"82ecd3732bed10685632f45560127ecb","availableDays":"13"}
            doPrint(typeIndex, dataJsonStr);
        } else if (typeIndex == 3) {
            show("服务器信息");
            doPrint(typeIndex, dataJsonStr);
        } else if (typeIndex == 4) {
            show("三方注册信息");
            doPrint(typeIndex, dataJsonStr);
        } else if (typeIndex == 1001) {
            show("OS总况");
            // {"exception":1.0,"success":63647.0,"namespace":"isc-os","avgRt":20.38}
            doPrint(typeIndex, dataJsonStr);
        } else if (typeIndex == 1002) {
            show("OS异常接口");
            doPrint(typeIndex, dataJsonStr);
        } else if (typeIndex == 1003) {
            show("OS健康打分");
            doPrint(typeIndex, dataJsonStr);
        } else if (typeIndex == 2001) {
            show("OS核心业务");
            doPrint(typeIndex, dataJsonStr);
        }
    }

    private void doPrint(Integer type, String dataJsonStr) {
        if (type != 1003) {
            return;
        }

        if(dataJsonStr.startsWith("[")) {
            show(JSON.toJSONString(JSON.parseArray(dataJsonStr, NeoMap.class)));
        } else {
            show(NeoMap.fromFastJsonStr(dataJsonStr).toFastJsonString());
        }
    }

    private void versionShow(NeoMap dataMap) {
        if (NeoMap.isEmpty(dataMap)) {
            show("数据为空");
            return;
        }
        show("版本：" + dataMap.toFastJsonString());
    }

    private void authShow(NeoMap dataMap) {
        if (NeoMap.isEmpty(dataMap)) {
            show("数据为空");
            return;
        }
        show("授权：" + dataMap.toFastJsonString());
    }

    private void serverInfoShow(NeoMap dataMap) {
        if (NeoMap.isEmpty(dataMap)) {
            show("数据为空");
            return;
        }
        show("服务器信息：" + dataMap.toFastJsonString());
    }

    private void thirdRegisterShow(NeoMap dataMap) {
        if (NeoMap.isEmpty(dataMap)) {
            show("数据为空");
            return;
        }
        show("三方注册：" + dataMap.toFastJsonString());
    }

    private void osReviewShow(NeoMap dataMap) {
        if (NeoMap.isEmpty(dataMap)) {
            show("数据为空");
            return;
        }
        show("OS总况：" + dataMap.toFastJsonString());
    }

    private void osExceptionShow(Object object) {
        if(object instanceof NeoMap) {
            if (NeoMap.isEmpty((NeoMap) object)) {
                show("数据为空");
                return;
            }
            NeoMap dataMap = (NeoMap) object;
            show("OS异常接口：" + dataMap.toFastJsonString());
        } else if(object instanceof List) {
            List dataMapList = (List) object;
            for (Object obje : dataMapList) {
                show(JSON.toJSONString(obje));
            }
        }

    }

    private void osHealthyShow(NeoMap dataMap) {
        if (NeoMap.isEmpty(dataMap)) {
            show("数据为空");
            return;
        }
        show("OS健康打分：" + dataMap.toFastJsonString());
    }

    private void osBizShow(NeoMap dataMap) {
        if (NeoMap.isEmpty(dataMap)) {
            show("数据为空");
            return;
        }
        show("OS核心业务：" + dataMap.toFastJsonString());
    }


    private NeoMap getDataMap(String line, int i) {
        tab("第" + (i + 1) + "行：*********");
        if (line.startsWith("========")) {
            show("===============当前数据太大未获取到出来==============================");
            return null;
        } else {
            int index = line.indexOf("[cockpit-upload]: data = ");
            show(index);
            NeoMap dataMap = NeoMap.fromFastJsonStr(line.substring(index + "[cockpit-upload]: data = ".length()));
            return dataMap;
        }
    }

    private String change(Integer type) {
        switch (type) {
            case 1:
                return "版本"; // 这个现在没有
            case 2:
                return "授权";
            case 3:
                return "服务器信息";    // 这个现在没有
            case 4:
                return "三方注册信息";  // 这个现在没有
            case 1001:
                return "OS总况";
            case 1002:
                return "OS异常接口";
            case 1003:
                return "OS健康打分";
            case 2001:
                return "OS核心业务"; // 这个现在没有
            default:
                return null;
        }
    }

    public void tab(Object... objects) {
        Arrays.asList(objects).stream().forEach(object -> {
            show("********* " + object);
        });
    }

    public void show(Object... objects) {
        Optional.ofNullable(objects).ifPresent(objects1 -> Arrays.stream(objects1).forEach(System.out::println));
    }
}
