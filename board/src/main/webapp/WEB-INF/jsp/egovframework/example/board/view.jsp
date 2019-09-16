<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ui"     uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
     //치환 변수 선언합니다.
     pageContext.setAttribute("crcn", "\r\n"); //Space, Enter
     pageContext.setAttribute("br", "<br/>"); //br 태그
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<title>Bootstrap Example</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="<c:url value='/css/bootstrap/css/bootstrap.min.css'/>">
<script src="<c:url value='/js/jquery-3.3.1.min.js'/>"></script>
<script src="<c:url value='/css/bootstrap/js/bootstrap.min.js'/>"></script>
<script type="text/javaScript" language="javascript" defer="defer">
function list(){
	location.href = "<c:url value='/list.do'/>";
}
function add(){
	if( $('#writer').val() == '' ){
		alert("작성자를 입력하세요");
		$('#writer').focus();
		return;
	}
	if( $('#reply').val() == '' ){
		alert("댓글을 입력하세요");
		$('#reply').focus();
		return;
	}
	
	if( !confirm("댓글을 작성하시겠습니까?") ){
		return;
	}
	
	document.form2.action = "<c:url value='/reply.do'/>?idx=${boardVO.idx}";
	document.form2.submit();
}
function mod(){
	location.href = "<c:url value='/mgmt.do'/>?idx=${boardVO.idx}";
}
function del(){
	
	var cnt = ${fn:length(resultList)};
	//alert(cnt);
	if( cnt > 0 ){
		alert("댓글이 있는 게시물은 삭제할 수 없습니다.");
		return;
	}
	
	if( !confirm("삭제하시겠습니까?") ){
		return;
	}
	
	document.form1.action = "<c:url value='/mgmt.do'/>?mode=del&idx=${boardVO.idx}";
	document.form1.submit();
}
function getfile(filename){
	//alert("<c:url value='/fileGet.do'/>?filename="+encodeURI(filename));
	location.href = "<c:url value='/fileGet.do'/>?filename="+filename;
}
function delfile(seq,filename){
	
	if( !confirm("첨부파일을 삭제하시겠습니까?") ){
		return;
	}
	
	location.href = "<c:url value='/fileDel.do'/>?idx=${boardVO.idx}&seq="+seq+"&filename="+filename;
}
</script>
</head>
<body>

<div class="container">
  <h1>상세화면</h1>
  <div class="panel panel-default">
    <div class="panel-heading">
      <label for="">${sessionScope.userName }님이 로그인 하셨습니다</label>
    </div>
    <div class="panel-body">
      <form id="form1" name="form1" class="form-horizontal" method="post" action="">
      <div class="form-group">
	    <label class="control-label col-sm-2" for="">게시물아이디:</label>
	    <div class="col-sm-10 control-label" style="text-align:left;">
	      <c:out value="${boardVO.idx}"/>
	    </div>
	  </div>
	  <div class="form-group">
	    <label class="control-label col-sm-2" for="">제목:</label>
	    <div class="col-sm-10 control-label" style="text-align:left;">
	      <c:out value="${boardVO.title}"/>
	    </div>
	  </div>
	  <div class="form-group">
	    <label class="control-label col-sm-2" for="">등록자/등록일:</label>
	    <div class="col-sm-10 control-label" style="text-align:left;">
	      <c:out value="${boardVO.writerNm}"/> / <c:out value="${fn:substring(boardVO.indate,0,fn:length(boardVO.indate)-2)}"/>
	    </div>
	  </div>
	  <div class="form-group">
	    <label class="control-label col-sm-2" for="">내용:</label>
	    <div class="col-sm-10 control-label" style="text-align:left;">
	      <c:out value="${fn:replace(boardVO.contents, crcn, br)}" escapeXml="false"/>
	    </div>
	  </div>
	  <div class="form-group">
	    <label class="control-label col-sm-2" for="">파일:</label>
	    <div class="col-sm-10 control-label" style="text-align:left;">
	      <a href="javascript:getfile('${boardVO.filename}');"><c:out value="${boardVO.filename}" escapeXml="false"/></a>
	      &nbsp;
	      <c:if test="${!empty sessionScope.userId && sessionScope.userId == boardVO.writer && boardVO.filename != null && boardVO.filename != '' }">
	      <a href="javascript:delfile('${boardVO.seq}','${boardVO.filename}');"><span class="glyphicon glyphicon-remove"></span></a>
	      </c:if>
	    </div>
	  </div>
	  </form>
    </div>
    <div class="panel-footer">
    <c:if test="${!empty sessionScope.userId && sessionScope.userId == boardVO.writer }">
      <button type="button" class="btn btn-default" onclick="mod();">수정</button>
      <button type="button" class="btn btn-default" onclick="del();">삭제</button>
    </c:if>
      <button type="button" class="btn btn-default" onclick="list();">목록</button>
    </div>
  </div>
  <c:forEach var="result" items="${resultList}" varStatus="status">
  <div class="well well-sm">
  	<c:out value="${result.writer}"/> / <fmt:formatDate value="${result.indate}" pattern="yyyy-MM-dd hh:mm:ss"/><br/>
  	<c:out value="${fn:replace(result.reply, crcn, br)}" escapeXml="false"/>
  </div>
  </c:forEach>
  <div class="well well-lg">
    <form id="form2" name="form2" class="form-horizontal" method="post" action="">
	  <div class="form-group">
	    <label class="control-label col-sm-2" for="">작성자/작성일:</label>
	    <div class="col-sm-10 control-label" style="text-align:left;">
	      <input type="text" class="form-control" id="writer" name="writer" placeholder="작성자를 입력하세요" maxlength="15" style="float:left;width:30%" value="${sessionScope.userName }">
	      <%-- <input type="text" class="form-control" id="indate" name="indate" placeholder="작성일을 입력하세요" maxlength="10" style="float:left;width:30%" readonly value="${strToday }"> --%>
	    </div>
	  </div>
	  <div class="form-group">
	    <label class="control-label col-sm-2" for="reply">댓글:</label>
	    <div class="col-sm-10"> 
	      <textarea class="form-control" rows="3" id="reply" name="reply" maxlength="300"></textarea>
	    </div>
	  </div>
	  <button type="button" class="btn btn-default" onclick="add();">작성</button>
	  * 댓글은 수정이나 삭제를 할 수 업습니다.
	</form>
  </div>
</div>

</body>
</html>