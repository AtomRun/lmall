<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<html>
<head>
    <meta charset="UTF-8">
</head>
<body>
<h2>Hello World!</h2>

springMVC上传文件
<form action="/manage/product/upload.do" name="form1" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file">
    <input type="submit" value="上传文件 ">
</form>

富文本图片上传文件
<form action="/manage/product/richtext_img_upload.do" name="form1" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file">
    <input type="submit" value="上传文件 ">
</form>
</body>
</html>
