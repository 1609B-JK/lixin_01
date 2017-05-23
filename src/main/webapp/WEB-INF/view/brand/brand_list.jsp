<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Insert title here</title>
</head>
<script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
<script src="<%=request.getContextPath()%>/js/bootstrap/js/bootstrap.min.js"></script>
<script src="<%=request.getContextPath() %>/js/bootStrap-addTabs/bootstrap.addtabs.min.js"></script>
<script src="<%=request.getContextPath() %>/js/bootstrap-treeview/dist/bootstrap-treeview.min.js"></script>

<script src="<%=request.getContextPath() %>/js/dateformat/formatDatebox.js"></script>
<script src="<%=request.getContextPath() %>/js/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js"></script>
<script src="<%=request.getContextPath() %>/js/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN.js"></script>

<link rel="stylesheet" href="<%=request.getContextPath()%>/js/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/bootstrap/css/bootstrap-theme.min.css">
<link rel="stylesheet" href="<%=request.getContextPath() %>/js/bootStrap-addTabs/bootstrap.addtabs.css">


<link href="<%=request.getContextPath() %>/js/bootstrap-table/dist/bootstrap-table.min.css" rel="stylesheet">
<link href="<%=request.getContextPath() %>/js/bootstrap-dialog/dist/css/bootstrap-dialog.min.css" rel="stylesheet">
<link href="<%=request.getContextPath() %>/js/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css" rel="stylesheet">
<link type="text/css" rel="stylesheet" href="<%=request.getContextPath() %>/js/bootstrap-fileinput/css/fileinput.css" />
<script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap-table/dist/bootstrap-table.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap-table/dist/locale/bootstrap-table-zh-CN.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap-dialog/dist/js/bootstrap-dialog.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap-fileinput/js/fileinput.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap-fileinput/js/locales/zh.js"></script>
<body>

<!-- 搜索面板 -->
<div class="tab-content" style="padding: 4px">
  <form id="search_book_form">
    <label>名称：</label>
    <input id="query_stu_name" class="form-control-sm" placeholder="请输入学生姓名">
    <label>日期：</label>
    <input id="query_stu_start_date" class="form-control-sm" placeholder="请输入日期">
    ~<input id="query_stu_end_date" class="form-control-sm" placeholder="请输入日期"><br>
    <div align="center">
      <!-- Single button -->
      <div class="btn-group">
        <button type="button" class="btn btn-success glyphicon glyphicon-search" onclick="search_book()">搜索</button>
      </div>
      <div class="btn-group">
        <button type="button" class="btn btn-danger glyphicon glyphicon-repeat" onclick="reset_search_book_form()">重置</button>
      </div>
    </div>
  </form>
</div>


<!-- datagrid -->
<div id="book_tb">
  <!-- Single button -->
  <div class="btn-group">
    <button type="button" class="btn btn-success" onclick="show_add_dialog()">新增</button>
  </div>
  <div class="btn-group">
    <button type="button" class="btn btn-danger" onclick="delete_all_checked_stu()">删除</button>
  </div>
