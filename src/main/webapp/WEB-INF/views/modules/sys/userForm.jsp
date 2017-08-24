<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/imageupload.jsp" %>
	<script type="text/javascript">
		var validateForm;
		function doSubmit(){//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		  if(validateForm.form()){
			  $("#inputForm").submit();
			  return true;
		  }
		  return false;
		}

		$(document).ready(function() {
			//设置了远程验证，在初始化时必须预先调用一次。
			var $inputForm = $("#inputForm");
			validateForm = $inputForm.validate({
				rules: {
					loginName: {remote: "${ctx}/sys/user/checkUsername?oldLoginName=" + encodeURIComponent('${user.loginName}')}
				},
				messages: {
					loginName: {remote: "用户名已存在"},
					confirmPassword: {equalTo: "两次输入密码不一致"}
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});

			//在ready函数中预先调用一次远程校验函数，是一个无奈的回避案。(刘高峰）
			//否则打开修改对话框，不做任何更改直接submit,这时再触发远程校验，耗时较长，
			//submit函数在等待远程校验结果然后再提交，而layer对话框不会阻塞会直接关闭同时会销毁表单，因此submit没有提交就被销毁了导致提交表单失败。
			$inputForm.validate().element($("#loginName"));
		});
	</script>
</head>
<body>
	<form:form id="inputForm" modelAttribute="user" action="${ctx}/sys/user/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<table class="table">
		   <tbody>
               <tr>
                   <td class="active"><label class="pull-right"><span style="color: red; ">*</span>头像：</label></td>
                   <td>
                       <input type="hidden" name="photo" id="image_id" value="${user.photo }"/>
                       <div>
                           <img src="<c:if test="${empty user.photo}">${ctxStatic}/static/app/image/default.jpg</c:if><c:if test="${not empty user.photo}">${fns:getFileAccessPath()}/${user.photo}</c:if>"
                                id="thumbImg" style="max-width: 100px; max-height: 100px; line-height: 20px;"/>
                       </div>
                       <input type="file" class="default" name="upload" onchange="imgUpload(this.value);" id="fileId" style="border: none; padding: 0;"/>
                   </td>
                   <td class="active"><label class="pull-right"><span style="color: red; ">*</span>用户角色:</label></td>
                   <td>
                       <div class="checkboxdiv"><form:checkboxes path="roleIdList" items="${fns:getRoleList()}"
                                                                 itemLabel="name" itemValue="id" htmlEscape="false"
                                                                 cssClass="i-checks required"/></div>
                       <label id="roleIdList-error" class="error" for="roleIdList"
                              style="display: inline-block;"></label>
                   </td>
               </tr>
		      <tr>
		         <td class="width-15 active"><label class="pull-right"><span style="color: red; ">*</span>归属公司：</label></td>
		         <td class="width-35">
                     <sys:treeselect id="companyId" name="companyId" value="${user.companyId}" labelName="companyName" labelValue="${user.companyName}"
                                     title="公司" url="/sys/office/treeData?type=1" cssClass="form-control required"/>
				 </td>
                  <td class="width-15 active"><label class="pull-right"><span style="color: red; ">*</span>归属部门：</label></td>
                  <td>
                      <sys:treeselect id="officeId" name="officeId" value="${user.officeId}" labelName="officeName" labelValue="${user.officeName}"
                                      title="部门" url="/sys/office/treeData?type=2" cssClass="form-control required" notAllowSelectParent="true"/>
                  </td>
              </tr>

		      <tr>
				  <td class="active"><label class="pull-right"><span style="color: red; ">*</span>登录名:</label></td>
				  <td><input id="oldLoginName" name="oldLoginName" type="hidden" value="${user.loginName}">
					  <form:input path="loginName" htmlEscape="false" maxlength="20" class="form-control required"/></td>
				  <td class="active"><label class="pull-right"><span style="color: red; ">*</span>姓名:</label></td>
				  <td><form:input path="name" htmlEscape="false" maxlength="20" class="form-control required"/></td>
		      </tr>

		      <tr>
		         <td class="active"><label class="pull-right">密码:</label></td>
		         <td><input id="password" name="password" type="password" value="" maxlength="20" minlength="3" class="form-control"/>
                     <c:if test="${empty user.id}"><span class="help-inline">不填则自动设置默认密码为“123456”。</span></c:if>
					 <c:if test="${not empty user.id}"><span class="help-inline">若不修改密码，请留空。</span></c:if></td>
		         <td class="active"><label class="pull-right">确认密码:</label></td>
		         <td><input id="confirmPassword" name="confirmPassword" type="password"  class="form-control" value="" maxlength="50" minlength="3" equalTo="#password"/></td>
		      </tr>

			  <tr>
				  <td class="active"><label class="pull-right"><span style="color: red; ">*</span>用户类型:</label></td>
				  <td>
					  <form:select path="userType" class="form-control required">
						  <form:option value="" label="请选择"/>
						  <form:options items="${fns:getDictList('sys_user_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					  </form:select>
				  </td>
			  </tr>

		       <tr>
		         <td class="active"><label class="pull-right"><span style="color: red; ">*</span>邮箱:</label></td>
		         <td><form:input path="email" htmlEscape="false" maxlength="100" class="form-control required email"/></td>
		         <td class="active"><label class="pull-right"><span style="color: red; ">*</span>手机号:</label></td>
		         <td><form:input path="mobile" htmlEscape="false" maxlength="11" class="form-control required mobile"/></td>
		      </tr>

		       <tr>
		         <td class="active"><label class="pull-right">描述:</label></td>
		         <td colspan="3"><form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="200" class="form-control"/></td>
		       </tr>

		      <c:if test="${not empty user.id}">
		       <tr>
		         <td class=""><label class="pull-right">创建时间:</label></td>
		         <td><span class="lbl"><fmt:formatDate value="${user.createDate}" type="both" dateStyle="full"/></span></td>
		      </tr>
		     </c:if>

		   </tbody>
		</table>
	</form:form>
</body>
</html>