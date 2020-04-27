<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>boardMongo</title>
<link rel="stylesheet" href="<c:url value='/resources/css/bootstrap.min.css'/>">
<script src="<c:url value='/resources/js/jquery-3.4.1.min.js'/>"></script>
<script src="<c:url value='/resources/js/bootstrap.min.js'/>"></script>
<script type="text/javaScript" language="javascript" defer="defer">
$( document ).ready(function() {
	list();
});

function list(){
	$.ajax({
		  url: "<c:url value='/list.do'/>",
		  processData: false,
		  contentType: false,
		  method: "GET",
		  cache: false,
		  data: ''
	})
	.done(function( data ) {
		//alert(data.list.length);
		$('#list').children().remove();
		for(var i=0; i<data.list.length; i++){
			var contents = data.list[i].contents;
			contents = contents.replace(/\n/gi,'\\n');
			var txt = "<tr onclick=\"detail('"+data.list[i].id+"','"+data.list[i].title+"','"+contents+"','"+data.list[i].fname+"');\">";
	        txt += "<td>"+ data.list[i].title +"<span style=\"float:right\">"+ data.list[i].date +"</span></td>";
	      	txt += "</tr>";
	      	$('#list').append(txt);
	      	
	      	if( i == 0 ){
				detail(data.list[i].id, data.list[i].title, contents.replace(/\\n/gi,'\n'), data.list[i].fname);
			}
		}
	})
	.fail(function( jqXHR, textStatus, errorThrown ) {
		alert("오류");
	});
}

function detail(id, title, contents, fname){
	//alert(title);
	//alert(contents);
	$('#id').val(id);
	$('#title').val(title);
	$('#contents').val(contents);
	$('#file').val('');
	//alert(fname);
	setTimeout(getImg(fname),500);
}
function getImg(fname){
	//alert(fname);
	
	$.ajax({
		  url: "<c:url value='/img2.do'/>?fname="+encodeURI(fname),
		  processData: false,
		  contentType: false,
		  method: "GET",
		  cache: false,
		  data: ''
	})
	.done(function( data ) {
		if( data == "" ){
			$('#img').html("");
		}else{
			$('#img').html(data);
		}
	})
	.fail(function( jqXHR, textStatus, errorThrown ) {
		alert("오류");
	});
}
function save(){
	//alert('save');
	if( !confirm("저장하시겠습니까?") ){
		return;
	}
	
	var formData = new FormData();
	formData.append('id', $('#id').val());
	formData.append('title', $('#title').val());
	formData.append('contents', $('#contents').val());
	for(var i=0; i<$('#file')[0].files.length; i++){
		formData.append('file', $('#file')[0].files[i]);
	}
	
	var url = "<c:url value='/add2.do'/>";
	if( $('#id').val() == '' ){
		url = "<c:url value='/add2.do'/>";
	}else{
		url = "<c:url value='/mod2.do'/>";
	}
		
	$.ajax({
		  url: url,
		  processData: false,
		  contentType: false,
		  method: "POST",
		  cache: false,
		  //data: $('#form1').serialize()
		  data: formData
	})
	.done(function( data ) {
		if(data.returnCode == 'success'){
			list();
		}else{
			alert(data.returnDesc);
		}
	})
	.fail(function( jqXHR, textStatus, errorThrown ) {
		alert("오류");
	});
}
function cancel(){
	//alert('cancel');
	$('#id').val('');
	$('#title').val('');
	$('#contents').val('');
	$('#img').html("");
}
function del(){
	//alert('del');
	if( $('#id').val() == '' ){
		alert("삭제할 데이터가 없습니다.");
	}
	if( !confirm("삭제하시겠습니까?") ){
		return;
	}
	
	var formData = new FormData();
	formData.append('id', $('#id').val());
	formData.append('title', $('#title').val());
	formData.append('contents', $('#contents').val());
	
	$.ajax({
		  url: "<c:url value='/del.do'/>",
		  processData: false,
		  contentType: false,
		  method: "POST",
		  cache: false,
		  //data: $('#form1').serialize()
		  data: formData
	})
	.done(function( data ) {
		if(data.returnCode == 'success'){
			list();
		}else{
			alert(data.returnDesc);
		}
	})
	.fail(function( jqXHR, textStatus, errorThrown ) {
		alert("오류");
	});
}
function delimg(){
//alert('delimg');
	
	if( $('#id').val() == '' ){
		alert("삭제할 데이터가 없습니다.");
	}
	if( !confirm("그림을 삭제하시겠습니까?") ){
		return;
	}
	
	var formData = new FormData();
	formData.append('id', $('#id').val());
	formData.append('title', $('#title').val());
	formData.append('contents', $('#contents').val());
	
	$.ajax({
		  url: "<c:url value='/delimg.do'/>",
		  processData: false,
		  contentType: false,
		  method: "POST",
		  cache: false,
		  //data: $('#form1').serialize()
		  data: formData
	})
	.done(function( data ) {
		if(data.returnCode == 'success'){
			list();
		}else{
			alert(data.returnDesc);
		}
	})
	.fail(function( jqXHR, textStatus, errorThrown ) {
		alert("오류");
	});
}
</script>
</head>
<body>
<div class="card">
	<h1>jsp board</h1>
	<div class="card-header">
		<ul class="nav nav-tabs">
		  <li class="nav-item">
		    <a class="nav-link" href="<c:url value='/board.do'/>">싱글이미지 게시판</a>
		  </li>
		  <li class="nav-item">
		    <a class="nav-link active" href="<c:url value='/board2.do'/>">멀티이미지 게시판</a>
		  </li>
		</ul>
	</div>
    <div class="card-body">
    	<div class="row">
			<div class="col-lg-4">
				<div class="card" style="min-height:500px;max-height:1000px">
					<table class="table">
					    <thead>
					      <tr>
					        <th>게시물 리스트</th>
					      </tr>
					    </thead>
					    <tbody id="list">
					    </tbody>
					  </table>
				</div>
			</div>
			<div class="col-lg-5">
				<div class="card bg-light text-dark" style="min-height:500px;max-height:1000px">
					<form id="form1" name="form1" action="">
					  <div class="form-group">
					    <label class="control-label" for="title">제목:</label>
					    <div>
					      <input type="text" class="form-control" id="title" placeholder="제목을 입력하세요">
					    </div>
					  </div>
					  <div class="form-group">
					    <label class="control-label" for="contents">내용:</label>
					    <div> 
					      <textarea class="form-control" rows="10" id="contents"></textarea>
					    </div>
					  </div>
					  <div className="form-group">
							<label className="control-label">이미지첨부: jpg,gif,png</label>
							<div>
							    <input type="file" className="form-control" multiple id="file" name="file" style="width:90%" />
							</div>
					  </div>
					  <input type="hidden" id="id" name="id" />
					</form>
					<div style="text-align:center">
						<div class="btn-group">
						  <button type="button" class="btn btn-primary" onclick="save()">저장</button>
						  <button type="button" class="btn btn-secondary" onclick="cancel()">취소</button>
						  <button type="button" class="btn btn-danger" onclick="del()">삭제</button>
						  <button type="button" class="btn btn-info" onclick="delimg()">그림삭제</button>
						</div>
					</div>
				</div>
			</div>
			<div class="col-lg-3">
				<div class="card bg-light text-dark" style="min-height:500px;max-height:1000px">
					<span id="img"></span>
				</div>
			</div>
		</div>
    </div>
    <div class="card-footer">SpringBoot + MongoDB + jquery + bootstrap4 게시판 만들기</div>
</div>
</body>
</html>