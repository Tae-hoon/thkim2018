<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%-- <jsp:useBean id="boardService" class="egovframework.example.board.service.BoardService" scope="page" /> --%>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="egovframework.example.board.service.*" %>
<%@ page import="egovframework.example.board.service.impl.*" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%
	
	ServletContext servletContext = this.getServletContext();
	WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

	BoardService boardService = (BoardService)wac.getBean("boardService");
		
	String mode = request.getParameter("mode");
	String idx = request.getParameter("idx");
	String title = request.getParameter("title");
	String contents = request.getParameter("contents");
	String writer = request.getParameter("writer");
	String indate = request.getParameter("indate");
	
	System.out.println("idx:"+idx);
	System.out.println("title:"+title);
	System.out.println("contents:"+contents);
	System.out.println("writer:"+writer);
	System.out.println("indate:"+indate);
	
	BoardVO boardVO = new BoardVO();
	boardVO.setIdx(idx);
	boardVO.setTitle(title);
	boardVO.setContents(contents);
	boardVO.setWriter(writer);
	boardVO.setIndate(indate);
	
	if( "add".equals(mode) ){
		boardService.insertBoard(boardVO);
	}else if( "mod".equals(mode) ){
		boardService.updateBoard(boardVO);
	}
%>
<script type="text/javaScript" language="javascript" defer="defer">
//alert("aaaa");
location.href = "<c:url value='/list.do'/>?pageIndex=1";
</script>