</div>
<table id="book_dg"></table>
<script type="text/javascript">
  //初始化日期控件
  $('#query_stu_start_date').datetimepicker({
    format: 'yyyy-mm-dd',
    autoclose:true,
    minView:2,
    todayBtn:true,
    language:"zh-CN",
  });
  $('#query_stu_end_date').datetimepicker({
    format: 'yyyy-mm-dd',
    autoclose:true,
    minView:2,
    todayBtn:true,
    language:"zh-CN",
  });


  //初始化数据表格
  $('#book_dg').bootstrapTable({
    url:"<%=request.getContextPath() %>/findBrandJson.jhtml",
    dataType:"json",
    //斑马线
    striped:true,
    //设置分页
    pagination:true,
    paginationLoop:true,
    pageNumber:1,
    pageSize:5,
    pageList:[3,5,8,10],
    //设置后台分页
    sidePagination:"server",
    //请求方式
    method:"post",
    //必须的，操你大爷！！！！不然会造成中文乱码
    contentType: "application/x-www-form-urlencoded",
    columns: [{
      checkbox:true
    },{
      field: 'brandID',
      title: '主键'
    }, {
      field: 'brandName',
      title: '品牌名称'
    }, {
      field: 'createDate',
      title: '创建时间'
    }, {
      field: 'updateDate',
      title: '修改时间'
    }
      ,
      {
        field: 'brandImgUrl',
        title: '照片展示',
        formatter:function(value, row, index) {
          alert(value);
          var zc_btn_group = '<img src="<%=request.getContextPath() %>/images/def/01.JPG" width="60" height="28"/>';
          if (null != value) {
            alert(1)
            zc_btn_group = '<img src="ftp://lixin:lixin@127.0.0.1/' + value + '" width="60" height="28"/>';
          }
          return zc_btn_group;
        }
      },
      {
        field: 'cz',
        title: '操作',
        formatter:function(value, row, index) {
          var zc_btn_group = '<div class="btn-group">'
                  + '<button type="button" class="btn btn-xs btn-success" onclick="show_edit_dialog(\'' + row.stuID + '\')">编辑</button>'
                  + '</div>&nbsp;&nbsp;'
                  + '<div class="btn-group">'
                  + '<button type="button" class="btn btn-xs btn-danger" onclick="deleteById(\'' + row.stuID + '\')">删除</button>'
                  + '</div>';
          return zc_btn_group;
        }
      }]
  });
  //新增
  function show_add_dialog() {
    BootstrapDialog.show({
      title:"添加书籍",
      message: $('<div></div>').load('<%=request.getContextPath()%>/add_brand.jsp'),
      buttons: [{
        label: '保存',
        cssClass:"btn btn-success",
        action: function(dialogItself){
          var abrandImgUrl = $("#brandImgUrl").val();
          var abrandName = $("#brandName").val();
          var brandJson = {
            brandImgUrl:abrandImgUrl,
            brandName:abrandName
          }
          $.ajax({
            url:"<%=request.getContextPath() %>/insertBrand.jhtml",
            data:brandJson,
            dataType:"json",
            type:"post",
            success:function(data) {
              //关闭对话框
              dialogItself.close();
              //刷新数据表格
              $("#book_dg").bootstrapTable('refresh');
            }
          });
        }
      }, {
        label: '取消',
        cssClass:"btn btn-danger",
        action: function(dialogItself){
          dialogItself.close();
        }
      }]
    });
  }

  //编辑
  function show_edit_dialog(data_id) {
    BootstrapDialog.show({
      title:"添加书籍",
      message: $('<div></div>').load('updateByID_student.jsp?student.stuID=' + data_id),
      buttons: [{
        label: '修改',
        cssClass:"btn btn-success",
        action: function(dialogItself){

          $.ajax({
            url:"<%=request.getContextPath() %>/student/updateStuByID.action",
            data:$("#update_form_info").serialize(),
            dataType:"json",
            type:"post",
            success:function(data) {
              //关闭对话框
              dialogItself.close();
              //刷新数据表格
              $("#book_dg").bootstrapTable('refresh');
            }
          });
        }
      }, {
        label: '取消',
        cssClass:"btn btn-danger",
        action: function(dialogItself){
          dialogItself.close();
        }
      }]
    });
  }
  //删除
  function deleteById(data){
    $.ajax({
      url:"<%=request.getContextPath() %>/student/deleteByID.action?student.stuID="+data,
      dataType:"json",
      type:"post",
      success:function(data) {
        //刷新数据表格
        $("#book_dg").bootstrapTable('refresh');
      }
    });
  }


  //批量删除
  function delete_all_checked_stu() {
    //获取所有选中的数据
    var checked_stu = $("#book_dg").bootstrapTable("getSelections");
    if (0 < checked_stu.length) {
      BootstrapDialog.show({
        title:"删除数据",
        message:"你确认删除吗",
        buttons: [{
          label: '确定',
          cssClass:"btn btn-success",
          action: function(dialogItself){
            var stu_ids = "";
            //循环这个数组
            for (var i = 0; i < checked_stu.length; i++) {
              stu_ids = stu_ids + "," + checked_stu[i].stuID;
            }
            stu_ids = stu_ids.substring(1);
            //删除操作

            $.ajax({
              url:"<%=request.getContextPath() %>/student/deleteStuByIDs.action?studentIDs=" + stu_ids,
              dataType:"json",
              type:"get",
              success:function(data) {
                //关闭对话框
                dialogItself.close();
                //刷新数据表格
                $("#book_dg").bootstrapTable('refresh');
              }
            });
          }
        }, {
          label: '取消',
          cssClass:"btn btn-danger",
          action: function(dialogItself){
            dialogItself.close();
          }
        }]
      });
    } else {
      BootstrapDialog.show({
        title:"警告",
        message:"至少选择一条",
        buttons: [{
          label: '确定',
          cssClass:"btn btn-success",
          action: function(dialogItself){
            dialogItself.close();
          }
        }]
      });
    }
  }





  //搜索功能
  function search_book() {
    var query_stu_name = $("#query_stu_name").val();
    var query_stu_type = $("#query_stu_type").val();
    var query_stu_start_date = $("#query_stu_start_date").val();
    var query_stu_end_date = $("#query_stu_end_date").val();

    var query_stu_parame = {
      query:{
        "student.stuName":query_stu_name,
        "student.address":query_stu_type,
        "student.startDate":query_stu_start_date,
        "student.endDate":query_stu_end_date,
      }
    }
    //刷新数据表格
    $("#book_dg").bootstrapTable('refresh', query_stu_parame);
  }

  //重置搜索表单
  function reset_search_book_form() {
    $("#search_book_form")[0].reset();
    search_book();
  }
</script>

</body>
</html>