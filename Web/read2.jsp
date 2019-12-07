<%@ page language="java" import="java.util.*,java.io.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html>
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>煤矿冲击地压震级统计</title>
	
	<link rel="stylesheet" type="text/css" href="css/head.css">
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">	
	<link rel="stylesheet" type="text/css" href="css/index2.css">
	<script type="text/javascript" src="My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="js/jquery-3.2.1.min.js"></script>	
  </head>
  <body>
  	<div class="head">
        <a href="index.jsp"><img src="D:/Procedure/KS_红阳三矿_1.0.5\WebRoot/image/152569275640874.png"></a>
        <a href="jsp/MyJsp5.jsp">实时数据</a>
        <a href="read2.jsp">微震统计</a>
       <!--  <a href="search.jsp">特征查询</a> -->
        <a href="jsp/MyJsp4.jsp">煤矿微震波形</a>
    </div>
    <div class="searchBox">
        <div class="radio">
        </div>
        <div class="test">
             
            <label>
               	  <input id="sdate" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true,maxDate:'%y-%M-%d'})"/>&nbsp;-&nbsp;<input id="edate" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true,minDate:'#F{$dp.$D(\'sdate\')}',maxDate:'%y-%M-%d'})"/>
            	
            </label>
            <label>
                	震级在<input type="text" id="n1" value="0">级以上
            </label>
        </div>

        <div class="searchbtn">
            <button type="button" class="btn btn-success" onclick="DoCmd(0)">查找</button>
            <button type="button" class="btn btn-success" onclick="DoCmd(1)">清除</button>            
        </div>
    </div>
    <div class="cad">
    	<object classid="clsid:74A777F8-7A8F-4e7c-AF47-7074828086E2"
			id="MxDrawXCtrl">
			<param name="_Version" value="65536">
			<param name="_ExtentX" value="24262">
			<param name="_ExtentY" value="16219">
			<param name="_StockProps" value="0">
			<param name="DwgFilePath"
				value="D:\Procedure\KS_红阳三矿_1.0.5\WebRoot\image\红阳三矿.dwg">
			<param name="ViewColor" value="16777215">
			<param name="IsRuningAtIE" value="1">
			<param name="EnablePrintCmd" value="1">
			<param name ="EnableIntelliSelect"  value="0">
			<param name="ShowCommandWindow" value="0">
			<param name="ShowModelBar" value="0">
			<param name="FirstRunPan" value="1">
			<param name="Iniset"
				value="AutoActive=N,EnableGripPoint=N,UseArrowCursor=Y,AutoZoomAll=Y,IintAnimation=Y">
			<param name="ToolBarFiles" value="">
			<param name="IsBrowner" value="1">
			<param name="ShowMenuBar" value="0">
			<param name="EnableUndo" value="0">
		</object>
    </div>
	<script>
	$.ajaxSetup({cache:false});
	$.ajaxSetup({async:false});
	var mxOcx = document.getElementById("MxDrawXCtrl");
	var currentPath="D:/Procedure/KS_红阳三矿_1.0.5/WebRoot/image/";
	function DoCmd(iCmd) {
		mxOcx.DoCommand(iCmd);
	}
	function DoCommandEventFunc(iCmd) {
		if (iCmd == 0) {
			InsertImage();
		}
		if (iCmd == 1) {
			DeleteImage();
		}
	}
	function InitMxDrawX() {
		if (mxOcx) {
			if (!mxOcx.IsIniting()) {
				clearInterval(mxtime);
				// 控件初始化完成，需要在启动做的事，在这里做

				//mxOcx.setCurrentLayout("布局名")

			}
		}
	}

	document.getElementById("MxDrawXCtrl").ImplementCommandEventFun = DoCommandEventFunc;
	document.getElementById("MxDrawXCtrl").ImplementMouseEventFun = MouseEvent;
	function InsertImage() {
		 DeleteImage();
			 var t1=document.getElementById("sdate").value;
             var t2=document.getElementById("edate").value;
              var quacklevel=document.getElementById("n1").value;
             if(t1=="" || t2==""){
             	alert("搜索框不能为空");
               	return false;
              }
             var x=new Array();
             var y=new Array();
             var n=new Array();
             $.get("Serv1",{SD:t1,CD:t2,level:quacklevel},function(data,status){
            	//alert(data); 
            	if(data==""){
            		alert("该时间段没有记录");
            		return false;
            	}
            	
            	var data1=data.split("\n");
            	for(var i=0;i<data1.length-1;i++){
					 var data2=data1[i].split(" ");
					 x.push(data2[0]);
	         		 y.push(data2[1]);
	         		 n.push(data2[2]);
				 }
             });                      
             //alert(x+"@@@@"+y);
             for(var i=0;i<x.length;i++){
            	 var m=0.15*n[i]+0.2; 
				 var sImageFile = currentPath+"pic31.png";
				 var sImageFile1 = currentPath+"pic32.png";
	    		
	    		 var o= mxOcx.DrawImageMark(x[i],y[i],m, 0.0, sImageFile, sImageFile1+","+sImageFile, false);
	    	     mxOcx.TwinkeEnt(o);//开启图片闪烁功能
	    	     var ent = mxOcx.ObjectIdToObject(o);//将o转换为IMxDrawEntity类型对象，为了将图片插入到顶层
	    	     var res = mxOcx.NewResbuf();//新建图层
	    	     res.AddLong(2147403647);//设置图层高度
	    	     ent.SetProp("drawOrder",res);//将图片插入到新图层
	    	     }
			 
	}
	function DeleteImage(){
		var ssGet = mxOcx.NewSelectionSet();
        var filter = mxOcx.NewResbuf();
        filter.AddStringEx("MxImageMark", 5020);

        ssGet.AllSelect(filter);

        for (var i = 0; i < ssGet.Count; i++)
        {
            ssGet.Item(i).Erase();
        }
	}
	
	function InsertSensors(){
		var sensorX=new Array();
		var sensorY=new Array();
		var sensorZ=new Array();
		var sImageFile = currentPath+"sensors.png";
		var sImageFile2 = currentPath+"sensors1.png";
		sensorX[0]=41519304.125;sensorY[0]=4595913.485;
		sensorX[1]=41519926.476;sensorY[1]=4597275.978;
		sensorX[2]=41520815.875;sensorY[2]=4597384.576;
		sensorX[3]=41520207.356;sensorY[3]=4597983.404;
		sensorX[4]=41516849.629;sensorY[4]=4598099.366;
		sensorX[5]=41516836.655;sensorY[5]=4596627.472;
		sensorX[6]=41516707.440;sensorY[6]=4593163.619;
		sensorX[7]=41518060.298;sensorY[7]=4594304.927;
		sensorX[8]=41518099.807;sensorY[8]=4595388.504;
	
		for(var i=0;i<sensorX.length;i++){
			o = mxOcx.DrawImageMark(sensorX[i],sensorY[i],0.1, 0.0, sImageFile,sImageFile+","+sImageFile2, false);
			mxOcx.TwinkeEnt(o);//开启图片闪烁功能
            var ent = mxOcx.ObjectIdToObject(o);//将o转换为IMxDrawEntity类型对象，为了将图片插入到顶层
            var res = mxOcx.NewResbuf();//新建图层
            res.AddLong(2147403647);//设置图层高度
            ent.SetProp("drawOrder",res);//将图片插入到新图层
        }
	}
    function MouseEvent(dX,dY,lType)
	{
		if(lType==2)
			InsertSensors();
	}
	
	</script>
	<script>
	$(function(){
        $("#nums").click(function(){
            $("#n1").attr("disabled",false);
            $("#edate").attr("disabled",true);
            $("#sdate").attr("disabled",true);
        })
        $("#dates").click(function(){
            $("#n1").attr("disabled",true);
            $("#edate").attr("disabled",false);
            $("#sdate").attr("disabled",false);
        })
    })
    
</script>
  </body>
</html>
