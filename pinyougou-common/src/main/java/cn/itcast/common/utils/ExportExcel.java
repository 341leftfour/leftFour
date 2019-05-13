package cn.itcast.common.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能: [POI实现把数据库数据导出到Excel]
 * 作者: JML
 * 版本: 1.0
 */
public class ExportExcel {

    /**
     * 功能: 导出为Excel工作簿
     * 参数: sheetName[工作簿中的一张工作表的名称]
     * 参数: titleName[表格的标题名称]
     * 参数: headers[表格每一列的列名]
     * 参数: dataSet[要导出的数据源]
     * 参数: resultUrl[导出的excel文件地址]
     * 参数: pattern[时间类型数据的格式]
     */
    public static void exportExcel(String sheetName,String titleName,String[] headers,Collection<?> dataSet,String resultUrl,String pattern) {

        doExportExcel(sheetName,titleName,headers,dataSet,resultUrl,pattern);

    }

    /**
     * 功能:真正实现导出
     */
    private static void doExportExcel(String sheetName,String titleName,String[] headers,Collection<?> dataSet,String resultUrl,String pattern) {

        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();

        // 生成一个工作表
        HSSFSheet sheet = workbook.createSheet(sheetName);
        // 设置工作表默认列宽度为20个字节
        sheet.setDefaultColumnWidth((short) 20);
        //在工作表中合并首行并居中
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,headers.length-1));

        //创建标题行-增加样式-赋值
        HSSFRow titleRow = sheet.createRow(0);
        HSSFCell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(titleName);

        // 创建列首-增加样式-赋值
        HSSFRow row = sheet.createRow(1);
        for (short i = 0; i < headers.length; i++) {

            @SuppressWarnings("deprecation")
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);

        }

        // 创建表中数据行-增加样式-赋值
        Iterator<?> it = dataSet.iterator();
        int index = 1;
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            Object t = it.next();
            // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            Field[] fields = t.getClass().getDeclaredFields();
            for (short i = 0; i < fields.length; i++) {
                @SuppressWarnings("deprecation")
                HSSFCell cell = row.createCell(i);
                Field field = fields[i];
                String fieldName = field.getName();
                //序列号不需要写入表格,所以抛弃
                if (fieldName == "serialVersionUID"){
                    continue;
                }
                String getMethodName = "get"+ fieldName.substring(0, 1).toUpperCase()+ fieldName.substring(1);
                try {
                    @SuppressWarnings("rawtypes")
                    Class tCls = t.getClass();
                    @SuppressWarnings("unchecked")
                    Method getMethod = tCls.getMethod(getMethodName,new Class[] {});
                    Object value = getMethod.invoke(t, new Object[] {});

                    // 如果是时间类型,按照格式转换
                    String textValue = null;
                    if (value instanceof Date) {
                        Date date = (Date) value;
                        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                        textValue = sdf.format(date);
                    } else {
                        // 其它数据类型都当作字符串简单处理
                        textValue =  value==null?"空":value.toString();
                    }

                    // 利用正则表达式判断textValue是否全部由数字组成
                    if (textValue != null) {
                        Pattern p = Pattern.compile("^\\d+(\\.\\d+)?$");
                        Matcher matcher = p.matcher(textValue);
                        if (matcher.matches()) {
                            // 是数字当作double处理
                            cell.setCellValue(Double.parseDouble(textValue));
                        } else {
                            // 不是数字做普通处理
                            cell.setCellValue(textValue);
                        }
                    }

                    OutputStream out=null;
                    try {
                        out = new FileOutputStream(resultUrl);
                        workbook.write(out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally{
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (SecurityException e) {
                    e.printStackTrace();
                    throw new RuntimeException("没有权限异常");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    throw new RuntimeException("未找到方法异常");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    throw new RuntimeException("参数异常");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RuntimeException("反射异常");
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } finally {
                    //清理资源
                    try {
                        workbook.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}