<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<body>
	<form id="stu_form_info">
	<input type="hidden"  id="brandImgUrl">
	<table border="1px" cellpadding="0" cellspacing="0" align="center">
		<tr>
			<td>品牌</td>
			<td><input type="text" id="brandName" class="form-control" placeholder="请输入品牌名称"></td>
		</tr>
		<tr>
			<td>上传</td>
			<td><input type="file" name="brandMainImage" class="projectfile" multiple /></td>
		</tr>
	</table>
	</form>
<script type="text/javascript">
	
	//初始化日期控件
	$('#add_date').datetimepicker({
	    format: 'yyyy-mm-dd',
	    autoclose:true,
	    minView:2,
	    todayBtn:true,
	    language:"zh-CN",
	});
	
	//fileinput组件的属性
	projectfileoptions = {
			//上传按钮
	    	showUpload:true,
	    	//移除按钮
		    showRemove : true,
		    //最大上传文件的个数，想要多选文件，input文件域必须加上multiple这个属性
		    maxFileCount:2,
		    //按钮尺寸
		    //mainClass: "input-group-lg",
		    browseClass: "btn btn-primary btn-block",
		    //文件上传的路径，设置可实现拖拽
		    uploadUrl:'<%=request.getContextPath() %>/uploadBrandMainImage.jhtml',
		    //语言设置：中文
		    language : 'zh',
		    //允许预览的文件类型
		    allowedPreviewTypes : [ 'image', 'text' ],
		    //允许选择的文件后缀名
		    allowedFileExtensions : [ 'jpg', 'png', 'gif', 'bmp', 'txt' ],
		    //最大上传文件的大小（KB）
		    maxFileSize : 2000,
		    //是否显示路径文本框
		    showCaption:false,
		    //是否显示预览
		    showPreview:true,
		    //是否显示关闭预览框
		    showClose:false,
		    //是否显示文件选择按钮
		    showBrowse:true,
	};

	//页面加载函数
	$(function() {
		//文件上传框
		$('input[class=projectfile]').each(function() {
			var imageurl = $(this).attr("value");
			if (imageurl) {
			    var op = $.extend({
			        initialPreview : [ // 预览图片的设置
			        "<img src='" + imageurl + "' class='file-preview-image'>", ]
			    }, projectfileoptions);
			    $(this).fileinput(op);
			} else {
			    $(this).fileinput(projectfileoptions);
			}
		});
		
		//文件上传之前
		$('input[class=projectfile]').each(function() {
			$(this).on('filepreupload', function(event, data, previewId, index) {
			    //alert("要上传了，啊啊啊啊");
			});
		});
		
		//文件上传之后
		$('input[class=projectfile]').each(function() {
			$(this).on('fileuploaded', function(event, data, previewId, index) {
			    var form = data.form, files = data.files, extra = data.extra,
			        response = data.response, reader = data.reader;
				alert(response.brandImgUrl)
			    $("#brandImgUrl").val(response.brandImgUrl);
			    //alert(index + "上传完毕" + previewId);
			    //根据每一个预览图片的下标，为每一个预览图片赋予id属性
			    //$("img[class=file-preview-image]")[index].attr("id", response.attachID);
			});
		});
	})
</script>
</body>
</html>