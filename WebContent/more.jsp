<%@ page import="java.util.*" %> 
<%@ page import="common0503.Result" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
	int userid = Integer.parseInt(session.getAttribute("user").toString());
	Map<String,String> map=null;
	Result rt = new Result();
	try{
		map = rt.get1MovieRating(userid);
	}catch(Exception e){
		e.printStackTrace();
	}
	int h = map.size()/4;//每行显示5部电影
	if(map.size()%4>0){
		h++;
	}
	Iterator<Map.Entry<String,String>> iter = map.entrySet().iterator();	
	Map.Entry<String,String> entry = iter.next();
%>        
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>查看更多</title>
<link type="text/css" rel="stylesheet" href="css/reset.css">
<link type="text/css" rel="stylesheet" href="css/main.css">
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

<div class="showBox">
	<table class="moretable">
		<tr><th>历史记录</th></tr>
			<%for(String movie:map.keySet()){%>						
		<tr onmouseover="this.style.backgroundColor='#fff';" onmouseout="this.style.backgroundColor='#c9cccd';">
				<td><a href="#"><%=movie%></a></td>
		</tr> 
			<%}%>		
	</table>
</div>


<div class="hr_25"></div>
<div class="footer">
	<p><a href="#">公司简介</a><i>|</i><a href="#">相关公告</a><i>|</i> <a href="#">招纳贤士</a><i>|</i><a href="#">联系我们</a><i>|</i>客服热线：123-456-7890</p>
	<p>Copyright &copy; 2006 - 2015 版权所有&nbsp;&nbsp;&nbsp;京ICP备09037834号&nbsp;&nbsp;&nbsp;京ICP证B1034-8373号&nbsp;&nbsp;&nbsp;某市公安局XX分局备案编号：123456789123</p>
	<p class="web"><a href="#"><img src="images/webLogo.jpg" alt="logo"></a><a href="#"><img src="images/webLogo.jpg" alt="logo"></a><a href="#"><img src="images/webLogo.jpg" alt="logo"></a><a href="#"><img src="images/webLogo.jpg" alt="logo"></a></p>
</div>

</body>
</html>