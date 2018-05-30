<%@page language="java" contentType="text/html; charset=utf-8"%>
<%@include file="/tagDeclare.jsp"%>
<%@include file="/headDeclare.jsp"%>
<!DOCTYPE html>
<html lang="zh">
<head>
    <link href="${basePath}plugins/plupload/jquery.plupload.queue/css/jquery.plupload.queue.css" rel="stylesheet"/>
<style type="text/css">
.mybtn {
	float:right;
    text-align:center;
    line-height:100%;
    padding-top:0.5em;
    padding-bottom:0.55em;
    font-family:Arial,sans-serif;
    font-size:10px;
    font-style:normal;
    font-variant:normal;
    font-weight:normal;
    text-decoration:none;
    margin-top:0px;
    margin-right:8px;
    margin-bottom:0px;
    margin-left:2px;
    vertical-align:text-bottom;
    display:inline-block;
    cursor:pointer;
    zoom:1;
    outline-width:medium;
    outline-color:invert;
    font-size-adjust:none;
    font-stretch:normal;
    border-top-left-radius:0.5em;
    border-top-right-radius:0.5em;
    border-bottom-left-radius:0.5em;
    border-bottom-right-radius:0.5em;
    box-shadow:0px 1px 2px rgba(0,0,0,0.2);
    color:#fefee9;
    border-top-color:#da7c0c;
    border-right-color:#da7c0c;
    border-bottom-color:#da7c0c;
    border-left-color:#da7c0c;
    border-top-width:1px;
    border-right-width:1px;
    border-bottom-width:1px;
    border-left-width:1px;
    border-top-style:solid;
    border-right-style:solid;
    border-bottom-style:solid;
    border-left-style:solid;
    background-image:none;
    background-attachment:scroll;
    background-repeat:repeat;
    background-position-x:0%;
    background-position-y:0%;
    background-size:auto;
    background-origin:padding-box;
    background-clip:padding-box;
    background-color:#f78d1d;
}
</style>
<script src="${basePath}plugins/plupload/plupload.full.min.js"></script>
<script src="${basePath}plugins/plupload/jquery.plupload.queue/jquery.plupload.queue.min.js"></script>
<script src="${basePath}plugins/plupload/i18n/zh_CN.js"></script>
<script type="text/javascript">
	$ (function () {
	    var uploader = $ ("#uploader").pluploadQueue ({
	        runtimes : 'html5,flash,silverlight,html4',
	        url : "${basePath}pluploadUpload/upload",
	        max_file_size : '1000mb',
	        chunk_size : '1mb',
			resize : {
	            width : 200,
	            height : 200,
	            quality : 90,
	            crop : true
	        },
	        filters : [ {
	            title : "Image files",
	            extensions : "jpg,gif,png"
	        }, {
	            title : "Vedio files",
	            extensions : "mp4,mkv"
	        }, {
	            title : "Zip files",
	            extensions : "zip,avi"
	        } ],
	        rename : true,
	        sortable : true,
	        dragdrop : true,
	        views : {
	            list : true,
	            thumbs : true,
	            active : 'thumbs'
	        },
	        browse_button : 'selectFile',
	        flash_swf_url : '${basePath}plugins/plupload/Moxie.swf',
	        silverlight_xap_url : '${basePath}plugins/plupload/Moxie.xap'
	    });
	    $ ("#toStop").on ('click', function () {
		    uploader.stop ();
	    });
	    $ ("#toStart").on ('click', function () {
		    uploader.start ();
	    });
    });
</script>
</head>
<body>
	<div id="uploader">
		<p>Your browser doesn't have Flash, Silverlight or HTML5 support.</p>
	</div>
	<button id="toStart" class="mybtn">再次开始</button>
	<button id="toStop" class="mybtn">暂停一下</button>
	<button id="selectFile" class="mybtn">选择文件</button>
</body>
</html>