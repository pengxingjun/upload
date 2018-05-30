<%@page language="java" contentType="text/html; charset=utf-8"%>
<%@include file="/tagDeclare.jsp"%>
<%@include file="/headDeclare.jsp"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<style>
    .elfinder-resize-type label:last-child{
        display: none;
    }
</style>
	<link href="${basePath}plugins/jquery-ui-themes-1.11.4/themes/smoothness/jquery-ui.min.css" rel="stylesheet"/>
	<link href="${basePath}plugins/elfinder/css/theme.css" rel="stylesheet"/>
	<link href="${basePath}plugins/elfinder/css/elfinder.min.css" rel="stylesheet"/>
	<script src="${basePath}plugins/jquery-ui-1.11.4/jquery-ui.min.js"></script>
	<script src="${basePath}plugins/elfinder/js/elfinder.full.js"></script>
	<script src="${basePath}plugins/elfinder/js/elfinder.zh_CN.js"></script>
<script type="text/javascript" charset="utf-8">
    $ (document).ready (function () {
	    $ ('#elfinder').elfinder ({
	        url : 'elfinder/connector',
	        lang : 'zh_CN', // language (OPTIONAL)
	        customData : {"userId" : "1"},
	        allowShortcuts : false,
            validName : /^[\u4e00-\u9fa5_a-zA-Z0-9.]+$/,
            UTCDate : true,
	        uiOptions : {
	            toolbar : [
	                ['mkdir', 'upload'],
	                ['open', 'download'],
	                ['info'],
	                ['quicklook'],
	                ['copy','cut', 'paste'],
	                ['rm'],
	                ['rename', 'resize'],
	                ['search'],
	                ['view', 'sort'],
	                ['fullscreen']
	            ]
	        },
	        contextmenu : {
	    		// navbarfolder menu
	    		navbar : ['open', 'download' ,'|', 'upload', 'mkdir', '|', 'copy', 'cut', 'paste', 'duplicate', '|', 'rm', '|', 'rename', '|', 'archive', '|', 'places', 'info', 'chmod', 'netunmount'],
	    		// current directory menu
	    		cwd    : ['reload' , '|', 'upload', 'mkdir', 'paste', '|', 'view', 'sort', 'colwidth', '|', 'fullscreen'],
	    		// current directory file menu
	    		files  : ['getfile', '|' , 'download', 'opendir', 'quicklook',  '|', 'copy', 'cut', 'paste',  '|', 'rm', '|',  'rename', 'resize','info']
	    	},
	    	handlers : {
	    		dblclick : function(event, elFinder) {
	    			if(elFinder.file(event.data.file).mime !== "directory"){
	    				event.preventDefault();
	    			}
	            }
	        }
	    });
    });
</script>
</head>
<body>
	<div id="elfinder"></div>
</body>
</html>