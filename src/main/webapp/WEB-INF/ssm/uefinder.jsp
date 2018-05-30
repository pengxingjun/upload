<%@page language="java" contentType="text/html; charset=utf-8"%>
<%@include file="/tagDeclare.jsp"%>
<%@include file="/headDeclare.jsp"%>
<!DOCTYPE html>
<html lang="zh">
<head>
    <link href="${basePath}plugins/jquery-ui-themes-1.11.4/themes/smoothness/jquery-ui.min.css" rel="stylesheet"/>
    <link href="${basePath}plugins/elfinder/css/theme.css" rel="stylesheet"/>
    <link href="${basePath}plugins/elfinder/css/elfinder.min.css" rel="stylesheet"/>
    <script src="${basePath}plugins/jquery-ui-1.11.4/jquery-ui.min.js"></script>
    <script src="${basePath}plugins/elfinder/js/elfinder.full.js"></script>
    <script src="${basePath}plugins/elfinder/js/elfinder.zh_CN.js"></script>
    <script type="text/javascript" src="<%=basePath%>plugins/ueditor/dialogs/internal.js"></script>
    <script type="text/javascript" charset="utf-8">
        var fileArray = [];

        $ ().ready (function () {
            var mime = '${mime}';
            var myArray=new Array();
            if(mime!='' && mime!=null && mime!=undefined){
                myArray[0] = mime;
            }
            var elf = $ ('#elfinder').elfinder ({
                url : '${fileServer}elfinder/connector',
                lang : 'zh_CN',
                customData : {"userId" : ${userId}},
                onlyMimes: myArray,
                allowShortcuts : false,
                height : document.body.scrollHeight - 20,
                resizable : false,
                validName : /^[\u4e00-\u9fa5_a-zA-Z0-9.]+$/,
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
                        ['view', 'sort']
                    ]
                },
                contextmenu : {
                    // navbarfolder menu
                    navbar : ['open', 'download', '|', 'upload', 'mkdir', '|', 'copy', 'cut', 'paste', 'duplicate', '|', 'rm', '|', 'rename', '|', 'archive', '|', 'places', 'info', 'chmod', 'netunmount'],
                    // current directory menu
                    cwd    : ['reload' , '|', 'upload', 'mkdir', 'paste', '|', 'view', 'sort', 'colwidth',],
                    // current directory file menu
                    files  : ['getfile', '|' , 'download', 'opendir', 'quicklook',  '|', 'copy', 'cut', 'paste',  '|', 'rm', '|',  'rename', 'resize','info']
                },
                handlers : {
                    dblclick : function(event, elFinder) {
                        if(elFinder.file(event.data.file).mime !== "directory"){
                            event.preventDefault();
                        }
                    },
                    select : function(event, instance) {
                        fileArray = [];
                        var selected = event.data.selected;
                        for(var i in selected){
                            var url = instance.url(selected[i]);
                            var file = instance.file(selected[i]);
                            fileArray[i]={
                                name:file.name,
                                mime:file.mime,
                                url:url
                            };
                        }
                    }
                }
            }).elfinder ('instance');

            dialog.onok = function (){
                for(var i in fileArray){
                    if(fileArray[i].mime.indexOf("image") == 0){
                        editor.execCommand('inserthtml', '<img src="' + fileArray[i].url + '" alt="' + fileArray[i].name + '"/>');
                    }else if (fileArray[i].mime.indexOf("video") == 0) {
                        editor.execCommand('inserthtml', "<iframe width='100%' src='" + fileArray[i].url
                            + "' frameborder=0 'allowfullscreen' onload='javascript:$(this).height(document.body.clientWidth*9/16)'></iframe>");
                    }
                    editor.execCommand('inserthtml', '<br />');
                }
                dialog.close();
            };

            dialog.oncancel = function () {
                //editor.execCommand('inserthtml', '<span>html code</span>');
            };
        });



    </script>
</head>
<body>

<!-- Element where elFinder will be created (REQUIRED) -->
<div id="elfinder"></div>

</body>
</html>