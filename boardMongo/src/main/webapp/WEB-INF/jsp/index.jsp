<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<jsp:forward page="/board.do"/>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Demo Project</title>
<script type="text/javaScript" language="javascript" defer="defer">
function main(){
	location.href = "/board.do";
}
setTimeout(main,3000);
</script>
</head>
<body>
<h1>스프링부트와 몽고디비를 이용한 게시판 만들기</h1>
<h2>안녕하세요. 반갑습니다. index.jsp입니다</h2>
<h3>3초후에 게시판으로 이동합니다.</h3>
</body>
</html>