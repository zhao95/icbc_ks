<div id="wf_track_list_con">
<#assign dataSize=_DATA_?size>
<table width="100%" border="0">
<thead>
  <tr>
    <th>序号</th>
    <th>名称</th>
    <th>送交人</th>
    <th>办理人</th>
    <th>办理部门</th>
  </tr>
</thead>
<tbody>
  <#list _DATA_ as trackInst>
  <tr>
    <td>${dataSize - trackInst_index}</td>
    <td>${trackInst.NODE_NAME}</td>
    <td>${trackInst.TO_DEPT_NAME}</td>
    <td>${trackInst.DONE_USER_NAME}</td>
    <td>${trackInst.DONE_DEPT_NAMES}</td>
  </tr>
  </#list>
</tbody>
</table>
</div>
