<%@include file="/tagDeclare.jsp"%>
<%@include file="/headDeclare.jsp"%>
<!-- body start -->

<!-- view-message -->
<div class="my-dialog" id="dg-message">
    <input hidden id="saveId" name="saveId"  >
    <!-- checkManage-message -->
    <form class="layui-form editMemberMsg" id="member-form">

        <script type="text/html" id="tpl-message">
            <%--<div class="overflow-y-auto" style="max-height: 360px; min-height: 360px;">--%>
            <div id="viewWrap">
                <table class="memberEdit" style="max-width: 1140px;">
                    <tr>
                        <th>内容：</th>
                        <td colspan="3" class="editViewer" data-show="reader">
                            <div class="td-text" data-view="reader">{{#content}}</div>
                            <div class="layui-input-inline" data-view="editor" style="width: 360px; " id="editorArea">
                                <textarea name="content" placeholder="请输入内容" class="layui-textarea"
                                          style="resize: none;">{{#content}}</textarea>
                            </div>
                        </td>
                    </tr>
                </table>
                <div style="width:100%; padding: 10px 0;"></div>
            </div>
        </script>
    </form>
    <!-- /checkManage-message -->
</div>
<!-- body end -->
<script type="text/plain" id="EditorView" style="width:850px; height:550px;"></script>
<script type="text/javascript" src="<%=basePath%>plugins/ueditor/ueditor.config.js"></script>
<script type="text/javascript" src="<%=basePath%>plugins/ueditor/editor_api.js?t=5"></script>
<script type="text/javascript" src="<%=basePath%>plugins/ueditor/custom/elfinder.js"></script>
<script>
    var _basePath = '<%=basePath%>';
    $(function () {
        showEditor();
    })
    
    function showEditor() {
        _editor = UE.getEditor('EditorView', {
            serverUrl: "/",
            autoFloatEnabled : false,
            theme: "default", //皮肤
            lang: 'zh-cn' //语言
        });

        var _editorArea = $('#editorArea'), _editorView = $('#EditorView');
        _editorArea.html('');
        _editorView.appendTo(_editorArea);
    }
</script>