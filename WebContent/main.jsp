<%@ page import="java.util.*" %> 
<%@ page import="common0503.Result" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<%
	int userid = Integer.parseInt(session.getAttribute("user").toString());
//	int userid = 1;
	Map<String,String> map=null;
	Map<String,String> map1=null;
	Map<String,String> map2=null;
	Result rt = new Result();
	try{
		map = rt.get1MovieRating(userid);
		map1 = rt.IselectRecByUID(userid);
		map2 = rt.UselectRecByUID(userid);
	}catch(Exception e){
		e.printStackTrace();
	}
%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>success</title>
<link type="text/css" rel="stylesheet" href="css/reset.css">
<link type="text/css" rel="stylesheet" href="css/main.css">

<link type="text/css" rel="stylesheet" href="css/jquery.slideBox.css"/>
<script type="text/javascript" src="js/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="js/jquery.slideBox.js"></script>

<link rel="stylesheet" type="text/css" href="template/ue-content/templates/images/ue_grid.css" />
<link rel="stylesheet" type="text/css" href="template/ue-content/templates/images/style.css" />
<link rel="stylesheet" type="text/css" href="template/ue-content/templates/css/style.css" />
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js" type="text/javascript"></script>
<script language="javascript" src="script/jquery.easing.min.js"></script>
<script language="javascript" src="script/custom.js"></script>

<script>
jQuery(function($){
	$('#demo').slideBox({
		duration : 0.3,//滚动持续时间，单位：秒
		easing : 'linear',//swing,linear//滚动特效
		delay : 3,//滚动延迟时间，单位：秒
		hideClickBar : false,//不自动隐藏点选按键
		clickBarRadius : 10
	});
});
</script>
</head>

<body>
<div class="headBar">
	<div class=" comWidth">
	<div class="fl">
		<h3 class="welcome_title">movieRec</h3>
	</div>
	<div class="nav fl">
		<span>导航</span>
	</div>
	<div class="search_box fl">
		<input type="text" class="search_text fl">
		<input type="button" value="搜 索" class="search_btn fr">
	</div>
	<div class="user fl">
		用户<%=userid %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

	</div>			
	</div>
</div>

<div class="imgBox comWidth">
		<ul>
			<li><a href="#" title="speed7"><img src="images/banner/speed7.jpg" alt="speed7"></a></li>
		</ul>
</div>
<div class="TList comWidth">
	<div class="movieList">
		<table class="hovertable"> 
			<tr><th>历史记录</th></tr>
			<%int i=1;%>
			<%for(String movie:map.keySet()){
				if(i<=19){
			%>						
			<tr onmouseover="this.style.backgroundColor='#fff';" onmouseout="this.style.backgroundColor='#c9cccd';">
				<td><a href="#"><%=movie%></a></td>
			</tr> 
			<%i++;}else{break;}}%>
			<tr>
				<td><a href="more.jsp" target="_blank">查看更多>></a></td>
			</tr>					 
		</table>	
	</div>
	<div class="movieList">
		<table class="hovertable"> 
			<tr><th>猜你喜欢</th></tr>
			<%for(String movie:map1.keySet()){%>						
			<tr onmouseover="this.style.backgroundColor='#fff';" onmouseout="this.style.backgroundColor='#c9cccd';">
				<td><a href="#"><%=movie%></a></td>
			</tr> 
			<%}%>						 
		</table>
	</div>
	<div class="movieList">
		<table class="hovertable"> 
			<tr><th>其他人还看了</th></tr>
			<%for(String movie:map2.keySet()){%>						
			<tr onmouseover="this.style.backgroundColor='#fff';" onmouseout="this.style.backgroundColor='#c9cccd';">
				<td><a href="#"><%=movie%></a></td>
			</tr> 
			<%}%>						 
		</table>
	</div>
</div>


<div class="hr_25"></div>
<div class="footer">
	<p><a href="#">公司简介</a><i>|</i><a href="#">相关公告</a><i>|</i> <a href="#">招纳贤士</a><i>|</i><a href="#">联系我们</a><i>|</i>客服热线：123-456-7890</p>
	<p>Copyright &copy; 2006 - 2015 版权所有&nbsp;&nbsp;&nbsp;京ICP备09037834号&nbsp;&nbsp;&nbsp;京ICP证B1034-8373号&nbsp;&nbsp;&nbsp;某市公安局XX分局备案编号：123456789123</p>
	<p class="web"><a href="#"><img src="images/webLogo.jpg" alt="logo"></a><a href="#"><img src="images/webLogo.jpg" alt="logo"></a><a href="#"><img src="images/webLogo.jpg" alt="logo"></a><a href="#"><img src="images/webLogo.jpg" alt="logo"></a></p>
</div>


</body>
</html>