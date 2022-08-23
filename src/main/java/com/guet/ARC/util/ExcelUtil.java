package com.guet.ARC.util;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import org.apache.poi.ss.usermodel.IndexedColors;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class ExcelUtil {
    /**
     * 得到流
     *
     * @param response      响应
     * @param fileName      文件名
     * @param excelTypeEnum excel类型
     * @return
     */
    public static OutputStream getOutputStream(HttpServletResponse response, String fileName,
                                               ExcelTypeEnum excelTypeEnum) {
        try {
            fileName = new String(fileName.getBytes("gb2312"), "ISO8859-1");
            // 设置响应输出的头类型
            if (Objects.equals(".xls", excelTypeEnum.getValue())) {
                //导出xls格式
                response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            } else if (Objects.equals(".xlsx", excelTypeEnum.getValue())) {
                //导出xlsx格式
                response.setContentType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
            }
            // 设置下载文件名称(注意中文乱码)
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName + excelTypeEnum.getValue());
            response.addHeader("Pragma", "No-cache");
            response.addHeader("Cache-Control", "No-cache");
            response.setCharacterEncoding("utf8");
            return response.getOutputStream();
        } catch (IOException e) {
            System.out.println("EasyExcelUtil-->getOutputStream exception:" + e.getMessage());
        }
        return null;
    }

    public static WriteCellStyle buildHeadCellStyle() {
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontName("宋体");
        headWriteFont.setFontHeightInPoints((short) 12);
        headWriteFont.setBold(true);
        headWriteCellStyle.setWriteFont(headWriteFont);
        //自动换行
        headWriteCellStyle.setWrapped(true);
        return headWriteCellStyle;
    }
}
