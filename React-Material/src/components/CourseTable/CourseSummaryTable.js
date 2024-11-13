import React, { useEffect, useState } from 'react';
import axiosInstance from '../AxiosInstance';
import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper
} from '@mui/material';

function CourseSummaryTable() {
    const [tableData, setTableData] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
              const currentDate = new Date(); // 当前日期
              const initialStartDate = new Date(2024, 9, 26); // 2024年10月26日
              const daysPerRange = 14; // 每个时间段的天数
          
              const differenceInTime = currentDate - initialStartDate; // 时间差（毫秒）
              const differenceInDays = Math.ceil(differenceInTime / (1000 * 60 * 60 * 24)); // 转换为天数
              
              // 如果 differenceInDays 正好是 daysPerRange 的倍数，则不进位
              const cycleIndex = (differenceInDays % daysPerRange === 0)
                  ? Math.floor(differenceInDays / daysPerRange) - 1
                  : Math.floor(differenceInDays / daysPerRange);
          
              const endcycledate = new Date(initialStartDate);
              endcycledate.setDate(endcycledate.getDate() + (cycleIndex + 1) * daysPerRange - 1);
              
              const cycledate = endcycledate.toISOString().split('T')[0];
              //console.log(cycledate); // 输出 cycledate
              
              const response = await axiosInstance.get(`/course/coursesummaytable?date=${cycledate}`);
              setTableData(response.data);
            } catch (error) {
                console.error("Error fetching data:", error);
            }
        };

        fetchData();
    }, []);

    return (
        <TableContainer component={Paper}>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>培训点编码</TableCell>
                        <TableCell>地址</TableCell>
                        <TableCell>课程名称</TableCell>
                        <TableCell>时区</TableCell>
                        <TableCell>AllCPR售价</TableCell>
                        <TableCell>开课日期计划</TableCell>
                        <TableCell>计划次数</TableCell>
                        <TableCell>备注</TableCell>
                        <TableCell>负责人</TableCell>
                        <TableCell>实际开课计划</TableCell>
                        <TableCell>实际广告次数</TableCell>
                        <TableCell>备注</TableCell>
                        <TableCell>发布人</TableCell>
                        <TableCell>场地状态</TableCell>

                        {/* 根据需要添加更多表头 */}
                    </TableRow>
                </TableHead>
                <TableBody>
  {Array.isArray(tableData) ? (
    tableData.map((row, index) => (
      <TableRow key={index}>
        <TableCell>{row["培训点编码"]}</TableCell>
        <TableCell>{row["地址"]}</TableCell>
        <TableCell>{row["课程名称"]}</TableCell>
        <TableCell>{row["时区"]}</TableCell>
        <TableCell>{row["AllCPR售价"]}</TableCell>
        <TableCell>{row["开课日期计划"]}</TableCell>
        <TableCell>{row["计划次数"]}</TableCell>
        <TableCell>{row["备注"]}</TableCell>
        <TableCell>{row["负责人"]}</TableCell>
        <TableCell>{row["实际开课日期"]}</TableCell>
        <TableCell>{row["实际广告次数"]}</TableCell>
        <TableCell>{row["补打广告次数"]}</TableCell>
        <TableCell>{row["发布人"]}</TableCell>
        <TableCell>{row["场地状态"]}</TableCell>
        {/* 根据 row 数据添加更多单元格 */}
      </TableRow>
    ))
  ) : (
    <TableRow>
      <TableCell colSpan={15} style={{ textAlign: "center" }}>
        暂无数据
      </TableCell>
    </TableRow>
  )}
</TableBody>
            </Table>
        </TableContainer>
    );
}

export default CourseSummaryTable;